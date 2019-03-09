package io.agrest.constraints;

import io.agrest.meta.AgEntity;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ConstrainedAgEntityTest {

	private ConstrainedAgEntity visitor;

	@Before
	public void before() {
		AgEntity<?> entity = mock(AgEntity.class);
		visitor = new ConstrainedAgEntity(entity);
	}

	@Test
	public void testVisitExcludePropertiesConstraint() {

		visitor.getChildren().put("c", mock(ConstrainedAgEntity.class));
		visitor.getChildren().put("c1", mock(ConstrainedAgEntity.class));
		visitor.getAttributes().add("a");
		visitor.getAttributes().add("a1");

		visitor.excludeProperties("c", "a1");
		assertEquals(1, visitor.getChildren().size());
		assertTrue(visitor.getChildren().containsKey("c1"));
		assertEquals(1, visitor.getAttributes().size());
		assertTrue(visitor.getAttributes().contains("a"));
	}
}
