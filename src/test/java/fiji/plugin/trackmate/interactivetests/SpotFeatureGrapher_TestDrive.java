package fiji.plugin.trackmate.interactivetests;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jdom2.JDOMException;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.scijava.util.AppUtils;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.ObjectCollection;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.TrackableObjectCollection;
import fiji.plugin.trackmate.features.SpotFeatureGrapher;
import fiji.plugin.trackmate.features.track.TrackIndexAnalyzer;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.io.TmXmlReader;
import fiji.plugin.trackmate.visualization.trackscheme.TrackScheme;

public class SpotFeatureGrapher_TestDrive
{

	public static void main( final String[] args ) throws JDOMException, IOException
	{

		// Load objects
		final File file = new File( AppUtils.getBaseDirectory( TrackMate.class ), "samples/FakeTracks.xml" );
		final TmXmlReader reader = new TmXmlReader( file );
		final Model model = reader.getModel();

		final HashSet< String > Y = new HashSet< String >( 1 );
		Y.add( TrackableObject.POSITION_T );
		final List< TrackableObject > spots = new ArrayList< TrackableObject >( model.getSpots().getNObjects( true ) );
		for ( final Iterator< TrackableObject > it = model.getSpots().iterator( true ); it.hasNext(); )
		{
			spots.add( it.next() );
		}

		final SpotFeatureGrapher grapher = new SpotFeatureGrapher( TrackableObject.POSITION_X, Y, spots, model );
		grapher.render();

		final TrackIndexAnalyzer analyzer = new TrackIndexAnalyzer();
		analyzer.process( model.getTrackModel().trackIDs( true ), model );
		// needed for trackScheme
		final TrackScheme trackScheme = new TrackScheme( model, new SelectionModel( model ) );
		trackScheme.render();

	}

	/**
	 * Another example: spots that go in spiral
	 */
	@SuppressWarnings( "unused" )
	private static Model getSpiralModel()
	{

		final int N_SPOTS = 50;
		final List< TrackableObject > spots = new ArrayList< TrackableObject >( N_SPOTS );
		final TrackableObjectCollection sc = new ObjectCollection();
		for ( int i = 0; i < N_SPOTS; i++ )
		{
			final double x = 100d + 100 * i / 100. * Math.cos( i / 100. * 5 * 2 * Math.PI );
			final double y = 100d + 100 * i / 100. * Math.sin( i / 100. * 5 * 2 * Math.PI );
			final double z = 0d;
			final TrackableObject spot = new Spot( x, y, z, 2d, -1d );
			spot.putFeature( TrackableObject.POSITION_T, Double.valueOf( i ) );

			spots.add( spot );

			final List< TrackableObject > ts = new ArrayList< TrackableObject >( 1 );
			ts.add( spot );
			sc.put( i, ts );
			spot.putFeature( TrackableObjectCollection.VISIBILITY, TrackableObjectCollection.ONE );
		}

		final Model model = new Model();
		model.setSpots( sc, false );

		final SimpleWeightedGraph< TrackableObject, DefaultWeightedEdge > graph = new SimpleWeightedGraph< TrackableObject, DefaultWeightedEdge >( DefaultWeightedEdge.class );
		for ( final TrackableObject spot : spots )
		{
			graph.addVertex( spot );
		}
		TrackableObject source = spots.get( 0 );
		for ( int i = 1; i < N_SPOTS; i++ )
		{
			final TrackableObject target = spots.get( i );
			final DefaultWeightedEdge edge = graph.addEdge( source, target );
			graph.setEdgeWeight( edge, 1 );
			source = target;
		}
		model.setTracks( graph, true );

		return model;
	}
}
