<#-- $name=bulk-result;description=default for elastic test results;version=0.1$ -->
<#assign body = JsonUtil.convertJsonToMap(input)>

<#assign testCases = body.testCases>
<#assign size = testCases?size>
<#list testCases as testCase>
    <#assign size = size-1>
    "${testCase}"
    <#if size != 0>,
    </#if>
</#list>