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

    public String echarts(String query) {
        query += "\n请返回Apache Echarts需要的数据格式。";
        String content = chatClient.prompt().user(query).call().content();
        if (content == null) {
            return content;
        }
        int index = content.indexOf("```");
        if (index > -1) {
            index = index + (content.contains("```json") ? 7 : 3);
            content = content.substring(index);
            content = content.substring(0, content.indexOf("```"));
        }
        return content;
    }

}
