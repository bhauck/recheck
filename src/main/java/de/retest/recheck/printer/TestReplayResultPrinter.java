package de.retest.recheck.printer;

import java.util.stream.Collectors;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.report.ActionReplayResult;
import de.retest.recheck.report.TestReplayResult;
import de.retest.recheck.ui.DefaultValueFinder;

public class TestReplayResultPrinter implements Printer<TestReplayResult> {

	private final DefaultValueFinderProvider provider;
	private final Filter filter;

	public TestReplayResultPrinter( final DefaultValueFinderProvider provider, final Filter filter ) {
		this.provider = provider;
		this.filter = filter;
	}

	@Override
	public String toString( final TestReplayResult difference, final String indent ) {
		return indent + createDescription( difference ) + "\n" + createDifferences( difference, indent );
	}

	private String createDescription( final TestReplayResult result ) {
		final String name = result.getName();
		final int differences = result.getDifferences( filter ).size();
		final int states = result.getActionReplayResults().size();
		return String.format( "Test '%s' has %d difference(s) in %d state(s):", name, differences, states );
	}

	private String createDifferences( final TestReplayResult difference, final String indent ) {
		return difference.getActionReplayResults().stream() //
				.map( a -> formatAction( a, indent ) ) //
				.collect( Collectors.joining( "\n" ) );
	}

	private String formatAction( final ActionReplayResult result, final String indent ) {
		final DefaultValueFinder finder = provider.findForAction( result.getDescription() );
		final ActionReplayResultPrinter printer = new ActionReplayResultPrinter( finder, filter );
		return printer.toString( result, indent );
	}
}
