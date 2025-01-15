package com.yuhan.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.yuhan.maker.generator.file.FileGenerator;
import com.yuhan.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;
@Data
@CommandLine.Command(name = "generate",mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable {
    @CommandLine.Option(names = {"-a", "--author"}, description = "作者名称",arity = "0..1",interactive = true,echo = true)
    private String author = "yuhanjin";//作者（字符串，填充值）
    @CommandLine.Option(names = {"-o", "--output"}, description = "输出文本",arity = "0..1",interactive = true,echo = true)
    private String outputText = "无输出信息";//输出信息
    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环",arity = "0..1",interactive = true,echo = true)
    private boolean loop = true;//是否循环（开关）

    @Override
    public Integer call() throws Exception {
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        FileGenerator.doGenerate(dataModel);
        return 0;
    }
}
