/**
 * 
 */
package fiji.plugin.trackmate.graph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.interfaces.TrackableObject;

public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<TrackableObject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<TrackableObject, DefaultWeightedEdge> g, TrackableObject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(TrackableObject vertex) {
    	
    	int ts = vertex.getFeature(TrackableObject.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            TrackableObject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(TrackableObject.FRAME).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}