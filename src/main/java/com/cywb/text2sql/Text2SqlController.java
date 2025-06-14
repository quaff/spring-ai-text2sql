package com.cywb.text2sql;

import java.util.List;

import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/text2sql")
public class Text2SqlController {

	private final Parser parser = Parser.builder().build();

	private final HtmlRenderer renderer = HtmlRenderer.builder().build();

	private final Text2Sql text2Sql;

	private final Text2SqlProperties text2SqlProperties;

	public Text2SqlController(Text2Sql text2Sql, Text2SqlProperties text2SqlProperties) {
		this.text2Sql = text2Sql;
		this.text2SqlProperties = text2SqlProperties;
	}

	@RequestMapping(value = "/query", produces = MediaType.TEXT_PLAIN_VALUE)
	public String query(@RequestParam String query) {
		return this.text2Sql.query(query);
	}

	@RequestMapping(value = "/echarts", produces = MediaType.TEXT_PLAIN_VALUE)
	public String echarts(@RequestParam String query) {
		String result = this.text2Sql.echarts(query);
		if (!result.startsWith("{")) {
			result = this.renderer.render(this.parser.parse(result));
		}
		return result;
	}

	@GetMapping("/predefinedPrompts")
	public List<String> predefinedPrompts() {
		return this.text2SqlProperties.getPredefinedPrompts();
	}

}
