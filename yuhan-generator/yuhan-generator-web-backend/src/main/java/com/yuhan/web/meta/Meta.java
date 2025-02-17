package com.yuhan.web.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    public FileConfigDTO fileConfig;
    private ModelConfigDTO modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfigDTO {
        private String inputRootPath;
        private String outputRootPath;
        private String sourceRootPath;
        private String type;
        private List<FilesDTO> files;

        @NoArgsConstructor
        @Data
        public static class FilesDTO {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition;
            private String groupKey;
            private String groupName;
            private List<FilesDTO> files;
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfigDTO {
        private List<ModelsDTO> models;

        @NoArgsConstructor
        @Data
        public static class ModelsDTO {
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;//注意这里，必须是对象类型，因为默认值有布尔类型，有字符串类型
            private String abbr;
            private String groupKey;
            private String groupName;
            private String condition;//控制是否开启参数组
            private String allArgsStr;//中间参数，表示分组下所有参数拼接字符串
            private List<ModelsDTO> models;
        }
    }
}
