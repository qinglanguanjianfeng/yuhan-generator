package com.yuhan.maker.Template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.yuhan.maker.Template.enums.FileFilterRangeEnum;
import com.yuhan.maker.Template.enums.FileFilterRuleEnum;
import com.yuhan.maker.Template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileFilter {

    /**
     * 对某个文件或目录进行过滤，返回文件列表
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath,List<FileFilterConfig> fileFilterConfigList){
        //根据路径获取所有文件
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()//将fileList转换为一个流（Stream），以便后续进行流式操作。
                .filter(file -> doSingleFileFilter(fileFilterConfigList,file))//对流中的每个文件进行过滤
                .collect(Collectors.toList());//将过滤后的流重新收集为一个List<File>
    }
    public static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file){
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        //过滤器校验结果
        boolean result = true;

        if(CollUtil.isEmpty(fileFilterConfigList)){
            return true;
        }

        for(FileFilterConfig fileFilterConfig:fileFilterConfigList){
            String range = fileFilterConfig.getRange();
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if(fileFilterRangeEnum == null){
                continue;
            }

            //要过滤的原内容
            String content = fileName;
            switch (fileFilterRangeEnum){
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if(fileFilterRuleEnum == null){
                continue;
            }

            switch (fileFilterRuleEnum){
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(value);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                default:
            }

            //有一个过滤器配置不满足
            if(!result){
                return false;
            }
        }

        //循环完成，每一个过滤器配置都满足
        return true;
    }
}
