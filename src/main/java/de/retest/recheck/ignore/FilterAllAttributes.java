package de.retest.recheck.ignore;

import de.retest.recheck.ui.descriptors.Element;
import de.retest.recheck.ui.diff.AttributeDifference;

public class FilterAllAttributes implements Filter {

	@Override
	public boolean matches( final Element element ) {
		return false;
	}

	@Override
	public boolean matches( final Element element, final AttributeDifference attributeDifference ) {
		return true;
	}

	@Override
	public boolean matches( final AttributeDifference attributeDifference ) {
		return true;
	}

}
