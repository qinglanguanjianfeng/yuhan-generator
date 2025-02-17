package com.yuhan.maker.Template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TemplateMakerOutputConfig {
    //是否从未分组的文件中移除组内的同名文件
    private boolean removeGroupFilesFromRoot = true;

}
