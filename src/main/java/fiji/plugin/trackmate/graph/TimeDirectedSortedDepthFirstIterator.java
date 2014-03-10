/**
 * 
 */
package fiji.plugin.trackmate.graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.interfaces.TrackableObject;

public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<TrackableObject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(Graph<TrackableObject, DefaultWeightedEdge> g, TrackableObject startVertex, Comparator<TrackableObject> comparator) {
		super(g, startVertex, comparator);
	}
	
	
	
    protected void addUnseenChildrenOf(Spot vertex) {
    	
    	// Retrieve target vertices, and sort them in a TreeSet
    	TreeSet<TrackableObject> sortedChildren = new TreeSet<TrackableObject>(comparator);
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	Map<TrackableObject, DefaultWeightedEdge> localEdges = new HashMap<TrackableObject, DefaultWeightedEdge>();
    	
    	int ts = vertex.getFeature(TrackableObject.FRAME).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
        	
        	TrackableObject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	int tt = oppositeV.getFeature(TrackableObject.FRAME).intValue();
        	if (tt <= ts) {
        		continue;
        	}
        	
        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }
        
        Iterator<TrackableObject> it = sortedChildren.descendingIterator();
        while (it.hasNext()) {
        	TrackableObject child = it.next();
			
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }

	
	
}