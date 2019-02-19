package de.retest.recheck.review.ignore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;
import de.retest.recheck.review.ignore.matcher.ElementIdMatcher;

class ElementShouldIgnoreTest {

	ElementShouldIgnore cut;

	@BeforeEach
	void setUp() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final ElementIdMatcher matcher = new ElementIdMatcher( element );
		cut = new ElementShouldIgnore( matcher );
	}

	@Test
	void shouldIgnoreElement_should_accept_element() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );

		assertThat( cut.shouldIgnoreElement( element ) ).isTrue();
	}

	@Test
	void shouldIgnoreElement_should_reject_element() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "ABC" );

		assertThat( cut.shouldIgnoreElement( element ) ).isFalse();
	}

	@Test
	void shouldIgnoreAttributeDifference_should_always_be_false() {
		final Element element = mock( Element.class );
		when( element.getRetestId() ).thenReturn( "abc" );
		final AttributeDifference difference = mock( AttributeDifference.class );

		assertThat( cut.shouldIgnoreAttributeDifference( element, difference ) ).isFalse();
	}
}
