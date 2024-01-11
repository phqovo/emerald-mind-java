package com.piheqi.emerald.mind.gpt.utils;

/**
 * @Author: PiHeQi
 * @Date: 2023/12/28 16:38
 * @Description: ChatGPTUtils
 */

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
public class ChatGPTUtils {

    /**
     * 修改为自己的密钥
     */
    private final String apiKey = "";


    public final String gptCompletionsUrl = "https://api.openai.com/v1/chat/completions";


    private static MediaType mediaType;
    private static Request.Builder requestBuilder;

private final static OkHttpClient client = new OkHttpClient();
    public final static Pattern contentPattern = Pattern.compile("\"content\":\"(.*?)\"}");
    /**
     * 对话符号
     */
    public final static String EVENT_DATA = "d";

    /**
     * 错误结束符号
     */
    public final static String EVENT_ERROR = "e";

    /**
     * 响应结束符号
     */
    public final static String END = "<<END>>";


    @PostConstruct
    public void init() {
        mediaType = MediaType.parse("application/json; charset=utf-8");
        requestBuilder = new Request.Builder()
                .url(gptCompletionsUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey);
    }


    /**
     * 流式对话
     *
     * @param talkList 上下文对话，最早的对话放在首位
     * @param callable 消费者，流式对话每次响应的内容
     */
    public GptChatResultDTO chatStream(List<ChatGptDTO> talkList, Consumer<String> callable) throws Exception {
        long start = System.currentTimeMillis();
        StringBuilder resp = new StringBuilder();
        Response response = chatStream(talkList);
        //解析对话内容
        try (ResponseBody responseBody = response.body();
             InputStream inputStream = responseBody.byteStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!org.springframework.util.StringUtils.hasLength(line)) {
                    continue;
                }
                Matcher matcher = contentPattern.matcher(line);
                if (matcher.find()) {
                    String content = matcher.group(1);
                    resp.append(content);
                    callable.accept(content);
                }

            }
        }
        int wordSize = 0;
        for (ChatGptDTO dto : talkList) {
            String content = dto.getContent();
            wordSize += content.toCharArray().length;
        }
        wordSize += resp.toString().toCharArray().length;
        long end = System.currentTimeMillis();
        return GptChatResultDTO.builder().resContent(resp.toString()).time(end - start).wordSize(wordSize).build();
    }

    /**
     * 流式对话
     *
     * @param talkList 上下文对话
     * @return 接口请求响应
     */
    private Response chatStream(List<ChatGptDTO> talkList) throws Exception {
        ChatStreamDTO chatStreamDTO = new ChatStreamDTO(talkList);
        RequestBody bodyOk = RequestBody.create(mediaType, chatStreamDTO.toString());
        Request requestOk = requestBuilder.post(bodyOk).build();
        Call call = client.newCall(requestOk);
        Response response;
        try {
            response = call.execute();
        } catch (IOException e) {
            throw new IOException("请求时IO异常: " + e.getMessage());
        }
        if (response.isSuccessful()) {
            return response;
        }
        try (ResponseBody body = response.body()) {
            if (429 == response.code()) {
                String msg = "Open Api key 已过期,msg: " + body.string();
                log.error(msg);
            }
            throw new RuntimeException("chat api 请求异常, code: " + response.code() + "body: " + body.string());
        }
    }


    private boolean sendToClient(String event, String data, SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name(event).data("{" + data + "}"));
            return true;
        } catch (IOException e) {
            log.error("向客户端发送消息时出现异常", e);
        }
        return false;
    }

    /**
     * 发送事件给客户端
     */
    public boolean sendData(String data, SseEmitter emitter) {
        if (StringUtils.isBlank(data)) {
            return true;
        }
        return sendToClient(EVENT_DATA, data, emitter);
    }

    /**
     * 发送结束事件,会关闭emitter
     */
    public void sendEnd(SseEmitter emitter) {
        try {
            sendToClient(EVENT_DATA, END, emitter);
        } finally {
            emitter.complete();
        }
    }


    /**
     * 发送异常事件,会关闭emitter
     */
    public void sendError(SseEmitter emitter) {
        try {
            sendToClient(EVENT_ERROR, "我累垮了", emitter);
        } finally {
            emitter.complete();
        }
    }


    /**
     * gpt请求结果
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GptChatResultDTO implements Serializable {
        /**
         * gpt请求返回的全部内容
         */
        private String resContent;

        /**
         * 上下文消耗的字数
         */
        private int wordSize;

        /**
         * 耗时
         */
        private long time;
    }


    /**
     * 连续对话DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatGptDTO implements Serializable {
        /**
         * 对话内容
         */
        private String content;

        /**
         * 角色 {@link GptRoleEnum}
         */
        private String role;
    }


    /**
     * gpt连续对话角色
     */
    @Getter
    public static enum GptRoleEnum {
        USER_ROLE("user", "用户"),
        GPT_ROLE("assistant", "ChatGPT本身"),

        /**
         * message里role为system，是为了让ChatGPT在对话过程中设定自己的行为
         * 可以理解为对话的设定，如你是谁，要什么语气、等级
         */
        SYSTEM_ROLE("system", "对话设定"),

        ;

        private final String value;
        private final String desc;

        GptRoleEnum(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }


    /**
     * gpt请求body
     */
    @Data
    public static class ChatStreamDTO {
        private static final String model = "gpt-3.5-turbo";
        private static final boolean stream = true;
        private List<ChatGptDTO> messages;


        public ChatStreamDTO(List<ChatGptDTO> messages) {
            this.messages = messages;
        }

        @Override
        public String toString() {
            return "{\"model\":\"" + model + "\"," +
                    "\"messages\":" + JSON.toJSONString(messages) + "," +
                    "\"stream\":" + stream + "}";
        }
    }

    public static void main(String[] args) throws Exception {
        ChatGPTUtils chatGptStreamUtil = new ChatGPTUtils();
        chatGptStreamUtil.init();

        //构建一个上下文对话情景
        List<ChatGptDTO> talkList = new ArrayList<>();
        //设定gpt
        talkList.add(ChatGptDTO.builder().content("你是chatgpt助手，能过帮助我查阅资料，编写教学报告。").role(GptRoleEnum.GPT_ROLE.getValue()).build());
        //开始提问
        talkList.add(ChatGptDTO.builder().content("请帮我写一篇小学数学加法运算教案").role(GptRoleEnum.USER_ROLE.getValue()).build());
        //这里是gpt每次流式返回的内容
        chatGptStreamUtil.chatStream(talkList, System.out::printf);
    }


}

