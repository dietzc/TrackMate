package fiji.plugin.trackmate.tracking;

import java.util.Map;

import fiji.plugin.trackmate.TrackableObjectCollection;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.tracking.hungarian.AssignmentAlgorithm;
import fiji.plugin.trackmate.tracking.hungarian.JonkerVolgenantAlgorithm;

public class FastLAPTracker<T extends TrackableObject> extends LAPTracker {

	public FastLAPTracker( final TrackableObjectCollection spots, final Map< String, Object > settings )
	{
		super( spots, settings );
	}

	@Override
	protected AssignmentAlgorithm createAssignmentProblemSolver() {
		return new JonkerVolgenantAlgorithm();
	}
}
