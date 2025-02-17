package com.yuhan.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.yuhan.maker.generator.JarGenerator;
import com.yuhan.maker.generator.ScriptGenerator;
import com.yuhan.maker.generator.file.DynamicFileGenerator;
import com.yuhan.maker.meta.Meta;
import com.yuhan.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator extends GenerateTemplate{

    @Override
    protected void buildDist(String outputPath, String sourceCopyDestPath, String shellOutputFilePath, String jarPath) {
        //super.buildDist(outputPath, sourceCopyDestPath, shellOutputFilePath, jarPath);
        //子类的具体实现，dogenerate中的方法会优先调用这个方法
        System.out.println("不需要输出dist");
    }
}
