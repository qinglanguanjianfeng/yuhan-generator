package com.yuhan.maker.model;

import lombok.Data;

/*
 * 静态模板配置
 */
@Data
public class DataModel {

    /*
    动态生成需求：
    在代码开头增加作者 @Author 注释（增加代码）
    修改程序输出的信息提示（替换代码）
    将循环读取输入改为单次读取（可选代码）
     */

    public String author = "无作者";//作者（字符串，填充值）

    public String outputText = "无输出信息";//输出信息

    public boolean loop = true;//是否循环（开关）

}
