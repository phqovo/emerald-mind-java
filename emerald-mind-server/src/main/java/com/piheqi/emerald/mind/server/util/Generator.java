package com.piheqi.emerald.mind.server.util;

/**
 * @Author: PiHeQi
 * @Date: 2024/1/8 15:35
 * @Description: Generator
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Generator {
    public static void main(String[] args) {
        code();
    }

    public static void code() {
        FastAutoGenerator.create("jdbc:mysql://103.133.178.118:3306/chatgpt?useUnicode=true&characterEncoding=UTF-8&useSSL=false&remarks=true&useInformationSchema=true&serverTimezone=Asia/Shanghai", "root", "Qaz23069875.")
                .globalConfig(builder -> {
                    builder.author("PiHeQi") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .disableOpenDir()//禁止打开输出文件
                            .outputDir("/Users/piheqi/IdeaProjects/emerald-mind/emerald-mind-gpt/src/main/java/"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("com.piheqi.emerald.mind") // 设置父包名
                            .moduleName("chatgpt") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "emerald-mind-gpt/src/main/resources/mapper")); // 设置mapperXml生成路径
                })
                .strategyConfig((scanner,builder) -> {
                    builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all"))) // 设置需要生成的表名
                            .addTablePrefix("t_")// 设置过滤表前缀
                            // Entity 策略配置
                            .entityBuilder()
                            .enableLombok() //开启 Lombok
                            .enableFileOverride() // 覆盖已生成文件
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)    //数据库表字段映射到实体的命名策略：下划线转驼峰命
                            .enableTableFieldAnnotation()
                            // 开启生成实体时生成字段注解
                            // Mapper 策略配置
                            .mapperBuilder()
                            .superClass(BaseMapper.class)   //设置父类
                            .formatMapperFileName("%sMapper")   //格式化 mapper 文件名称
                            .enableMapperAnnotation()       //开启 @Mapper 注解
                            .formatXmlFileName("%sXml") //格式化 Xml 文件名称 如 UserXml
                            .enableFileOverride() // 覆盖已生成文件
                            // Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride() // 覆盖已生成文件
                            .formatServiceFileName("%sService") // 如:UserService
                            .formatServiceImplFileName("%sServiceImpl") // 如:UserServiceImpl
                            .formatServiceFileName("%sService") //格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                            .formatServiceImplFileName("%sServiceImpl") //格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl
                            // Controller 策略配置
                            .controllerBuilder()
                            .formatFileName("%sController") // 如 UserController
                            .enableRestStyle() //开启生成 @RestController 控制器
                            .enableFileOverride();// 覆盖已生成文件
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }

    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}

