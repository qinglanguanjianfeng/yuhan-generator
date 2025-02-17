package ${basePackage}.model;

import lombok.Data;

<#macro generateModel incent modelInfo>
<#if modelInfo.description??>
${incent}/**
${incent}*${modelInfo.description}
${incent}*/
</#if>
${incent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>


/*
* 数据模型
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>
    <#--有分组-->
    <#if modelInfo.groupKey??>
    /*
    ${modelInfo.groupName}
    */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();
    /*
    ${modelInfo.description}
    */
    @Data
    public static class ${modelInfo.type}{
        <#list modelInfo.models as modelInfo>
            <@generateModel incent="        "modelInfo=modelInfo></@generateModel>
        </#list>
    }
    <#else>
    <#--无分组-->
    <@generateModel incent="    "modelInfo=modelInfo></@generateModel>
    </#if>
</#list>
}
