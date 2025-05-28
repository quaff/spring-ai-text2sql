package com.cywb.text2sql;

import org.springframework.ai.converter.StructuredOutputConverter;
import org.springframework.lang.NonNull;

public class EChartsStructuredOutputConverter implements StructuredOutputConverter<String> {

	@Override
	public String convert(@NonNull String text) {
		int index = text.indexOf("```");
		if (index > -1) {
			index = index + (text.contains("```json") ? 7 : 3);
			text = text.substring(index);
			text = text.substring(0, text.indexOf("```")).trim();
		}
		return text;
	}

	@Override
	public String getFormat() {
		return """
				如果回答适合使用图表展现的话，请返回不包含任何解释和推理过程的符合ECharts格式的JSON结果。
				""";
	}

}
