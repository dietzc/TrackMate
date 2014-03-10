package fiji.plugin.trackmate.interfaces;

import net.imglib2.algorithm.OutputAlgorithm;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import fiji.plugin.trackmate.Logger;

public interface Tracker extends OutputAlgorithm<SimpleWeightedGraph<TrackableObject, DefaultWeightedEdge>> {
	
	public void setLogger(final Logger logger);

}
