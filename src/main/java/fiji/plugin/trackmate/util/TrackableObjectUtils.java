package fiji.plugin.trackmate.util;

import java.util.Comparator;

import fiji.plugin.trackmate.tracking.TrackableObject;

public abstract class TrackableObjectUtils
{

	public static < T extends TrackableObject< T >> int frameDiff( final T t1,
			final T t2 )
	{
		return t1.frame() - t2.frame();
	}

	public static < T extends TrackableObject< T >> double squareDistanceTo(
			final T t1, final T t2 )
	{
		double sumSquared = 0d;

		for ( int d = 0; d < t1.numDimensions(); d++ )
		{
			final double t1pos = t1.getDoublePosition( d );
			final double t2pos = t2.getDoublePosition( d );
			sumSquared += ( t1pos - t2pos ) * ( t1pos - t2pos );
		}
		return ( sumSquared == 0 ) ? Double.MIN_NORMAL : sumSquared;
	}

	/**
	 * A comparator used to sort spots by name. The comparison uses numerical
	 * natural sorting, So that "Spot_4" comes before "Spot_122".
	 */
	public static < T extends TrackableObject< T >> Comparator< T > nameComparator()
	{
		return new Comparator< T >()
		{
			private final AlphanumComparator comparator = AlphanumComparator.instance;

			@Override
			public int compare( final T o1, final T o2 )
			{
				return comparator.compare( o1.getName(), o2.getName() );
			}
		};
	}

	public static Comparator< TrackableObject< ? >> frameComparator()
	{
		return new Comparator< TrackableObject< ? >>()
		{

			@Override
			public int compare( final TrackableObject< ? > o1, final TrackableObject< ? > o2 )
			{
				return o1.frame() - o2.frame();
			}
		};
	}
}
