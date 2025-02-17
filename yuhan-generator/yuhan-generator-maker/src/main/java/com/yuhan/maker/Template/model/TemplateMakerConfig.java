package com.yuhan.maker.Template.model;

import com.yuhan.maker.meta.Meta;
import lombok.Data;

/**
 * 模板制作配置
 */
@Data
public class TemplateMakerConfig {
//    /**
//     * 制作模板
//     * @param newMeta
//     * @param originProjectPath
//     * @param templateMakerModelConfig
//     * @param templateMakerFileConfig
//     * @param id
//     * @return
//     */

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();

}
