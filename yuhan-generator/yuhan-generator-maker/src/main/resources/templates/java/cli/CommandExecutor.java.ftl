package ${basePackage}.cli;


import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;//主命令的功能就是绑定子命令

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }
    //单独用一个代码块初始化CommandLine队象：所有命令要使用的CommandLine对象都一样


    @Override
    public void run() {
        System.out.println("请输入具体命令，或者输入 --help查看命令提示");
    }

    //执行命令类的方法
    /**
     * 执行命令
     *
     * @param args
     * @return
     */
    public Integer doExecute(String[] args){
        return commandLine.execute(args);
    }


}
