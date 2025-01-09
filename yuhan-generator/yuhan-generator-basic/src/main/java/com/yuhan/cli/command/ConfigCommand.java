package com.yuhan.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.yuhan.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

@CommandLine.Command(name = "config",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{
    @Override
    public void run() {
        Field fields[] = ReflectUtil.getFields(MainTemplateConfig.class);//Hutool反射工具类，获取类的字段
        for(Field field:fields){
            System.out.println("字段类型："+field.getType());
            System.out.println("字段名称："+field.getName());
        }
    }
}
