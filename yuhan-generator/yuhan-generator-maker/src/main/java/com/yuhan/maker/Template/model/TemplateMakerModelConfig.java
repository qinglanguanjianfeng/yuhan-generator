package com.yuhan.maker.Template.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class TemplateMakerModelConfig {
    private List<ModelInfoConfig> models;
    private ModelGroupConfig modelGroupConfig;

    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig{
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;//注意这里，必须是对象类型，因为默认值有布尔类型，有字符串类型
        private String abbr;
        //用于替换哪些文本
        private String replaceText;
    }

    @Data
    public static class ModelGroupConfig{
        private String condition;
        private String groupKey;
        private String groupName;

        private String type;
        private String description;
    }
}
