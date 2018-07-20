package com.nhl.link.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nhl.link.rest.runtime.encoder.AttributeEncoderFactoryProvider;
import org.junit.Before;
import org.junit.Test;

import com.nhl.link.rest.encoder.EncoderFilter;
import com.nhl.link.rest.encoder.PropertyMetadataEncoder;
import com.nhl.link.rest.it.fixture.cayenne.E1;
import com.nhl.link.rest.runtime.encoder.EncoderService;
import com.nhl.link.rest.runtime.encoder.IAttributeEncoderFactory;
import com.nhl.link.rest.runtime.encoder.IEncoderService;
import com.nhl.link.rest.runtime.encoder.IStringConverterFactory;
import com.nhl.link.rest.runtime.semantics.RelationshipMapper;
import com.nhl.link.rest.unit.TestWithCayenneMapping;

public class DataResponseTest extends TestWithCayenneMapping {

	private IEncoderService encoderService;

	@Before
	public void setUp() {

		IAttributeEncoderFactory attributeEncoderFactory = new AttributeEncoderFactoryProvider(Collections.emptyMap()).get();
		IStringConverterFactory stringConverterFactory = mock(IStringConverterFactory.class);
		this.encoderService = new EncoderService(Collections.<EncoderFilter> emptyList(), attributeEncoderFactory,
				stringConverterFactory, new RelationshipMapper(),
				Collections.<String, PropertyMetadataEncoder> emptyMap());
	}

	@Test
	public void testToResponse_PlainObjects() {

		ResourceEntity<E1> resourceEntity = getResourceEntity(E1.class);

		List<E1> o1 = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			o1.add(new E1());
		}

		DataResponse<E1> response = DataResponse.forType(E1.class);
		response.setObjects(o1);
		response.setEncoder(encoderService.dataEncoder(resourceEntity));

		assertNotNull(response);
		assertEquals(o1, response.getObjects());
	}
}
