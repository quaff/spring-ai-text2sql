package com.demo.ai.text2sql;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class Text2Sql {

    private final ChatClient chatClient;

    public Text2Sql(ChatClient.Builder builder,
                    DatabaseInformationAdvisor databaseMetadataAdvisor, JdbcClientTool jdbcClientTool) {
        chatClient = builder.defaultAdvisors(databaseMetadataAdvisor).defaultTools(jdbcClientTool).build();
    }

    public String query(String query) {
        return chatClient.prompt().user(query).call().content();
    }
}
