package com.demo.ai.text2sql;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInformationAdvisor implements BaseAdvisor {

    private static final String DEFAULT_SYSTEM_TEXT = """
            你是一个{databaseProductName}数据库专家。请帮助生成一个{databaseProductName} {databaseVersion}查询语句，然后执行这个查询语句回答问题。
            
            ===Tables
            {tableSchemas}
            """;

    private final DatabaseInformation databaseInformation;

    public DatabaseInformationAdvisor(DatabaseInformation databaseInformation) {
        this.databaseInformation = databaseInformation;
    }

    @Override
    public AdvisedRequest before(AdvisedRequest advisedRequest) {
        Map<String, Object> systemParams = new HashMap<>(advisedRequest.systemParams());
        systemParams.put("databaseProductName", databaseInformation.getDatabaseProductName());
        systemParams.put("databaseVersion", databaseInformation.getDatabaseVersion());
        systemParams.put("tableSchemas", databaseInformation.getTableSchemas());
        return AdvisedRequest.from(advisedRequest)
                .systemText(DEFAULT_SYSTEM_TEXT)
                .systemParams(systemParams)
                .build();
    }

    @Override
    public AdvisedResponse after(AdvisedResponse advisedResponse) {
        return advisedResponse;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}