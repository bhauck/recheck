package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.descriptors.IdentifyingAttributes;
import de.retest.recheck.ui.diff.AttributeDifference;

/**
 * General interface to ignore changes during Diffing
 *
 * In principal, we create a diff for everything that differs, and put that into the result file. Then it is up to the
 * UI (GUI or CLI) to hide ignored differences – this is what this interface is for. This is pretty much inline with how
 * Git works – the diff is there, it just doesn't show up when ignored.
 *
 * For more details, see <a href="https://github.com/retest/recheck/wiki/How-Ignore-works-in-recheck" target="_top">the
 * GitHub wiki</a>.
 *
 * Can be implemented by reading a file, or by an implementation delivered by the user.
 *
 * @deprecated As of release 1.1.0, replaced by {@link de.retest.recheck.ignore.Filter}
 *
 */
@Deprecated
public interface ShouldIgnore extends Filter {

	/**
	 * Returns <code>true</code> if the element <em>and all of its child elements</em>, so essentially the whole subtree
	 * this element is the root of, should be completely ignored (all attributes of all elements, whether elements are
	 * added or removed).
	 *
	 * @param element
	 *            The element in question.
	 * @return <code>true</code> if the given element should be completely ignored.
	 * @deprecated As of release 1.1.0, replaced by {@link de.retest.recheck.ignore.Filter#matches(Element)}
	 */
	@Deprecated
	boolean shouldIgnoreElement( final Element element );

	/**
	 * Returns <code>true</code> if the given attribute difference as specified by the triple (attribute-key,
	 * expectedValue, actualValue) should be ignored for the given element as specified by its
	 * {@link IdentifyingAttributes}.
	 *
	 * Note that for some elements all values of a given attribute key could be ignored, or an attribute key for all
	 * elements. But sometimes one wants to specify that a certain difference is meaningless, such as
	 * <code>Times Roman</code> vs. <code>Times New Roman</code> for font-family or a 5px difference for outline.
	 *
	 * @param element
	 *            The element in question.
	 * @param attributeDifference
	 *            The attribute difference for the given element.
	 * @return <code>true</code> if the given attribute difference should be ignored.
	 * @deprecated As of release 1.1.0, replaced by
	 *             {@link de.retest.recheck.ignore.Filter#matches(Element, AttributeDifference)}
	 */
	@Deprecated
	boolean shouldIgnoreAttributeDifference( final Element element, AttributeDifference attributeDifference );

	@Override
	default boolean matches( final Element element ) {
		return shouldIgnoreElement( element );
	}

	@Override
	default boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return shouldIgnoreAttributeDifference( element, attributeDifference );
	}

	public static final ShouldIgnore IGNORE_NOTHING = new ShouldIgnore() {

		@Override
		public boolean shouldIgnoreElement( final Element element ) {
			return false;
		}

		@Override
		public boolean shouldIgnoreAttributeDifference( final Element element,
				final AttributeDifference attributeDifference ) {
			return false;
		}

		@Override
		public boolean matches( final AttributeDifference attributeDifference ) {
			return false;
		}
	};
}
