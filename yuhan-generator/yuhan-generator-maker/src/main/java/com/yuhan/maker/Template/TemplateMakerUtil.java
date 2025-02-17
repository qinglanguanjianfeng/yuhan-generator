package com.yuhan.maker.Template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.yuhan.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具类
 */
public class TemplateMakerUtil {
    /**
     * 从未分组的文件中移除组内的同名文件
     * @param filesInfoList
     * @return
     */
    public static List<Meta.FileConfigDTO.FilesDTO> removeGroupFilesFromRoot(List<Meta.FileConfigDTO.FilesDTO> filesInfoList){
        //一、获取到所有分组

        List<Meta.FileConfigDTO.FilesDTO> groupFilesInfoList = filesInfoList.stream()
                .filter(filesDTO -> StrUtil.isNotBlank(filesDTO.getGroupKey()))
                .collect(Collectors.toList());

        //二、获取所有分组内的文件列表————flatMap()将一个对象转化为多个对象
        List<Meta.FileConfigDTO.FilesDTO> groupInnerFilesInfoList = groupFilesInfoList.stream()
                .flatMap(filesDTO -> filesDTO.getFiles().stream())//Lambda表达式
                .collect(Collectors.toList());

        //三、获取所有分组内文件的输入路径集合
        Set<String> fileInputPathSet = groupInnerFilesInfoList
                .stream()
                .map(Meta.FileConfigDTO.FilesDTO::getInputPath)//方法引用，适用于直接调用对象的方法
                .collect(Collectors.toSet());

        //四、移除所有集合内的外层文件
        return filesInfoList.stream()
                .filter(filesDTO -> !fileInputPathSet.contains(filesDTO.getInputPath()))
                .collect(Collectors.toList());

    }
}
