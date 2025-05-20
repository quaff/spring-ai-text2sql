package com.demo.ai.text2sql;

import org.springframework.ai.chat.client.ChatClient;

public class Text2Sql {

    private final ChatClient chatClient;

    public Text2Sql(ChatClient.Builder builder,
                    DatabaseInformationAdvisor databaseMetadataAdvisor, Object[] tools) {
        this.chatClient = builder.defaultAdvisors(databaseMetadataAdvisor)
                .defaultTools(tools)
                .build();
    }

    public ChatClient getChatClient() {
        return this.chatClient;
    }

    public String query(String query) {
        return chatClient.prompt().user(query).call().content();
    }

    public <T> T query(String query, Class<T> type) {
        return chatClient.prompt().user(query).call().entity(type);
    }
}
