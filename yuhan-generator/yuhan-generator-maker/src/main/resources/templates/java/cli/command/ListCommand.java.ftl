package com.yuhan.maker.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

@CommandLine.Command(name = "list",mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");//项目的根目录----basic
        File parentFile = new File(projectPath).getParentFile();//basic的父目录
        String inputPath = new File(parentFile,"yuhan-generator-demo-projects/acm-template").getAbsolutePath();
        //遍历文件目录
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file:files) {
            System.out.println(file);
        }

    }
}
