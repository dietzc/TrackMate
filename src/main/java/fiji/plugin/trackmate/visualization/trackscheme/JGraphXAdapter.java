package fiji.plugin.trackmate.visualization.trackscheme;

import java.util.HashMap;
import java.util.Set;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.view.mxGraph;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.interfaces.TrackableObject;

public class JGraphXAdapter extends mxGraph implements GraphListener<Spot, DefaultWeightedEdge> {

	private HashMap<TrackableObject, mxCell> 					vertexToCellMap 	= new HashMap<TrackableObject, mxCell>();
	private HashMap<DefaultWeightedEdge, mxCell> 	edgeToCellMap 		= new HashMap<DefaultWeightedEdge, mxCell>();
	private HashMap<mxCell, TrackableObject>					cellToVertexMap		= new HashMap<mxCell, TrackableObject>();
	private HashMap<mxCell, DefaultWeightedEdge>	cellToEdgeMap		= new HashMap<mxCell, DefaultWeightedEdge>();
	private Model tmm;

	/*
	 * CONSTRUCTOR
	 */

	public JGraphXAdapter(final Model tmm) {
		super();
		this.tmm = tmm;
		insertTrackCollection(tmm);
	}

	/*
	 * METHODS
	 */
	
	/**
	 * Overridden method so that when a label is changed, we change the target spot's name.
	 */
	@Override
	public void cellLabelChanged(Object cell, Object value, boolean autoSize) {
		model.beginUpdate();
		try {
			TrackableObject spot = cellToVertexMap.get(cell);
			if (null == spot)
				return;
			String str = (String) value;
			spot.setName(str);
			getModel().setValue(cell, str);

			if (autoSize) {
				cellSizeUpdated(cell, false);
			}
		} finally {
			model.endUpdate();
		}
	}

	public mxCell addJGraphTVertex(TrackableObject vertex) {
		if (vertexToCellMap.containsKey(vertex)) {
			// cell for Spot already existed, skip creation and return original cell.
			return vertexToCellMap.get(vertex);
		}
		mxCell cell = null;
		getModel().beginUpdate();
		try {
			cell = new mxCell(vertex, new mxGeometry(), "");
			cell.setVertex(true);
			cell.setId(null);
			cell.setValue(vertex.getName());
			addCell(cell, defaultParent);
			vertexToCellMap.put(vertex, cell);
			cellToVertexMap.put(cell, vertex);
		} finally {
			getModel().endUpdate();
		}
		return cell;
	}

	public mxCell addJGraphTEdge(DefaultWeightedEdge edge) {
		if (edgeToCellMap.containsKey(edge)) {
			// cell for edge already existed, skip creation and return original cell.
			return edgeToCellMap.get(edge);
		}
		mxCell cell = null;
		getModel().beginUpdate();
		try {
			TrackableObject source = tmm.getTrackModel().getEdgeSource(edge);
			TrackableObject target = tmm.getTrackModel().getEdgeTarget(edge);				
			cell = new mxCell(edge);
			cell.setEdge(true);
			cell.setId(null);
			cell.setValue(String.format("%.1f", tmm.getTrackModel().getEdgeWeight(edge)));
			cell.setGeometry(new mxGeometry());
			cell.getGeometry().setRelative(true);
			addEdge(cell, defaultParent, vertexToCellMap.get(source),  vertexToCellMap.get(target), null);
			edgeToCellMap.put(edge, cell);
			cellToEdgeMap.put(cell, edge);
		} finally {
			getModel().endUpdate();
		}
		return cell;
	}
	
	public void mapEdgeToCell(DefaultWeightedEdge edge, mxCell cell) {
		cellToEdgeMap.put(cell, edge);
		edgeToCellMap.put(edge, cell);
	}
	
	public TrackableObject getSpotFor(mxICell cell) {
		return cellToVertexMap.get(cell);
	}
	
	public DefaultWeightedEdge getEdgeFor(mxICell cell) {
		return cellToEdgeMap.get(cell);
	}
	
	public mxCell getCellFor(TrackableObject spot) {
		return vertexToCellMap.get(spot);
	}
	
	public mxCell getCellFor(DefaultWeightedEdge edge) {
		return edgeToCellMap.get(edge);
	}
	
	public Set<mxCell> getVertexCells() {
		return cellToVertexMap.keySet();
	}
	
	public Set<mxCell> getEdgeCells() {
		return cellToEdgeMap.keySet();
	}
	
	public void removeMapping(TrackableObject spot) {
		mxICell cell = vertexToCellMap.remove(spot);
		cellToVertexMap.remove(cell);
	}
	
	public void removeMapping(DefaultWeightedEdge edge) {
		mxICell cell = edgeToCellMap.remove(edge);
		cellToEdgeMap.remove(cell);
	}

	

	

	/*
	 * GRAPH LISTENER
	 */


	@Override
	public void vertexAdded(GraphVertexChangeEvent<Spot> e) {
		addJGraphTVertex(e.getVertex());
	}

	@Override
	public void vertexRemoved(GraphVertexChangeEvent<Spot> e) {
		mxCell cell = vertexToCellMap.remove(e.getVertex());
		removeCells(new Object[] { cell } );
	}

	@Override
	public void edgeAdded(GraphEdgeChangeEvent<Spot, DefaultWeightedEdge> e) {
		addJGraphTEdge(e.getEdge());
	}

	@Override
	public void edgeRemoved(GraphEdgeChangeEvent<Spot, DefaultWeightedEdge> e) {
		mxICell cell = edgeToCellMap.remove(e.getEdge());
		removeCells(new Object[] { cell } );
	}

	/*
	 * PRIVATE METHODS
	 */

	/**
	 * Only insert spot and edges belonging to visible tracks. 
	 * Any other spot or edges will be ignored by the whole trackscheme
	 * framework, and if they are needed, they will have to be imported "by hand".
	 */
	private void insertTrackCollection(final Model tmm) {
		model.beginUpdate();
		try {
			for (Integer trackID : tmm.getTrackModel().trackIDs(true)) {
				
				for (TrackableObject vertex : tmm.getTrackModel().trackSpots(trackID)) {
					addJGraphTVertex(vertex);
				}

				for (DefaultWeightedEdge edge : tmm.getTrackModel().trackEdges(trackID)) {
					addJGraphTEdge(edge);
				}
			
			}
		} finally {
			model.endUpdate();
		}
		
	}



}