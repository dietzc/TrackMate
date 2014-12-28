package fiji.plugin.trackmate.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.OutputAlgorithm;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import fiji.plugin.trackmate.tracking.TrackableObject;

public class FromContinuousBranches< T extends TrackableObject< T >> implements
		OutputAlgorithm< SimpleWeightedGraph< T, DefaultWeightedEdge >>, Benchmark
{

	private static final String BASE_ERROR_MSG = "[FromContinuousBranches] ";

	private long processingTime;

	private final Collection< List< T >> branches;

	private final Collection< List< T >> links;

	private String errorMessage;

	private SimpleWeightedGraph< T, DefaultWeightedEdge > graph;

	public FromContinuousBranches( final Collection< List< T >> branches,
			final Collection< List< T >> links )
	{
		this.branches = branches;
		this.links = links;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public boolean checkInput()
	{
		final long start = System.currentTimeMillis();
		if ( null == branches )
		{
			errorMessage = BASE_ERROR_MSG + "branches are null.";
			return false;
		}
		if ( null == links )
		{
			errorMessage = BASE_ERROR_MSG + "links are null.";
			return false;
		}
		for ( final List< T > link : links )
		{
			if ( link.size() != 2 )
			{
				errorMessage = BASE_ERROR_MSG
						+ "A link is not made of two spots.";
				return false;
			}
			if ( !checkIfInBranches( link.get( 0 ) ) )
			{
				errorMessage = BASE_ERROR_MSG
						+ "A spot in a link is not present in the branch collection: "
						+ link.get( 0 ) + " in the link " + link.get( 0 ) + "-"
						+ link.get( 1 ) + ".";
				return false;
			}
			if ( !checkIfInBranches( link.get( 1 ) ) )
			{
				errorMessage = BASE_ERROR_MSG
						+ "A spot in a link is not present in the branch collection: "
						+ link.get( 1 ) + " in the link " + link.get( 0 ) + "-"
						+ link.get( 1 ) + ".";
				return false;
			}
		}
		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public boolean process()
	{
		final long start = System.currentTimeMillis();

		graph = new SimpleWeightedGraph< T, DefaultWeightedEdge >(
				DefaultWeightedEdge.class );
		for ( final List< T > branch : branches )
		{
			for ( final T spot : branch )
			{
				graph.addVertex( spot );
			}
		}

		for ( final List< T > branch : branches )
		{
			final Iterator< T > it = branch.iterator();
			T previous = it.next();
			while ( it.hasNext() )
			{
				final T spot = it.next();
				graph.addEdge( previous, spot );
				previous = spot;
			}
		}

		for ( final List< T > link : links )
		{
			graph.addEdge( link.get( 0 ), link.get( 1 ) );
		}

		final long end = System.currentTimeMillis();
		processingTime = end - start;
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public SimpleWeightedGraph< T, DefaultWeightedEdge > getResult()
	{
		return graph;
	}

	private final boolean checkIfInBranches( final T spot )
	{
		for ( final List< T > branch : branches )
		{
			if ( branch.contains( spot ) ) { return true; }
		}
		return false;
	}

}
