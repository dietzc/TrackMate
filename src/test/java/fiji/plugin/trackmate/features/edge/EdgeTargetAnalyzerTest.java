package fiji.plugin.trackmate.features.edge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.Test;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.ModelChangeEvent;
import fiji.plugin.trackmate.ModelChangeListener;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.features.edges.EdgeTargetAnalyzer;

public class EdgeTargetAnalyzerTest
{

	private static final int N_TRACKS = 10;

	private static final int DEPTH = 9; // must be at least 6 to avoid tracks
										// too shorts - may make this test fail
										// sometimes

	private Model< Spot > model;

	private HashMap< DefaultWeightedEdge, Spot > edgeTarget;

	private HashMap< DefaultWeightedEdge, Spot > edgeSource;

	private HashMap< DefaultWeightedEdge, Double > edgeCost;

	@Before
	public void setUp()
	{
		edgeSource = new HashMap< DefaultWeightedEdge, Spot >();
		edgeTarget = new HashMap< DefaultWeightedEdge, Spot >();
		edgeCost = new HashMap< DefaultWeightedEdge, Double >();

		model = new Model< Spot >();
		model.beginUpdate();
		try
		{

			for ( int i = 0; i < N_TRACKS; i++ )
			{

				Spot previous = null;

				for ( int j = 0; j <= DEPTH; j++ )
				{
					final Spot spot = new Spot( 0d, 0d, 0d, 1d, -1d );
					model.addSpotTo( spot, j );
					if ( null != previous )
					{
						final DefaultWeightedEdge edge = model.addEdge( previous, spot, j );
						edgeSource.put( edge, previous );
						edgeTarget.put( edge, spot );
						edgeCost.put( edge, Double.valueOf( j ) );

					}
					previous = spot;
				}
			}

		}
		finally
		{
			model.endUpdate();
		}
	}

	@Test
	public final void testProcess()
	{
		// Process model
		final EdgeTargetAnalyzer< Spot > analyzer = new EdgeTargetAnalyzer< Spot >();
		analyzer.process( model.getTrackModel().edgeSet(), model );

		// Collect features
		for ( final DefaultWeightedEdge edge : model.getTrackModel().edgeSet() )
		{
			assertEquals( edgeSource.get( edge ).ID(), model.getFeatureModel().getEdgeFeature( edge, EdgeTargetAnalyzer.SPOT_SOURCE_ID ).intValue() );
			assertEquals( edgeTarget.get( edge ).ID(), model.getFeatureModel().getEdgeFeature( edge, EdgeTargetAnalyzer.SPOT_TARGET_ID ).intValue() );
			assertEquals( edgeCost.get( edge ).doubleValue(), model.getFeatureModel().getEdgeFeature( edge, EdgeTargetAnalyzer.EDGE_COST ).doubleValue(), Double.MIN_VALUE );
		}
	}

	@Test
	public final void testModelChanged()
	{
		// Initial calculation
		final TestEdgeTargetAnalyzer analyzer = new TestEdgeTargetAnalyzer();
		analyzer.process( model.getTrackModel().edgeSet(), model );

		// Prepare listener
		model.addModelChangeListener( new ModelChangeListener< Spot >()
		{
			@Override
			public void modelChanged( final ModelChangeEvent< Spot > event )
			{
				final HashSet< DefaultWeightedEdge > edgesToUpdate = new HashSet< DefaultWeightedEdge >();
				for ( final DefaultWeightedEdge edge : event.getEdges() )
				{
					if ( event.getEdgeFlag( edge ) != ModelChangeEvent.FLAG_EDGE_REMOVED )
					{
						edgesToUpdate.add( edge );
					}
				}
				if ( analyzer.isLocal() )
				{

					analyzer.process( edgesToUpdate, model );

				}
				else
				{

					// Get the all the edges of the track they belong to
					final HashSet< DefaultWeightedEdge > globalEdgesToUpdate = new HashSet< DefaultWeightedEdge >();
					for ( final DefaultWeightedEdge edge : edgesToUpdate )
					{
						final Integer motherTrackID = model.getTrackModel().trackIDOf( edge );
						globalEdgesToUpdate.addAll( model.getTrackModel().trackEdges( motherTrackID ) );
					}
					analyzer.process( globalEdgesToUpdate, model );
				}
			}
		} );

		// Change the cost of one edge
		final DefaultWeightedEdge edge = edgeSource.keySet().iterator().next();
		final double val = 67.43;
		model.beginUpdate();
		try
		{
			model.setEdgeWeight( edge, val );
		}
		finally
		{
			model.endUpdate();
		}

		// We must have received only one edge to analyzer
		assertTrue( analyzer.hasBeenRun );
		assertEquals( 1, analyzer.edges.size() );
		final DefaultWeightedEdge analyzedEdge = analyzer.edges.iterator().next();
		assertEquals( edge, analyzedEdge );
		assertEquals( val, model.getFeatureModel().getEdgeFeature( edge, EdgeTargetAnalyzer.EDGE_COST ).doubleValue(), Double.MIN_VALUE );

	}

	private static class TestEdgeTargetAnalyzer extends EdgeTargetAnalyzer< Spot >
	{

		private boolean hasBeenRun = false;

		private Collection< DefaultWeightedEdge > edges;

		@Override
		public void process( final Collection< DefaultWeightedEdge > edges, final Model< Spot > model )
		{
			this.hasBeenRun = true;
			this.edges = edges;
			super.process( edges, model );
		}

	}
}
