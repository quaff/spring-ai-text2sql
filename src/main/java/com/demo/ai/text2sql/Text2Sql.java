package com.demo.ai.text2sql;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Text2Sql {

    private final ChatClient chatClient;

    public Text2Sql(ChatClient.Builder builder,
                    DatabaseInformationAdvisor databaseMetadataAdvisor, ApplicationContext ctx) {
        chatClient = builder.defaultAdvisors(databaseMetadataAdvisor)
                .defaultTools(ctx.getBeansWithAnnotation(ToolComponent.class).values().toArray())
                .build();
    }

    public String query(String query) {
        return chatClient.prompt().user(query).call().content();
    }
}
