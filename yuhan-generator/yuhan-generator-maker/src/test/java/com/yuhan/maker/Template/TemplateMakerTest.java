package com.yuhan.maker.Template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yuhan.maker.Template.enums.FileFilterRangeEnum;
import com.yuhan.maker.Template.enums.FileFilterRuleEnum;
import com.yuhan.maker.Template.model.FileFilterConfig;
import com.yuhan.maker.Template.model.TemplateMakerConfig;
import com.yuhan.maker.Template.model.TemplateMakerFileConfig;
import com.yuhan.maker.Template.model.TemplateMakerModelConfig;
import com.yuhan.maker.meta.Meta;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class TemplateMakerTest {
    @Test
    public void testMakeTemplateBug1() {
        //项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-pro-generator");
        meta.setDescription("ACM示例模板生成器");

        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent()+File.separator+"yuhan-generator-demo-projects/springboot-init-master";

        //要挖坑的文件
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";
//        String fileInputPath2 = "src/main/resources/application.yml";
        //输入模型参数信息
//        Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");

        //输入模型参数信息（第二次）
        Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        //过滤
        //模板制作文件配置信息（带过滤器）
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
//        //过滤信息对象
//        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
//                .range(FileFilterRangeEnum.FILE_NAME.getValue())
//                .rule(FileFilterRuleEnum.CONTAINS.getValue())
//                .value("Base")
//                .build();
//
//        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();//过滤信息列表
//        fileFilterConfigList.add(fileFilterConfig);//仅添加了一个过滤信息

        //1包含过滤信息的，文件信息配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
//        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

//        //2没有过滤信息的，文件信息配置
//        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
//        fileInfoConfig2.setPath(fileInputPath2);
        //文件信息配置列表
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1);
        templateMakerFileConfig.setFiles(fileInfoConfigList);//向“模板制作文件配置信息”中添加文件配置信息列表

        //文件分组
//        //文件分组信息配置
//        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
//        fileGroupConfig.setCondition("outputText2");
//        fileGroupConfig.setGroupKey("test2");
//        fileGroupConfig.setGroupName("测试分组2");
//        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        //模型配置
        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // - 模型组配置
//        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
//        modelGroupConfig.setGroupKey("mysql");
//        modelGroupConfig.setGroupName("数据库配置");
//        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

//        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
//        modelInfoConfig2.setFieldName("username");
//        modelInfoConfig2.setType("String");
//        modelInfoConfig2.setDefaultValue("root");
//        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,null,1887017206442098688L);
        System.out.println(id);
    }


    @Test
    public void testMakeTemplateBug2() {
        //项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-pro-generator");
        meta.setDescription("ACM示例模板生成器");

        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent()+File.separator+"yuhan-generator-demo-projects/springboot-init-master";

        //要挖坑的文件
        String fileInputPath1 = "./";
//        String fileInputPath2 = "src/main/resources/application.yml";
        //输入模型参数信息
//        Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");

        //输入模型参数信息（第二次）
        Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        //过滤
        //模板制作文件配置信息（带过滤器）
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
//        //过滤信息对象
//        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
//                .range(FileFilterRangeEnum.FILE_NAME.getValue())
//                .rule(FileFilterRuleEnum.CONTAINS.getValue())
//                .value("Base")
//                .build();
//
//        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();//过滤信息列表
//        fileFilterConfigList.add(fileFilterConfig);//仅添加了一个过滤信息

        //1包含过滤信息的，文件信息配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
//        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

//        //2没有过滤信息的，文件信息配置
//        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
//        fileInfoConfig2.setPath(fileInputPath2);
        //文件信息配置列表
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1);
        templateMakerFileConfig.setFiles(fileInfoConfigList);//向“模板制作文件配置信息”中添加文件配置信息列表

        //文件分组
//        //文件分组信息配置
//        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
//        fileGroupConfig.setCondition("outputText2");
//        fileGroupConfig.setGroupKey("test2");
//        fileGroupConfig.setGroupName("测试分组2");
//        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        //模型配置
        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // - 模型组配置
//        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
//        modelGroupConfig.setGroupKey("mysql");
//        modelGroupConfig.setGroupName("数据库配置");
//        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

//        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
//        modelInfoConfig2.setFieldName("username");
//        modelInfoConfig2.setType("String");
//        modelInfoConfig2.setDefaultValue("root");
//        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,null,1887017206442098688L);
        System.out.println(id);
    }


    @Test
    public void testMakeTemplateWithJSON(){
        String configStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println(id);
    }

    @Test
    public void makeSpringBootTemplate(){
        String rootPath = "examples/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("1");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("2");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker2.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("3");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker3.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("4");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker4.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("5");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("6");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker6.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("7");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker7.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("8");

        configStr = ResourceUtil.readUtf8Str(rootPath+"templateMaker8.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);
        System.out.println("9");


        System.out.println(id);
    }


}