package fiji.plugin.trackmate.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.graph.TimeDirectedNeighborIndex;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.interfaces.TrackableObjectUtils;

public class TrackNavigator {

	private final Model model;
	private final SelectionModel selectionModel;
	private final TimeDirectedNeighborIndex neighborIndex;

	public TrackNavigator(final Model model, final SelectionModel selectionModel) {
		this.model = model;
		this.selectionModel = selectionModel;
		this.neighborIndex = model.getTrackModel().getDirectedNeighborIndex();
	}

	public synchronized void nextTrack() {
		final TrackableObject spot = getASpot();
		if (null == spot) {
			return;
		}

		final Set<Integer> trackIDs = model.getTrackModel().trackIDs(true); // if only it was navigable...
		if (trackIDs.isEmpty()) {
			return;
		}

		Integer trackID = model.getTrackModel().trackIDOf(spot);
		if (null == trackID) {
			// No track? Then move to the first one.
			trackID = model.getTrackModel().trackIDs(true).iterator().next();
		}

		final Iterator<Integer> it = trackIDs.iterator();
		Integer nextTrackID = null;
		while (it.hasNext()) {
			final Integer id = it.next();
			if (id.equals(trackID)) {
				if (it.hasNext()) {
					nextTrackID = it.next();
					break;
				} else {
					nextTrackID = trackIDs.iterator().next(); // loop
				}
			}

		}

		final Set<TrackableObject> spots = model.getTrackModel().trackSpots(nextTrackID);
		final TreeSet<TrackableObject> ring = new TreeSet<TrackableObject>(TrackableObjectUtils.featureComparator(TrackableObject.FRAME));
		ring.addAll(spots);
		TrackableObject target = ring.ceiling(spot);
		if (null == target) {
			target = ring.floor(spot);
		}

		selectionModel.clearSelection();
		selectionModel.addSpotToSelection(target);
	}

	public synchronized void previousTrack() {
		final TrackableObject spot = getASpot();
		if (null == spot) {
			return;
		}

		Integer trackID = model.getTrackModel().trackIDOf(spot);
		final Set<Integer> trackIDs = model.getTrackModel().trackIDs(true); // if only it was navigable...
		if (trackIDs.isEmpty()) {
			return;
		}

		Integer lastID = null;
		for (final Integer id : trackIDs) {
			lastID = id;
		}

		if (null == trackID) {
			// No track? Then take the last one.
			trackID = lastID;
		}

		final Iterator<Integer> it = trackIDs.iterator();
		Integer previousTrackID = null;
		while (it.hasNext()) {
			final Integer id = it.next();
			if (id.equals(trackID)) {
				if (previousTrackID != null) {
					break;
				} else {
					previousTrackID = lastID;
					break;
				}
			}
			previousTrackID = id;

		}

		final Set<TrackableObject> spots = model.getTrackModel().trackSpots(previousTrackID);
		final TreeSet<TrackableObject> ring = new TreeSet<TrackableObject>(TrackableObjectUtils.featureComparator(TrackableObject.FRAME));
		ring.addAll(spots);
		TrackableObject target = ring.ceiling(spot);
		if (null == target) {
			target = ring.floor(spot);
		}

		selectionModel.clearSelection();
		selectionModel.addSpotToSelection(target);
	}

	public synchronized void nextSibling() {
		final TrackableObject spot = getASpot();
		if (null == spot) {
			return;
		}

		final Integer trackID = model.getTrackModel().trackIDOf(spot);
		if (null == trackID) {
			return;
		}

		final int frame = spot.getFeature(Spot.FRAME).intValue();
		final TreeSet<TrackableObject> ring = new TreeSet<TrackableObject>(TrackableObjectUtils.nameComparator());

		final Set<TrackableObject> spots = model.getTrackModel().trackSpots(trackID);
		for (final TrackableObject s : spots) {
			final int fs = s.getFeature(Spot.FRAME).intValue();
			if (frame == fs && s != spot) {
				ring.add(s);
			}
		}

		if (!ring.isEmpty()) {
			TrackableObject nextSibling = ring.ceiling(spot);
			if (null == nextSibling) {
				nextSibling = ring.first(); // loop
			}
			selectionModel.clearSelection();
			selectionModel.addSpotToSelection(nextSibling);
		}
	}

