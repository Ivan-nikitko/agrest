package com.nhl.link.rest.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhl.link.rest.client.protocol.Expression.ExpressionBuilder;
import com.nhl.link.rest.client.protocol.Include;
import com.nhl.link.rest.client.protocol.Include.IncludeBuilder;
import com.nhl.link.rest.client.protocol.LrcRequest;
import com.nhl.link.rest.client.protocol.LrcRequest.LrRequestBuilder;
import com.nhl.link.rest.client.protocol.Sort;
import com.nhl.link.rest.client.runtime.response.DataResponseHandler;
import com.nhl.link.rest.client.runtime.response.SimpleResponseHandler;
import com.nhl.link.rest.client.runtime.run.InvocationBuilder;
import com.nhl.link.rest.parser.converter.JsonValueConverter;
import com.nhl.link.rest.runtime.parser.converter.DefaultJsonValueConverterFactoryProvider;
import com.nhl.link.rest.runtime.parser.converter.IJsonValueConverterFactory;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @since 2.0
 */
public class LinkRestClient {

	private static JsonFactory jsonFactory;
	private static IJsonValueConverterFactory jsonEntityReaderFactory;

	static {
		jsonFactory = new ObjectMapper().getFactory();
		jsonEntityReaderFactory = new DefaultJsonValueConverterFactoryProvider(Collections.emptyMap()).get();
	}

	public static LinkRestClient client(WebTarget target) {
		return new LinkRestClient(target);
	}

	private WebTarget target;
	private LrRequestBuilder request;

	private LinkRestClient(WebTarget target) {
		this.target = target;
		request = LrcRequest.builder();
	}

	public LinkRestClient exclude(String... excludePaths) {
		request.exclude(excludePaths);
		return this;
	}

	public LinkRestClient include(String... includePaths) {
		request.include(includePaths);
		return this;
	}

	public LinkRestClient include(Include include) {
		request.include(include);
		return this;
	}

	public LinkRestClient include(IncludeBuilder include) {
		request.include(include.build());
		return this;
	}

	public LinkRestClient cayenneExp(ExpressionBuilder cayenneExp) {
		request.cayenneExp(cayenneExp.build());
		return this;
	}

	public LinkRestClient sort(String... properties) {
		request.sort(properties);
		return this;
	}

	public LinkRestClient sort(Sort ordering) {
		request.sort(ordering);
		return this;
	}

	public LinkRestClient start(long startIndex) {
		request.start(startIndex);
		return this;
	}

	public LinkRestClient limit(long limit) {
		request.limit(limit);
		return this;
	}

	public <T> ClientDataResponse<T> get(Class<T> targetType) {
		return invoke(targetType, InvocationBuilder.target(target).request(request.build()).buildGet());
	}

	/**
	 * @since 2.1
     */
	public <T> ClientDataResponse<T> post(Class<T> targetType, String data) {
		return invoke(targetType, InvocationBuilder.target(target).request(request.build()).buildPost(data));
	}

	/**
	 * @since 2.1
     */
	public <T> ClientDataResponse<T> put(Class<T> targetType, String data) {
		return invoke(targetType, InvocationBuilder.target(target).request(request.build()).buildPut(data));
	}

	/**
	 * @since 2.1
     */
	public ClientSimpleResponse delete() {
		Supplier<Response> invocation = InvocationBuilder.target(target).request(request.build()).buildDelete();
		return new SimpleResponseHandler(jsonFactory).handleResponse(invocation.get());
	}

	private <T> ClientDataResponse<T> invoke(Class<T> targetType, Supplier<Response> invocation) {
		JsonValueConverter<T> entityReader = getEntityReader(targetType);
		Response response = invocation.get();
		DataResponseHandler<T> responseHandler = new DataResponseHandler<>(jsonFactory, entityReader);
		return responseHandler.handleResponse(response);
	}

	private <T> JsonValueConverter<T> getEntityReader(Class<T> entityType) {
		Objects.requireNonNull(entityType, "Missing target type");
		return jsonEntityReaderFactory.typedConverter(entityType)
				.orElseThrow(() -> new LinkRestClientException("Can't build converter for type: " + entityType.getName()));
	}
}
