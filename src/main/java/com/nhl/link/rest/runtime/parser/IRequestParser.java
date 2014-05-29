package com.nhl.link.rest.runtime.parser;

import javax.ws.rs.core.UriInfo;

import com.nhl.link.rest.DataResponse;
import com.nhl.link.rest.UpdateResponse;

/**
 * Defines protocol adapter between the REST interface and LinkRest backend.
 */
public interface IRequestParser {

	<T> DataResponse<T> parseSelect(DataResponse<T> response, UriInfo uriInfo, String autocompleteProperty);

	<T> UpdateResponse<T> parseUpdate(UpdateResponse<T> response, Object id, String requestBody);

	/**
	 * @deprecated since 1.1 - renamed to
	 *             {@link #parseInsert(UpdateResponse, String)} for consistency.
	 */
	@Deprecated
	<T> UpdateResponse<T> insertRequest(UpdateResponse<T> response, String requestBody);

	/**
	 * @since 1.1
	 */
	<T> UpdateResponse<T> parseInsert(UpdateResponse<T> response, String requestBody);

}
