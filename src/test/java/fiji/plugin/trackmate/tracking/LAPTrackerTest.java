package fiji.plugin.trackmate.tracking;

import static fiji.plugin.trackmate.tracking.TrackerKeys.KEY_ALLOW_GAP_CLOSING;
import static fiji.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_FEATURE_PENALTIES;
import static fiji.plugin.trackmate.tracking.TrackerKeys.KEY_LINKING_MAX_DISTANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.junit.Test;

import fiji.plugin.trackmate.ObjectCollection;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackableObjectCollection;
import fiji.plugin.trackmate.features.spot.SpotIntensityAnalyzerFactory;
import fiji.plugin.trackmate.interfaces.TrackableObject;

public class LAPTrackerTest
{

	/**
	 * Standard tracking
	 */
	@Test
	public void testTracking()
	{

		final int nFrames = 100;

		// Create 2 "lines" of spots, keeping track of the manual tracks for
		// later testing
		final List< TrackableObject > group1 = new ArrayList< TrackableObject >( nFrames );
		final List< TrackableObject > group2 = new ArrayList< TrackableObject >( nFrames );
		final TrackableObjectCollection spotCollection = new ObjectCollection();
		for ( int i = 0; i < nFrames; i++ )
		{
			final double[] coords1 = new double[] { 1d, 1d * i, 0 };
			final double[] coords2 = new double[] { 2d, 1d * i, 0 };

			final TrackableObject spot1 = new Spot( coords1[ 0 ], coords1[ 1 ], coords1[ 2 ], 1d, -1d );
			final TrackableObject spot2 = new Spot( coords2[ 0 ], coords2[ 1 ], coords2[ 2 ], 1d, -1d );
			spot1.putFeature( Spot.POSITION_T, Double.valueOf( i ) );
			spot2.putFeature( Spot.POSITION_T, Double.valueOf( i ) );
			spot1.setName( "G1T" + i );
			spot2.setName( "G2T" + i );

			group1.add( spot1 );
			group2.add( spot2 );

			final List< TrackableObject > spots = new ArrayList< TrackableObject >( 2 );
			spots.add( spot1 );
			spots.add( spot2 );
			spotCollection.put( i, spots );
		}

		// Make them all visible
		spotCollection.setVisible( true );

		final List< List< TrackableObject >> groups = new ArrayList< List< TrackableObject >>( 2 );
		groups.add( group1 );
		groups.add( group2 );

		// Set the tracking settings
		final Map< String, Object > trackerSettings = LAPUtils.getDefaultLAPSettingsMap();
		trackerSettings.put( KEY_LINKING_MAX_DISTANCE, 2d );
		trackerSettings.put( KEY_ALLOW_GAP_CLOSING, false );

		// Instantiate tracker
		final LAPTracker tracker = new LAPTracker( spotCollection, trackerSettings );

		// Check process
		if ( !tracker.checkInput() || !tracker.process() )
		{
			fail( tracker.getErrorMessage() );
		}

		// Check results
		final SimpleWeightedGraph< TrackableObject, DefaultWeightedEdge > graph = tracker.getResult();
		verifyTracks( graph, groups, nFrames );
	}

	/**
	 * This time we try to track spots with different intensities and see if we
	 * can put them back right
	 */
	@Test
	public void testTrackingWithFeature()
	{

		final int nFrames = 100;

		// Create 2 "lines" of spots, keeping track of the manual tracks for
		// later testing
		final List< TrackableObject > group1 = new ArrayList< TrackableObject >( nFrames );
		final List< TrackableObject > group2 = new ArrayList< TrackableObject >( nFrames );
		final TrackableObjectCollection spotCollection = new ObjectCollection();
		for ( int i = 0; i < nFrames; i++ )
		{
			final double[] coords1 = new double[] { ( i % 2 ), 1d * i, 0 };
			final double[] coords2 = new double[] { ( ( i + 1 ) % 2 ), 1d * i, 0 };

			final Spot spot1 = new Spot( coords1[ 0 ], coords1[ 1 ], coords1[ 2 ], 1d, -1d );
			final Spot spot2 = new Spot( coords2[ 0 ], coords2[ 1 ], coords2[ 2 ], 1d, -1d );
			spot1.putFeature( Spot.POSITION_T, Double.valueOf( i ) );
			spot2.putFeature( Spot.POSITION_T, Double.valueOf( i ) );
			spot1.setName( "G1T" + i );
			spot2.setName( "G2T" + i );
			// For this test, we need to put a different feature for each track
			spot1.putFeature( SpotIntensityAnalyzerFactory.MEAN_INTENSITY, Double.valueOf( 100 ) );
			spot2.putFeature( SpotIntensityAnalyzerFactory.MEAN_INTENSITY, Double.valueOf( 200 ) );

			group1.add( spot1 );
			group2.add( spot2 );

			final List< TrackableObject > spots = new ArrayList< TrackableObject >( 2 );
			spots.add( spot1 );
			spots.add( spot2 );
			spotCollection.put( i, spots );
		}

		// Make them all visible
		spotCollection.setVisible( true );

		final List< List< TrackableObject >> groups = new ArrayList< List< TrackableObject >>( 2 );
		groups.add( group1 );
		groups.add( group2 );

		// Set the tracking settings
		final Map< String, Object > trackerSettings = LAPUtils.getDefaultLAPSettingsMap();
		trackerSettings.put( KEY_LINKING_MAX_DISTANCE, 2. );
		trackerSettings.put( KEY_ALLOW_GAP_CLOSING, false );
		final StringBuilder errorHolder = new StringBuilder();
		final boolean ok = LAPUtils.addFeaturePenaltyToSettings( trackerSettings, KEY_LINKING_FEATURE_PENALTIES, SpotIntensityAnalyzerFactory.MEAN_INTENSITY, 1d, errorHolder );
		if ( !ok )
		{
			fail( errorHolder.toString() );
		}

		// Instantiate tracker
		final LAPTracker tracker = new LAPTracker( spotCollection, trackerSettings );

		// Check process
		if ( !tracker.checkInput() || !tracker.process() )
		{
			fail( tracker.getErrorMessage() );
		}

		// Check results
		final SimpleWeightedGraph< TrackableObject, DefaultWeightedEdge > graph = tracker.getResult();
		verifyTracks( graph, groups, nFrames );
	}

	private static void verifyTracks( final SimpleWeightedGraph< TrackableObject, DefaultWeightedEdge > graph, final List< List< TrackableObject >> groups, final int nFrames )
	{

		// Check that we have the right number of vertices
		assertEquals( "The tracking result graph has the wrong number of vertices, ", 2 * nFrames, graph.vertexSet().size() );

		// Check that we have the right number of tracks
		final ConnectivityInspector< TrackableObject, DefaultWeightedEdge > inspector = new ConnectivityInspector< TrackableObject, DefaultWeightedEdge >( graph );
		final int nTracks = inspector.connectedSets().size();
		assertEquals( "Did not get the right number of tracks, ", 2, nTracks );

		// Check that the tracks are right: the group1 must contain exactly the
		// spot of one track
		for ( final List< TrackableObject > group : groups )
		{
			// System.out.print("\nTrack: ");
			final Set< TrackableObject > track1 = inspector.connectedSetOf( group.get( 0 ) );
			for ( final TrackableObject spot : group )
			{
				final boolean removed = track1.remove( spot );
				assertTrue( "Failed to find spot " + spot + " in track.", removed );
				// System.out.print(spot+"-");
			}
			assertEquals( "Track has some unexpected spots", 0, track1.size() );
		}

	}
}
