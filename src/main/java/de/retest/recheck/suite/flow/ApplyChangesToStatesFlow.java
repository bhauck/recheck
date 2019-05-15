package de.retest.recheck.suite.flow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.retest.recheck.ignore.Filter;
import de.retest.recheck.persistence.GoldenMasterProvider;
import de.retest.recheck.persistence.GoldenMasterProviderImpl;
import de.retest.recheck.persistence.NoGoldenMasterFoundException;
import de.retest.recheck.persistence.Persistence;
import de.retest.recheck.ui.descriptors.SutState;
import de.retest.recheck.ui.review.ActionChangeSet;
import de.retest.recheck.ui.review.SuiteChangeSet;
import de.retest.recheck.ui.review.TestChangeSet;

public class ApplyChangesToStatesFlow {

	private static final Logger logger = LoggerFactory.getLogger( ApplyChangesToStatesFlow.class );

	private final GoldenMasterProvider goldenMasterProvider;

	public static List<String> apply( final Persistence<SutState> persistence, final SuiteChangeSet acceptedChanges )
			throws NoGoldenMasterFoundException {
		return new ApplyChangesToStatesFlow( persistence ).apply( acceptedChanges );
	}

	public static List<String> apply( final Persistence<SutState> persistence, final SuiteChangeSet acceptedChanges,
			final Filter filter ) throws NoGoldenMasterFoundException {
		return new ApplyChangesToStatesFlow( persistence ).apply( acceptedChanges, filter );
	}

	private ApplyChangesToStatesFlow( final Persistence<SutState> persistence ) {
		goldenMasterProvider = new GoldenMasterProviderImpl( persistence );
	}

	private List<String> apply( final SuiteChangeSet acceptedChanges ) throws NoGoldenMasterFoundException {
		final List<String> updatedFiles = new ArrayList<>();
		for ( final TestChangeSet testChangeSet : acceptedChanges.getTestChangeSets() ) {
			updatedFiles.addAll( apply( testChangeSet ) );
		}
		return updatedFiles;
	}

	private List<String> apply( final SuiteChangeSet acceptedChanges, final Filter filter )
			throws NoGoldenMasterFoundException {
		final List<String> updatedFiles = new ArrayList<>();
		for ( final TestChangeSet testChangeSet : acceptedChanges.getTestChangeSets() ) {
			updatedFiles.addAll( apply( testChangeSet, filter ) );
		}
		return updatedFiles;
	}

	private List<String> apply( final TestChangeSet testChangeSet ) throws NoGoldenMasterFoundException {
		if ( testChangeSet.isEmpty() ) {
			return Collections.emptyList();
		}
		final List<String> updatedFiles = new ArrayList<>();
		for ( final ActionChangeSet changeSet : testChangeSet.getActionChangeSets() ) {
			updatedFiles.addAll( apply( changeSet ) );
		}
		// TODO: RET-1274 will remove the initial change set
		if ( testChangeSet.containsInitialStateChangeSet() ) {
			final ActionChangeSet changeSet = testChangeSet.getInitialStateChangeSet();
			updatedFiles.addAll( apply( changeSet ) );
		}
		return updatedFiles;
	}

	private List<String> apply( final TestChangeSet testChangeSet, final Filter filter )
			throws NoGoldenMasterFoundException {
		if ( testChangeSet.isEmpty() ) {
			return Collections.emptyList();
		}
		final List<String> updatedFiles = new ArrayList<>();
		for ( final ActionChangeSet changeSet : testChangeSet.getActionChangeSets() ) {
			updatedFiles.addAll( apply( changeSet, filter ) );
		}
		// TODO: RET-1274 will remove the initial change set
		if ( testChangeSet.containsInitialStateChangeSet() ) {
			final ActionChangeSet changeSet = testChangeSet.getInitialStateChangeSet();
			updatedFiles.addAll( apply( changeSet, filter ) );
		}
		return updatedFiles;
	}

	private List<String> apply( final ActionChangeSet actionChangeSet ) throws NoGoldenMasterFoundException {
		if ( actionChangeSet.isEmpty() ) {
			return Collections.emptyList();
		}
		final File file = goldenMasterProvider.getGoldenMaster( actionChangeSet.getGoldenMasterPath() );
		final SutState oldState = goldenMasterProvider.loadGoldenMaster( file );
		final SutState newState = oldState.applyChanges( actionChangeSet );
		if ( newState.equals( oldState ) ) {
			logger.debug( "SutState {} did not change after applying changes, so not persisting it...", oldState );
			return Collections.emptyList();
		}
		goldenMasterProvider.saveGoldenMaster( file, newState );
		return Collections.singletonList( actionChangeSet.getDescription() );
	}

	private List<String> apply( final ActionChangeSet actionChangeSet, final Filter filter )
			throws NoGoldenMasterFoundException {
		if ( actionChangeSet.isEmpty() ) {
			return Collections.emptyList();
		}
		final File file = goldenMasterProvider.getGoldenMaster( actionChangeSet.getGoldenMasterPath() );
		final SutState oldState = goldenMasterProvider.loadGoldenMaster( file );
		final SutState newState = oldState.applyChanges( actionChangeSet, filter );
		if ( newState.equals( oldState ) ) {
			logger.debug( "SutState {} did not change after applying changes, so not persisting it...", oldState );
			return Collections.emptyList();
		}
		goldenMasterProvider.saveGoldenMaster( file, newState );
		return Collections.singletonList( actionChangeSet.getDescription() );
	}
}
