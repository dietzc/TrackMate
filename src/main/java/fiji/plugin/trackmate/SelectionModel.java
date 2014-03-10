package fiji.plugin.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.GraphIterator;

import fiji.plugin.trackmate.interfaces.TrackableObject;

/**
 * A component of {@link Model} that handles spot and edges selection.
 * @author Jean-Yves Tinevez
 */
public class SelectionModel {

	private static final boolean DEBUG = false;

	/** The spot current selection. */
	private Set<TrackableObject> spotSelection = new HashSet<TrackableObject>();
	/** The edge current selection. */
	private Set<DefaultWeightedEdge> edgeSelection = new HashSet<DefaultWeightedEdge>();
	/** The list of listener listening to change in selection. */
	private List<SelectionChangeListener> selectionChangeListeners = new ArrayList<SelectionChangeListener>();

	private final Model model;

	/*
	 * DEFAULT VISIBILITY CONSTRUCTOR
	 */

	public SelectionModel(Model parent) {
		this.model = parent;
	}

	/*
	 * DEAL WITH SELECTION CHANGE LISTENER
	 */

	public boolean addSelectionChangeListener(SelectionChangeListener listener) {
		return selectionChangeListeners.add(listener);
	}

	public boolean removeSelectionChangeListener(SelectionChangeListener listener) {
		return selectionChangeListeners.remove(listener);
	}

	public List<SelectionChangeListener> getSelectionChangeListener() {
		return selectionChangeListeners;
	}

	/*
	 * SELECTION CHANGES
	 */

