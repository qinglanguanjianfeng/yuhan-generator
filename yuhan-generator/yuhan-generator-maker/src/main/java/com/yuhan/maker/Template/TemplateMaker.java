package com.yuhan.maker.Template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.yuhan.maker.Template.enums.FileFilterRangeEnum;
import com.yuhan.maker.Template.enums.FileFilterRuleEnum;
import com.yuhan.maker.Template.model.*;
import com.yuhan.maker.meta.Meta;
import com.yuhan.maker.meta.enums.FileGenerateTypeEnum;
import com.yuhan.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/*
模板制作工具
 */
public class TemplateMaker {

    /**
     * 制作模板的封装方法
     * @param templateMakerConfig
     * @return
     */
    public static long makeTemplate(TemplateMakerConfig templateMakerConfig){
        Long id = templateMakerConfig.getId();
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();

        return makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,outputConfig,id);
    }


    /**
     * 制作模板
     * @param newMeta
     * @param originProjectPath
     * @param templateMakerModelConfig
     * @param templateMakerFileConfig
     * @param templateMakerOutputConfig
     * @param id
     * @return
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, TemplateMakerOutputConfig templateMakerOutputConfig,Long id){
        if(id == null){
            id = IdUtil.getSnowflakeNextId();//雪花算法分配id
        }
        //业务逻辑

        //复制目录和原项目
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath+File.separator+".temp";//模板目录路径
        String templatePath = tempDirPath+File.separator+id;//工作空间
        if(!FileUtil.exist(templatePath)){
            //原路径不存在，证明没有被创建过隔离空间，进行复制
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath,templatePath,true);
        }

        //输入信息
        //输入文件信息
        //获得要挖坑的项目根目录
        File tempFile = new File(templatePath);
        templatePath = tempFile.getAbsolutePath();
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath),1,null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        //win系统对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");//向右双斜杠->向左单斜杠

        //二、制作文件模板
        List<Meta.FileConfigDTO.FilesDTO> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        //处理模型信息
        List<Meta.ModelConfigDTO.ModelsDTO> newModelsInfoList = getModelInfoList(templateMakerModelConfig);

        //三、生成配置文件(对象转json)
        String metaOutputPath = templatePath+File.separator+"meta.json";

        //已有meta文件，不是第一次制作，则在原有基础上进行修改
        if(FileUtil.exist(metaOutputPath)){
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);//原json转回对象

            //1.追加配置参数
            List<Meta.FileConfigDTO.FilesDTO> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelsInfoList);

            //配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList)) ;

        }else{
            //1.构造配置参数对象

            //文件配置
            Meta.FileConfigDTO fileConfig = new Meta.FileConfigDTO();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);

            List<Meta.FileConfigDTO.FilesDTO> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfigDTO modelConfig = new Meta.ModelConfigDTO();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelsInfoList);
        }

        //2.额外的输出配置
        if(templateMakerOutputConfig!=null){
//            List<Meta.FileConfigDTO.FilesDTO> list = newMeta.getFileConfig().getFiles().stream().filter(filesDTO -> filesDTO.getCondition()!=null).collect(Collectors.toList());
//            for (Meta.FileConfigDTO.FilesDTO filesDTO:list) {
//                System.out.println(filesDTO.getCondition());
//            }
            //文件外层和分组去重
            if(templateMakerOutputConfig.isRemoveGroupFilesFromRoot()){
                List<Meta.FileConfigDTO.FilesDTO> fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtil.removeGroupFilesFromRoot(fileInfoList));
            }
        }

        //2.输出元信息文件

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),metaOutputPath);

        return id;
    }

    /**
     * 获取模型配置
     * @param templateMakerModelConfig
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        //本次新增的模型列表
        List<Meta.ModelConfigDTO.ModelsDTO> newModelsInfoList = new ArrayList<>();
        //非空校验
        if(templateMakerModelConfig == null){
            return newModelsInfoList;
        }

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        //非空校验
        if(CollUtil.isEmpty(models)){
            return newModelsInfoList;
        }

        //处理模型信息
        //转化为配置文件接受的ModelsDTO对象，构成List<ModelsDTO>
        List<Meta.ModelConfigDTO.ModelsDTO> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    //ModelsInfoConfig只有List<ModelsDTO>这一个成员，而ModelsDTO中也有这个成员
                    Meta.ModelConfigDTO.ModelsDTO modelsInfo = new Meta.ModelConfigDTO.ModelsDTO();
                    BeanUtil.copyProperties(modelInfoConfig,modelsInfo);
                    return modelsInfo;
                })
                .collect(Collectors.toList());



        //如果是模型组，需要重新组装newModelInfoList
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if(modelGroupConfig!=null){

            //创建一个新的模型信息类对象
            Meta.ModelConfigDTO.ModelsDTO groupModelInfo = new Meta.ModelConfigDTO.ModelsDTO();
            BeanUtil.copyProperties(modelGroupConfig,groupModelInfo);//复制变量方法为对象成员属性赋值

            //模型放到一个分组
            groupModelInfo.setModels(inputModelInfoList);//todo解决
            newModelsInfoList = new ArrayList<>();//列表清空，一次一分组
            newModelsInfoList.add(groupModelInfo);
        }else{
            //不分组，将所有模型信息放到列表中
            newModelsInfoList.addAll(inputModelInfoList);
        }
        return newModelsInfoList;
    }

    /**
     * 生成多个文件
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {

        List<Meta.FileConfigDTO.FilesDTO> newFileInfoList = new ArrayList<>();
        //非空校验
        if(templateMakerFileConfig == null){
            return newFileInfoList;
        }
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        if(CollUtil.isEmpty(fileInfoConfigList)){
            return newFileInfoList;
        }

        //遍历输入文件
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig: fileInfoConfigList) {
            String inputFilePath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath +File.separator+inputFilePath;//绝对路径拼接相对路径

            //得到过滤后的文件列表
            List<File> fileList =  FileFilter.doFilter(inputFileAbsolutePath,fileInfoConfig.getFilterConfigList());//传入绝对路径

            //不处理已经生成的FTL模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());

            for(File file:fileList){
                Meta.FileConfigDTO.FilesDTO fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath,file,fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        //如果是文件组，需要重新组装newFileInfoList
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if(fileGroupConfig!=null){
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();
            //新增分组配置
            Meta.FileConfigDTO.FilesDTO groupFileInfo = new Meta.FileConfigDTO.FilesDTO();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupName(groupName);
            //文件放到一个分组
            groupFileInfo.setFiles(newFileInfoList);
            newFileInfoList = new ArrayList<>();//列表清空，一次一分组
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }


    /**
     * 制作模板文件
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @param inputFile
     * @param fileInfoConfig
     * @return
     */
    private static Meta.FileConfigDTO.FilesDTO makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile, TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        //注意win系统需要对路径进行转义
        fileInputAbsolutePath = fileInputAbsolutePath.replaceAll("\\\\","/");
        String fileOutputAbsolutePath = fileInputAbsolutePath+".ftl";
        //要挖坑的文件：注意一定要是相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath+"/","");
        String fileOutputPath = fileInputPath +".ftl";

        //使用字符串替换，生成模板文件，得到文件配置信息

        //如果已有模板文件，表示不是第一次制作，在原有模板基础上再挖坑
        String fileContent;
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);//模板文件是否已经生成
        if(hasTemplateFile){//已经生成了一份模板文件，在此基础上进行修改
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else{
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        //支持多个模型：对同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;//最新替换后的内容
        String replacement;//替换物

        for(TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig:templateMakerModelConfig.getModels()){
            String fieldName = modelInfoConfig.getFieldName();
            //模型配置
            if(modelGroupConfig == null){//不是分组
                replacement = String.format("${%s}",fieldName);
            }else{//是分组
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}",groupKey,fieldName);
            }
            //循环遍历进行挖坑
            newFileContent = StrUtil.replace(newFileContent,modelInfoConfig.getReplaceText(),replacement);
        }

        //文件配置信息
        Meta.FileConfigDTO.FilesDTO fileInfo = new Meta.FileConfigDTO.FilesDTO();
        //文件输入路径与输出路径反转
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setInputPath(fileOutputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        boolean contentEquals = newFileContent.equals(fileContent);//判断是否对文件进行了挖坑（首次或第n次）

        if(!hasTemplateFile){//没有模板文件，首次制作
            //静态与动态文件信息
            if(contentEquals){//静态文件 todo
                //输出路径 = 输入路径
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            }else {//动态文件
                fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                //输出模板文件
                FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
            }
        }else if(!contentEquals){//已有模板文件，且动态生成
            //输出模板文件
            FileUtil.writeUtf8String(newFileContent,fileOutputAbsolutePath);
        }
        return fileInfo;
    }

    public static void main(String[] args) {
        //项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-pro-generator");
        meta.setDescription("ACM示例模板生成器");

        //指定原始项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent()+File.separator+"yuhan-generator-demo-projects/springboot-init-master";

        //要挖坑的文件
        String fileInputPath1 = "src/main/java/com/yupi/springbootinit/common";
        String fileInputPath2 = "src/main/resources/application.yml";
        //输入模型参数信息
//        Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
//        modelInfo.setFieldName("outputText");
//        modelInfo.setType("String");
//        modelInfo.setDefaultValue("sum = ");

        //输入模型参数信息（第二次）
        Meta.ModelConfigDTO.ModelsDTO modelInfo = new Meta.ModelConfigDTO.ModelsDTO();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        //替换变量（首次）
//        String searchStr = "Sum：";
        String searchStr = "BaseResponse";

        //过滤
        //模板制作文件配置信息（带过滤器）
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        //过滤信息对象
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();

        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();//过滤信息列表
        fileFilterConfigList.add(fileFilterConfig);//仅添加了一个过滤信息

        //1包含过滤信息的，文件信息配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(fileInputPath1);
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

        //2没有过滤信息的，文件信息配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(fileInputPath2);
        //文件信息配置列表
        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1,fileInfoConfig2);
        templateMakerFileConfig.setFiles(fileInfoConfigList);//向“模板制作文件配置信息”中添加文件配置信息列表

        //文件分组
        //文件分组信息配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText2");
        fileGroupConfig.setGroupKey("test2");
        fileGroupConfig.setGroupName("测试分组2");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        //模型配置
        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();

        // - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);

        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta,originProjectPath,templateMakerFileConfig,templateMakerModelConfig,null,1887017206442098688L);
        System.out.println(id);
    }

    /**
     * 文件去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfigDTO.FilesDTO> distinctFiles(List<Meta.FileConfigDTO.FilesDTO> fileInfoList) {
        //1.将所有文件配置（FilesDTO）分为有分组的和无分组的
        // List<{groupKey,[1,2]}、{groupKey,[2,3]}、{[4,5]}>
        //1.1先处理有分组的文 件{groupKey,[1,2]}、{groupKey,[2,3]}
        //1.1.1以组为单位对文件进行划分
        Map<String,List<Meta.FileConfigDTO.FilesDTO>> groupKeyFileInfoList = fileInfoList.stream()
                .filter(filesDTO -> StrUtil.isNotBlank(filesDTO.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfigDTO.FilesDTO::getGroupKey)//直接按照groupKey进行分组，返回map
                );
        //Map<groupKey,List<{groupKey,[1,2]},{groupKey,[2,3]}>>

        //2同组内文件配置合并
        Map<String, Meta.FileConfigDTO.FilesDTO> groupKeyMergedFileInfoMap = new HashMap<>();
        //合并后的对象map，一个key一个文件配置     Map<groupKey,{groupKey,[1,2,3]}>
        for (Map.Entry<String,List<Meta.FileConfigDTO.FilesDTO>> entry:groupKeyFileInfoList.entrySet()) {
            List<Meta.FileConfigDTO.FilesDTO> tempFileInfoList = entry.getValue();//List<{groupKey,[1,2]},{groupKey,[2,3]}>
            List<Meta.FileConfigDTO.FilesDTO> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(filesDTO -> filesDTO.getFiles().stream())//实现FilesDTO元素到FilesDTO元素流的转换
                    .collect(
                            Collectors.toMap(Meta.FileConfigDTO.FilesDTO::getOutputPath,o->o,(e,r)->r)
                            //inputpath是key,o->o表示将元素fileInfo本身作为值,(e,r)->r表示合并
                    ).values());//List<{1},{2},{3}>

            //使用新的group配置
            Meta.FileConfigDTO.FilesDTO newFileInfo = CollUtil.getLast(tempFileInfoList);//获取group信息（最新配置信息）{groupKey,[2,3]}
            newFileInfo.setFiles(newFileInfoList);//填充文件列表{groupKey,[1,2,3]}
            String groupKey = entry.getKey();//当前循环遍历的groupKey
            groupKeyMergedFileInfoMap.put(groupKey,newFileInfo);//Map<groupKey,{groupKey,[1,2,3]}>

        }

        //3.将合并后的分组添加到结果列表
        ArrayList<Meta.FileConfigDTO.FilesDTO> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());

        //4.将无分组的文件配置列表添加到结果列表
        resultList.addAll(
        new ArrayList<>(fileInfoList.stream()
                .filter(filesDTO -> StrUtil.isBlank(filesDTO.getGroupKey()))
                .collect(
                        Collectors.toMap(Meta.FileConfigDTO.FilesDTO::getInputPath, o -> o, (exist, replace) -> replace))
                .values()));
        return resultList;
    }

    /**
     * 模型去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelsDTO> distinctModels(List<Meta.ModelConfigDTO.ModelsDTO> modelInfoList) {
        //1.将所有模型配置（ModelsDTO）分为有分组的和无分组的
        // List<{groupKey,[1,2]}、{groupKey,[2,3]}、{[4,5]}>
        //1.1先处理有分组的模型{groupKey,[1,2]}、{groupKey,[2,3]}
        //1.1.1以组为单位对模型进行划分
        Map<String,List<Meta.ModelConfigDTO.ModelsDTO>> groupKeyModelInfoList = modelInfoList.stream()
                .filter(modelsDTO -> StrUtil.isNotBlank(modelsDTO.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfigDTO.ModelsDTO::getGroupKey)//直接按照groupKey进行分组，返回map
                );
        //Map<groupKey,List<{groupKey,[1,2]},{groupKey,[2,3]}>>

        //2同组内模型配置合并
        Map<String, Meta.ModelConfigDTO.ModelsDTO> groupKeyMergedModelInfoMap = new HashMap<>();
        //合并后的对象map，一个key一个模型配置     Map<groupKey,{groupKey,[1,2,3]}>
        for (Map.Entry<String,List<Meta.ModelConfigDTO.ModelsDTO>> entry:groupKeyModelInfoList.entrySet()) {
            List<Meta.ModelConfigDTO.ModelsDTO> tempModelInfoList = entry.getValue();//List<{groupKey,[1,2]},{groupKey,[2,3]}>
            List<Meta.ModelConfigDTO.ModelsDTO> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelsDTO -> modelsDTO.getModels().stream())//实现ModelsDTO元素到ModelsDTO元素流的转换
                    .collect(
                            Collectors.toMap(Meta.ModelConfigDTO.ModelsDTO::getFieldName,o->o,(e,r)->r)
                            //inputpath是key,o->o表示将元素modelInfo本身作为值,(e,r)->r表示合并
                    ).values());//List<{1},{2},{3}>

            //使用新的group配置
            Meta.ModelConfigDTO.ModelsDTO newModelInfo = CollUtil.getLast(tempModelInfoList);//获取group信息（最新配置信息）{groupKey,[2,3]}
            newModelInfo.setModels(newModelInfoList);//填充模型列表{groupKey,[1,2,3]}
            String groupKey = entry.getKey();//当前循环遍历的groupKey
            groupKeyMergedModelInfoMap.put(groupKey,newModelInfo);//Map<groupKey,{groupKey,[1,2,3]}>

        }

        //3.将合并后的分组添加到结果列表
        ArrayList<Meta.ModelConfigDTO.ModelsDTO> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());

        //4.将无分组的模型配置列表添加到结果列表
        resultList.addAll(
                new ArrayList<>(modelInfoList.stream()
                        .filter(modelsDTO -> StrUtil.isBlank(modelsDTO.getGroupKey()))
                        .collect(
                                Collectors.toMap(Meta.ModelConfigDTO.ModelsDTO::getFieldName, o -> o, (exist, replace) -> replace))
                        .values()));
        return resultList;
    }
}
