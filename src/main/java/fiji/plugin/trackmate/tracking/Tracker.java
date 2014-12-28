package fiji.plugin.trackmate.tracking;

import net.imglib2.algorithm.OutputAlgorithm;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import fiji.plugin.trackmate.Logger;

public interface Tracker< T extends TrackableObject< T >> extends OutputAlgorithm< SimpleWeightedGraph< T, DefaultWeightedEdge >>
{
	/**
	 * Sets the {@link Logger} instance that will receive messages from this
	 * {@link Tracker}.
	 *
	 * @param logger
	 *            the logger to echo messages to.
	 */
	public void setLogger( final Logger logger );

	void setNumThreads( int numThreads );

	int getNumThreads();

	void setNumThreads();

}