	public synchronized void previousSibling() {
		final TrackableObject spot = getASpot();
		if (null == spot) {
			return;
		}

		final Integer trackID = model.getTrackModel().trackIDOf(spot);
		if (null == trackID) {
			return;
		}

		final int frame = spot.getFeature(TrackableObject.FRAME).intValue();
		final TreeSet<TrackableObject> ring = new TreeSet<TrackableObject>(TrackableObjectUtils.nameComparator());

		final Set<TrackableObject> spots = model.getTrackModel().trackSpots(trackID);
		for (final TrackableObject s : spots) {
			final int fs = s.getFeature(TrackableObject.FRAME).intValue();
			if (frame == fs && s != spot) {
				ring.add(s);
			}
		}

		if (!ring.isEmpty()) {
			TrackableObject previousSibling = ring.floor(spot);
			if (null == previousSibling) {
				previousSibling = ring.last(); // loop
			}
			selectionModel.clearSelection();
			selectionModel.addSpotToSelection(previousSibling);
		}
	}

	public synchronized void previousInTime() {
		final TrackableObject spot = getASpot();
		if (null == spot) {
			return;
		}

		final Set<TrackableObject> predecessors = neighborIndex.predecessorsOf(spot);
		if (!predecessors.isEmpty()) {
			final TrackableObject next = predecessors.iterator().next();
			selectionModel.clearSelection();
			selectionModel.addSpotToSelection(next);
		}
	}

	public synchronized void nextInTime() {
		final TrackableObject spot = getASpot();
		if (null == spot) {
			return;
		}

		final Set<TrackableObject> successors = neighborIndex.successorsOf(spot);
		if (!successors.isEmpty()) {
			final TrackableObject next = successors.iterator().next();
			selectionModel.clearSelection();
			selectionModel.addSpotToSelection(next);
		}
	}

	/*
	 * STATIC METHODS
	 */

	/**
	 * Return a meaningful spot from the current selection, or <code>null</code>
	 * if the selection is empty.
	 * 
	 * @return
	 */
	private TrackableObject getASpot() {
		// Get it from spot selection
		final Set<TrackableObject> spotSelection = selectionModel.getSpotSelection();
		if (!spotSelection.isEmpty()) {
			final Iterator<TrackableObject> it = spotSelection.iterator();
			TrackableObject spot = it.next();
			int minFrame = spot.getFeature(TrackableObject.FRAME).intValue();
			while (it.hasNext()) {
				final TrackableObject s = it.next();
				final int frame = s.getFeature(TrackableObject.FRAME).intValue();
				if (frame < minFrame) {
					minFrame = frame;
					spot = s;
				}
			}
			return spot;
		}

		// Nope? Then get it from edges
		final Set<DefaultWeightedEdge> edgeSelection = selectionModel.getEdgeSelection();
		if (!edgeSelection.isEmpty()) {
			final Iterator<DefaultWeightedEdge> it = edgeSelection.iterator();
			final DefaultWeightedEdge edge = it.next();
			TrackableObject spot = model.getTrackModel().getEdgeSource(edge);
			int minFrame = spot.getFeature(Spot.FRAME).intValue();
			while (it.hasNext()) {
				final DefaultWeightedEdge e = it.next();
				final TrackableObject s = model.getTrackModel().getEdgeSource(e);
				final int frame = s.getFeature(Spot.FRAME).intValue();
				if (frame < minFrame) {
					minFrame = frame;
					spot = s;
				}
			}
			return spot;
		}

		// Still nothing? Then give up.
		return null;
	}
}
