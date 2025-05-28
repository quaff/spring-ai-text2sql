package com.cywb.text2sql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class LoggingClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LoggingClientHttpRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		logRequest(request, body);
		var response = execution.execute(request, body);
		return logResponse(request, response);
	}

	private void logRequest(HttpRequest request, byte[] body) {
		logger.info("Request: {} {}", request.getMethod(), request.getURI());
		if (body != null && body.length > 0) {
			logger.info("Request body:\n{}", new String(body, StandardCharsets.UTF_8));
		}
	}

	private ClientHttpResponse logResponse(HttpRequest request, ClientHttpResponse response) throws IOException {
		logger.info("Response status: {}", response.getStatusCode());

		byte[] responseBody = response.getBody().readAllBytes();
		if (responseBody.length > 0) {
			logger.info("Response body:\n{}", new String(responseBody, StandardCharsets.UTF_8));
		}

		return new BufferingClientHttpResponseWrapper(response, responseBody);
	}

	private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

		private final ClientHttpResponse response;

		private final byte[] body;

		BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
			this.response = response;
			this.body = body;
		}

		@Override
		public InputStream getBody() {
			return new ByteArrayInputStream(this.body);
		}

		// Delegate other methods to wrapped response
		@Override
		public HttpStatusCode getStatusCode() throws IOException {
			return this.response.getStatusCode();
		}

		@Override
		public HttpHeaders getHeaders() {
			return this.response.getHeaders();
		}

		@Override
		public void close() {
			this.response.close();
		}

		@Override
		public String getStatusText() throws IOException {
			return this.response.getStatusText();
		}

	}

}
