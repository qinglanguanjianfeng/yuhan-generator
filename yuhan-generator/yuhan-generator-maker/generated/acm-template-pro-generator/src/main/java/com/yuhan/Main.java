package com.yuhan;

import com.yuhan.cli.CommandExecutor;

public class Main {
    public static void main(String[] args) {
//        args = new String[]{"generate", "-l", "-a", "-o"};
//        args = new String[]{"config"};
//        args = new String[]{"list"};
        CommandExecutor commandExecutor = new CommandExecutor();
        args = new String[]{"generate","-l"};
        commandExecutor.doExecute(args);//用户输入的参数
    }
}