	public void clearSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing selection");
		// Prepare event
		Map<TrackableObject, Boolean> spotMap = new HashMap<TrackableObject, Boolean>(spotSelection.size());
		for (TrackableObject spot : spotSelection)
			spotMap.put(spot, false);
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<DefaultWeightedEdge, Boolean>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, spotMap, edgeMap);
		// Clear fields
		clearSpotSelection();
		clearEdgeSelection();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearSpotSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing spot selection");
		// Prepare event
		Map<TrackableObject, Boolean> spotMap = new HashMap<TrackableObject, Boolean>(spotSelection.size());
		for (TrackableObject spot : spotSelection)
			spotMap.put(spot, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, spotMap, null);
		// Clear field
		spotSelection.clear();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void clearEdgeSelection() {
		if (DEBUG)
			System.out.println("[SelectionModel] Clearing edge selection");
		// Prepare event
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<DefaultWeightedEdge, Boolean>(edgeSelection.size());
		for (DefaultWeightedEdge edge : edgeSelection)
			edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		// Clear field
		edgeSelection.clear();
		// Fire event
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addSpotToSelection(final TrackableObject spot) {
		if (!spotSelection.add(spot))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding spot " + spot + " to selection");
		Map<TrackableObject, Boolean> spotMap = new HashMap<TrackableObject, Boolean>(1);
		spotMap.put(spot, true);
		if (DEBUG)
			System.out.println("[SelectionModel] Seding event to listeners: "+selectionChangeListeners);
		SelectionChangeEvent event = new SelectionChangeEvent(this, spotMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeSpotFromSelection(final TrackableObject spot) {
		if (!spotSelection.remove(spot))
			return; // Do nothing was not already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing spot " + spot + " from selection");
		Map<TrackableObject, Boolean> spotMap = new HashMap<TrackableObject, Boolean>(1);
		spotMap.put(spot, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, spotMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addSpotToSelection(final Collection<TrackableObject> spots) {
		Map<TrackableObject, Boolean> spotMap = new HashMap<TrackableObject, Boolean>(spots.size());
		for (TrackableObject spot : spots) {
			if (spotSelection.add(spot)) {
				spotMap.put(spot, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding spot " + spot + " to selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, spotMap, null);
		if (DEBUG) 
			System.out.println("[SelectionModel] Seding event "+event.hashCode()+" to "+selectionChangeListeners.size()+" listeners: "+selectionChangeListeners);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeSpotFromSelection(final Collection<TrackableObject> spots) {
		Map<TrackableObject, Boolean> spotMap = new HashMap<TrackableObject, Boolean>(spots.size());
		for (TrackableObject spot : spots) {
			if (spotSelection.remove(spot)) {
				spotMap.put(spot, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing spot " + spot + " from selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, spotMap, null);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void addEdgeToSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.add(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<DefaultWeightedEdge, Boolean>(1);
		edgeMap.put(edge, true);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void removeEdgeFromSelection(final DefaultWeightedEdge edge) {
		if (!edgeSelection.remove(edge))
			return; // Do nothing if already present in selection
		if (DEBUG)
			System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<DefaultWeightedEdge, Boolean>(1);
		edgeMap.put(edge, false);
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);

	}

	public void addEdgeToSelection(final Collection<DefaultWeightedEdge> edges) {
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<DefaultWeightedEdge, Boolean>(edges.size());
		for (DefaultWeightedEdge edge : edges) {
			if (edgeSelection.add(edge)) {
				edgeMap.put(edge, true);
				if (DEBUG)
					System.out.println("[SelectionModel] Adding edge " + edge + " to selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public void removeEdgeFromSelection(final Collection<DefaultWeightedEdge> edges) {
		Map<DefaultWeightedEdge, Boolean> edgeMap = new HashMap<DefaultWeightedEdge, Boolean>(edges.size());
		for (DefaultWeightedEdge edge : edges) {
			if (edgeSelection.remove(edge)) {
				edgeMap.put(edge, false);
				if (DEBUG)
					System.out.println("[SelectionModel] Removing edge " + edge + " from selection");
			}
		}
		SelectionChangeEvent event = new SelectionChangeEvent(this, null, edgeMap);
		for (SelectionChangeListener listener : selectionChangeListeners)
			listener.selectionChanged(event);
	}

	public Set<TrackableObject> getSpotSelection() {
		return spotSelection;
	}

	public Set<DefaultWeightedEdge> getEdgeSelection() {
		return edgeSelection;
	}

	/*
	 * SPECIAL METHODS
	 */


	/**
	 * Search and add all spots and links belonging to the same track(s) that of given <code>spots</code> and 
	 * <code>edges</code> to current selection. A <code>direction</code> parameter allow specifying
	 * whether we should include only parts upwards in time, downwards in time or all the way through. 
	 * @param spots  the spots to include in search
	 * @param edges  the edges to include in search
	 * @param direction  the direction to go when searching. Positive integers will result in searching
	 * upwards in time, negative integers downwards in time and 0 all the way through.
	 */
	public void selectTrack(final Collection<TrackableObject> spots, final Collection<DefaultWeightedEdge> edges, final int direction) {

		HashSet<TrackableObject> inspectionSpots = new HashSet<TrackableObject>(spots);

		for(DefaultWeightedEdge edge : edges) {
			// We add connected spots to the list of spots to inspect
			inspectionSpots.add(model.getTrackModel().getEdgeSource(edge));
			inspectionSpots.add(model.getTrackModel().getEdgeTarget(edge));
		}

		// Walk across tracks to build selection
		final HashSet<TrackableObject> spotSelection 					= new HashSet<TrackableObject>();
		final HashSet<DefaultWeightedEdge> edgeSelection 	= new HashSet<DefaultWeightedEdge>();

		if (direction == 0) { // Unconditionally
			for (TrackableObject spot : inspectionSpots) {
				spotSelection.add(spot);
				GraphIterator<TrackableObject, DefaultWeightedEdge> walker = model.getTrackModel().getDepthFirstIterator(spot, false);
				while (walker.hasNext()) { 
					TrackableObject target = walker.next();
					spotSelection.add(target); 
					// Deal with edges
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(target);
					for(DefaultWeightedEdge targetEdge : targetEdges) {
						edgeSelection.add(targetEdge);
					}
				}
			}

		} else { // Only upward or backward in time 
			for (TrackableObject spot : inspectionSpots) {
				spotSelection.add(spot);

				// A bit more complicated: we want to walk in only one direction,
				// when branching is occurring, we do not want to get back in time.
				Stack<TrackableObject> stack = new Stack<TrackableObject>();
				stack.add(spot);
				while (!stack.isEmpty()) { 
					TrackableObject inspected = stack.pop();
					Set<DefaultWeightedEdge> targetEdges = model.getTrackModel().edgesOf(inspected);
					for(DefaultWeightedEdge targetEdge : targetEdges) {
						TrackableObject other;
						if (direction > 0) {
							// Upward in time: we just have to search through edges using their source spots
							other = model.getTrackModel().getEdgeSource(targetEdge);
						} else {
							other = model.getTrackModel().getEdgeTarget(targetEdge);
						}

						if (other != inspected) {
							spotSelection.add(other);
							edgeSelection.add(targetEdge);
							stack.add(other);
						}
					}
				}
			}
		}

		// Cut "tail": remove the first an last edges in time, so that the selection only has conencted 
		// edges in it.
		ArrayList<DefaultWeightedEdge> edgesToRemove = new ArrayList<DefaultWeightedEdge>();
		for(DefaultWeightedEdge edge : edgeSelection) {
			TrackableObject source = model.getTrackModel().getEdgeSource(edge);
			TrackableObject target = model.getTrackModel().getEdgeTarget(edge);
			if ( !(spotSelection.contains(source) && spotSelection.contains(target)) ) {
				edgesToRemove.add(edge);
			}
		}
		edgeSelection.removeAll(edgesToRemove);

		// Set selection
		addSpotToSelection(spotSelection);
		addEdgeToSelection(edgeSelection);
	}

}
