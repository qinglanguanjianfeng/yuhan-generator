package com.yuhan.generator;

import com.yuhan.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        //1.静态文件生成
        String projectPath = System.getProperty("user.dir");//获取当前项目下的根目录
        String inputPath = projectPath+ File.separator+"yuhan-generator-demo-projects"+ File.separator+"acm-template";//相对路径,获取不同系统的分隔符
        String outputPath =projectPath;
        StaticGenerator.copyFilesByRecursive(inputPath,outputPath);

        //2.动态文件生成

        String dynamicInputPath = projectPath + File.separator+"yuhan-generator-basic"+File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/yuhan/acm/MainTemplate.java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("yuhan");
        mainTemplateConfig.setLoop(false);
        mainTemplateConfig.setOutputText("求和结果：");
        DynamicGenerator.doGenerate(dynamicInputPath, dynamicOutputPath, mainTemplateConfig);
    }
}
