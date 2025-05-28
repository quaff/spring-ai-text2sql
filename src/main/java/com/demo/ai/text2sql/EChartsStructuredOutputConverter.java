package com.demo.ai.text2sql;

import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.lang.NonNull;

public class EChartsStructuredOutputConverter implements StructuredOutputConverter<String> {

	@Override
	public String convert(@NonNull String text) {
		int index = text.indexOf("```");
		if (index > -1) {
			index = index + (text.contains("```json") ? 7 : 3);
			text = text.substring(index);
			text = text.substring(0, text.indexOf("```"));
		}
		return text;
	}

	@Override
	public String getFormat() {
		return """
				Your response should be regular result with explanations if it's not suitable for chart presentation.
				Your response should be in JSON format if it's suitable for the Apache ECharts library.
				The data structure for the JSON should match Apache ECharts required data format.
				Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
				Remove the ```json markdown surrounding the output including the trailing "```".
				""";
	}

}
