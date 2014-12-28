package fiji.plugin.trackmate.tracking.sparselap.costmatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.tracking.TrackableObject;
import fiji.plugin.trackmate.util.TrackableObjectUtils;

public class GraphSegmentSplitter< T extends TrackableObject< T >>
{
	private final List< T > segmentStarts;

	private final List< T > segmentEnds;

	private final List< List< T >> segmentMiddles;

	public GraphSegmentSplitter(
			final UndirectedGraph< T, DefaultWeightedEdge > graph,
			final boolean findMiddlePoints )
	{
		final ConnectivityInspector< T, DefaultWeightedEdge > connectivity = new ConnectivityInspector< T, DefaultWeightedEdge >(
				graph );
		final List< Set< T >> connectedSets = connectivity.connectedSets();
		final Comparator< TrackableObject< ? >> framecomparator = TrackableObjectUtils
				.frameComparator();

		segmentStarts = new ArrayList< T >( connectedSets.size() );
		segmentEnds = new ArrayList< T >( connectedSets.size() );
		if ( findMiddlePoints )
		{
			segmentMiddles = new ArrayList< List< T >>( connectedSets.size() );
		}
		else
		{
			segmentMiddles = Collections.emptyList();
		}

		for ( final Set< T > set : connectedSets )
		{
			if ( set.size() < 2 )
			{
				continue;
			}

			final List< T > list = new ArrayList< T >( set );
			Collections.sort( list, framecomparator );

			segmentEnds.add( list.remove( list.size() - 1 ) );
			segmentStarts.add( list.remove( 0 ) );
			if ( findMiddlePoints )
			{
				segmentMiddles.add( list );
			}
		}
	}

	public List< T > getSegmentEnds()
	{
		return segmentEnds;
	}

	public List< List< T >> getSegmentMiddles()
	{
		return segmentMiddles;
	}

	public List< T > getSegmentStarts()
	{
		return segmentStarts;
	}

}
