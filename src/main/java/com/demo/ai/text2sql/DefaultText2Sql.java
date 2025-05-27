package com.demo.ai.text2sql;

import org.springframework.ai.chat.client.ChatClient;

public class DefaultText2Sql implements Text2Sql {

    private final ChatClient chatClient;

    public DefaultText2Sql(ChatClient.Builder builder,
                           DatabaseInformationAdvisor databaseMetadataAdvisor, Object[] tools) {
        this.chatClient = builder.defaultAdvisors(databaseMetadataAdvisor)
                .defaultTools(tools)
                .build();
    }

    public ChatClient getChatClient() {
        return this.chatClient;
    }

    @Override
    public String query(String query) {
        return chatClient.prompt().user(query).call().content();
    }

    @Override
    public <T> T query(String query, Class<T> type) {
        return chatClient.prompt().user(query).call().entity(type);
    }

    @Override
    public String echarts(String query) {
        return chatClient.prompt().user(query).call().entity(new EChartsStructuredOutputConverter());
    }

}
