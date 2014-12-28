package fiji.plugin.trackmate.tracking.sparselap.costfunction;

import fiji.plugin.trackmate.tracking.TrackableObject;
import fiji.plugin.trackmate.util.TrackableObjectUtils;

/**
 * A cost function that returns cost equal to the square distance. Suited to
 * Brownian motion.
 *
 * @author Jean-Yves Tinevez - 2014
 *
 */
public class SquareDistCostFunction< T extends TrackableObject< T >> implements
		CostFunction< T, T >
{

	@Override
	public double linkingCost( final T source, final T target )
	{
		return TrackableObjectUtils.squareDistanceTo( source, target );
	}

}
