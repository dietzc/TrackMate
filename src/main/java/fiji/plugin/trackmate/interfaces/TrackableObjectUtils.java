package fiji.plugin.trackmate.interfaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import fiji.plugin.trackmate.Dimension;

public abstract class TrackableObjectUtils {
	
	public static Comparator< TrackableObject > featureComparator( final String feature )
	{
		final Comparator< TrackableObject > comparator = new Comparator< TrackableObject >()
		{
			@Override
			public int compare( final TrackableObject o1, final TrackableObject o2 )
			{
				final double diff = o2.diffTo( o1, feature );
				if ( diff == 0 )
					return 0;
				else if ( diff < 0 )
					return 1;
				else
					return -1;
			}
		};
		return comparator;
	}
	
	public static Comparator< TrackableObject > nameComparator( )
	{
		final Comparator< TrackableObject > comparator = new Comparator< TrackableObject >()
		{
			@Override
			public int compare( final TrackableObject o1, final TrackableObject o2 )
			{
				return o1.getName().compareTo(o2.getName());
			}
		};
		return comparator;
	}

	/** The 7 privileged spot feature names. */
	public static Map< String, String > FEATURE_NAMES = new HashMap<String, String>();

	/** The 7 privileged spot feature short names. */
	public static Map< String, String > FEATURE_SHORT_NAMES = new HashMap<String, String>();

	/** The 7 privileged spot feature dimensions. */
	public static Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap<String, Dimension>();
	
	public final static Collection< String > FEATURES = new ArrayList< String >( 7 );
	
	static
	{
		FEATURES.add( TrackableObject.QUALITY );
		FEATURES.add( TrackableObject.POSITION_X );
		FEATURES.add( TrackableObject.POSITION_Y );
		FEATURES.add( TrackableObject.POSITION_Z );
		FEATURES.add( TrackableObject.POSITION_T );
		FEATURES.add( TrackableObject.FRAME );
		FEATURES.add( TrackableObject.RADIUS );

		FEATURE_NAMES.put( TrackableObject.POSITION_X, "X" );
		FEATURE_NAMES.put( TrackableObject.POSITION_Y, "Y" );
		FEATURE_NAMES.put( TrackableObject.POSITION_Z, "Z" );
		FEATURE_NAMES.put( TrackableObject.POSITION_T, "T" );
		FEATURE_NAMES.put( TrackableObject.FRAME, "Frame" );
		FEATURE_NAMES.put( TrackableObject.RADIUS, "Radius" );
		FEATURE_NAMES.put( TrackableObject.QUALITY, "Quality" );

		FEATURE_SHORT_NAMES.put( TrackableObject.POSITION_X, "X" );
		FEATURE_SHORT_NAMES.put( TrackableObject.POSITION_Y, "Y" );
		FEATURE_SHORT_NAMES.put( TrackableObject.POSITION_Z, "Z" );
		FEATURE_SHORT_NAMES.put( TrackableObject.POSITION_T, "T" );
		FEATURE_SHORT_NAMES.put( TrackableObject.FRAME, "Frame" );
		FEATURE_SHORT_NAMES.put( TrackableObject.RADIUS, "R" );
		FEATURE_SHORT_NAMES.put( TrackableObject.QUALITY, "Quality" );

		FEATURE_DIMENSIONS.put( TrackableObject.POSITION_X, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( TrackableObject.POSITION_Y, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( TrackableObject.POSITION_Z, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( TrackableObject.POSITION_T, Dimension.TIME );
		FEATURE_DIMENSIONS.put( TrackableObject.FRAME, Dimension.NONE );
		FEATURE_DIMENSIONS.put( TrackableObject.RADIUS, Dimension.LENGTH );
		FEATURE_DIMENSIONS.put( TrackableObject.QUALITY, Dimension.QUALITY );
	}


}
