package fiji.plugin.trackmate.tracking.oldlap;

import java.util.Map;

import fiji.plugin.trackmate.tracking.TrackableObject;
import fiji.plugin.trackmate.tracking.TrackableObjectCollection;
import fiji.plugin.trackmate.tracking.oldlap.costfunction.CostCalculator;
import fiji.plugin.trackmate.tracking.oldlap.hungarian.AssignmentAlgorithm;
import fiji.plugin.trackmate.tracking.oldlap.hungarian.JonkerVolgenantAlgorithm;

public class FastLAPTracker< T extends TrackableObject< T >> extends LAPTracker< T >
{

	public FastLAPTracker( final CostCalculator< T > calculator,
			final TrackableObjectCollection< T > spots,
			final Map< String, Object > settings )
	{
		super( calculator, spots, settings );
	}

	@Override
	protected AssignmentAlgorithm createAssignmentProblemSolver()
	{
		return new JonkerVolgenantAlgorithm();
	}
}
