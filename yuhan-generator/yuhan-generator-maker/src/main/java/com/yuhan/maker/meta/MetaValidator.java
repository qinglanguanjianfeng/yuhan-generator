package com.yuhan.maker.meta;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yuhan.maker.meta.enums.FileGenerateTypeEnum;
import com.yuhan.maker.meta.enums.FileTypeEnum;
import com.yuhan.maker.meta.enums.ModelTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MetaValidator {
    public static void doValidAndFill(Meta meta){
        //基本信息校验和填充
        validAndFillMetaRoot(meta);

        //fileConfig校验和填充
        validAndFillFileConfig(meta);
        //modelConfig校验和填充
        validAndFillModelConfig(meta);
    }

    private static void validAndFillModelConfig(Meta meta) {
        Meta.ModelConfigDTO modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList = modelConfig.getModels();
        if (CollectionUtil.isEmpty(modelInfoList)) {
            return;
        }
        for(Meta.ModelConfigDTO.ModelsDTO modelInfo:modelInfoList){
            String groupKey = modelInfo.getGroupKey();
            if(StrUtil.isNotEmpty(groupKey)){
                //生成中间参数
                List<Meta.ModelConfigDTO.ModelsDTO> subModelInfoList = modelInfo.getModels();
                String allArgsStr = subModelInfoList.stream()
                        .map(subModelInfo->String.format("\"--%s\"",subModelInfo.getFieldName()))
                        .collect(Collectors.joining(", "));
                modelInfo.setAllArgsStr(allArgsStr);
                continue;
            }
            //输出路径默认值
            String fieldName = modelInfo.getFieldName();
            if(StrUtil.isBlank(fieldName)){
                throw new MetaException("未填写 fieldName");
            }

            String modelInfoType = modelInfo.getType();
            if(StrUtil.isEmpty(modelInfoType)){
                modelInfo.setType(ModelTypeEnum.STRING.getValue());
            }
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        Meta.FileConfigDTO fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        //sourceRootPath:必填
        String sourceRootPath = fileConfig.getSourceRootPath();
        if(StrUtil.isBlank(sourceRootPath)){
            throw new MetaException("未填写 sourceRootPath");
        }

        //inputRootPath:.source+sourcePath最后一个层级的路径
        String inputRootPath = fileConfig.getInputRootPath();
        String defaultInputRootPath = ".source/"+
                FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        if(StrUtil.isEmpty(inputRootPath)){
            fileConfig.setInputRootPath(defaultInputRootPath);
        }

        //outputRootPath:默认为当前路径下的generated
        String outputRootPath = fileConfig.getOutputRootPath();
        String defaultOutputRootPath = "generated";
        if(StrUtil.isEmpty(outputRootPath)){
            fileConfig.setOutputRootPath(defaultOutputRootPath);
        }

        //type：默认为dir
        String fileConfigType = fileConfig.getType();
        String defaultType = "dir";
        if(StrUtil.isEmpty(fileConfigType)){
            fileConfig.setType(defaultType);
        }

        //fileInfo默认值
        List<Meta.FileConfigDTO.FilesDTO> fileInfoList = fileConfig.getFiles();
        if (CollectionUtil.isEmpty(fileInfoList)) {
            return;
        }
        for(Meta.FileConfigDTO.FilesDTO fileInfo:fileInfoList){
            //类型为group，不进行校验
            String type = fileInfo.getType();
            if(FileTypeEnum.GROUP.getValue().equals(type)){
                continue;
            }
            //inputPath:必填
            String inputPath = fileInfo.getInputPath();
            if(StrUtil.isEmpty(inputPath)){
                throw new MetaException("未填写 inputPath");
            }

            //outputPath:默认等于inputPath
            String outputPath = fileInfo.getOutputPath();
            if(StrUtil.isEmpty(outputPath)){
                fileInfo.setOutputPath(inputPath);
            }
            //type：默认inputPath有文件后缀为file,否则为dir
            if(StrUtil.isBlank(type)){
                //无文件后缀
                if(StrUtil.isBlank(FileUtil.getSuffix(inputPath))){
                    fileInfo.setType(FileTypeEnum.DIR.getValue());
                }else{
                    fileInfo.setType(FileTypeEnum.FILE.getValue());
                }
            }
            //generatedType:如果文件结尾不为ftl,generated默认为static,否则为dynamic
            String generateType = fileInfo.getGenerateType();
            if(StrUtil.isBlank(generateType)){
                //为动态模板
                if(inputPath.endsWith("ftl")){
                    fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                }else{
                    fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                }
            }
        }
    }

    private static void validAndFillMetaRoot(Meta meta) {


        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器");
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "yupi");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.yupi");
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());
        meta.setName(name);
        meta.setDescription(description);
        meta.setAuthor(author);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setCreateTime(createTime);
    }
}



