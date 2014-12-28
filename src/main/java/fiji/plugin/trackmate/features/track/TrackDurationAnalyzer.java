package fiji.plugin.trackmate.features.track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.multithreading.SimpleMultiThreading;
import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.FeatureModel;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.TrackmateConstants;
import fiji.plugin.trackmate.tracking.TrackableObject;
import fiji.plugin.trackmate.util.TrackableObjectUtils;

public class TrackDurationAnalyzer< T extends TrackableObject< T >> implements TrackAnalyzer< T >, MultiThreaded
{

	public static final String KEY = "Track duration";

	public static final String TRACK_DURATION = "TRACK_DURATION";

	public static final String TRACK_START = "TRACK_START";

	public static final String TRACK_STOP = "TRACK_STOP";

	public static final String TRACK_DISPLACEMENT = "TRACK_DISPLACEMENT";

	public static final List< String > FEATURES = new ArrayList< String >( 4 );

	public static final Map< String, String > FEATURE_NAMES = new HashMap< String, String >(
			4 );

	public static final Map< String, String > FEATURE_SHORT_NAMES = new HashMap< String, String >(
			4 );

	public static final Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< String, Dimension >(
			4 );

	public static final Map< String, Boolean > IS_INT = new HashMap< String, Boolean >(
			4 );

	static
	{
		FEATURES.add( TRACK_DURATION );
		FEATURES.add( TRACK_START );
		FEATURES.add( TRACK_STOP );
		FEATURES.add( TRACK_DISPLACEMENT );

		FEATURE_NAMES.put( TRACK_DURATION, "Duration of track" );
		FEATURE_NAMES.put( TRACK_START, "Track start" );
		FEATURE_NAMES.put( TRACK_STOP, "Track stop" );
		FEATURE_NAMES.put( TRACK_DISPLACEMENT, "Track displacement" );

		FEATURE_SHORT_NAMES.put( TRACK_DURATION, "Duration" );
		FEATURE_SHORT_NAMES.put( TRACK_START, "T start" );
		FEATURE_SHORT_NAMES.put( TRACK_STOP, "T stop" );
		FEATURE_SHORT_NAMES.put( TRACK_DISPLACEMENT, "Displacement" );

		FEATURE_DIMENSIONS.put( TRACK_DURATION, Dimension.TIME );
		FEATURE_DIMENSIONS.put( TRACK_START, Dimension.TIME );
		FEATURE_DIMENSIONS.put( TRACK_STOP, Dimension.TIME );
		FEATURE_DIMENSIONS.put( TRACK_DISPLACEMENT, Dimension.LENGTH );

		IS_INT.put( TRACK_DURATION, Boolean.FALSE );
		IS_INT.put( TRACK_START, Boolean.FALSE );
		IS_INT.put( TRACK_STOP, Boolean.FALSE );
		IS_INT.put( TRACK_DISPLACEMENT, Boolean.FALSE );
	}

	private int numThreads;

	private long processingTime;

	public TrackDurationAnalyzer()
	{
		setNumThreads();
	}

	@Override
	public boolean isLocal()
	{
		return true;
	}

	@Override
	public void process( final Collection< Integer > trackIDs, final Model< T > model )
	{

		if ( trackIDs.isEmpty() ) { return; }

		final ArrayBlockingQueue< Integer > queue = new ArrayBlockingQueue< Integer >(
				trackIDs.size(), false, trackIDs );
		final FeatureModel< T > fm = model.getFeatureModel();

		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		for ( int i = 0; i < threads.length; i++ )
		{
			threads[ i ] = new Thread( "TrackDurationAnalyzer thread " + i )
			{
				@Override
				public void run()
				{
					Integer trackID;
					while ( ( trackID = queue.poll() ) != null )
					{

						// I love brute force.
						final Set< T > track = model.getTrackModel()
								.trackSpots( trackID );
						double minT = Double.POSITIVE_INFINITY;
						double maxT = Double.NEGATIVE_INFINITY;
						Double t;
						T startSpot = null;
						T endSpot = null;
						for ( final T spot : track )
						{
							t = spot.getFeature( TrackmateConstants.POSITION_T );
							if ( t < minT )
							{
								minT = t;
								startSpot = spot;
							}
							if ( t > maxT )
							{
								maxT = t;
								endSpot = spot;
							}
						}

						fm.putTrackFeature( trackID, TRACK_DURATION,
								( maxT - minT ) );
						fm.putTrackFeature( trackID, TRACK_START, minT );
						fm.putTrackFeature( trackID, TRACK_STOP, maxT );
						fm.putTrackFeature( trackID, TRACK_DISPLACEMENT, Math
								.sqrt( TrackableObjectUtils.squareDistanceTo(
										startSpot, endSpot ) ) );

					}
				}
			};
		}

		final long start = System.currentTimeMillis();
		SimpleMultiThreading.startAndJoin( threads );
		final long end = System.currentTimeMillis();
		processingTime = end - start;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;

	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	@Override
	public List< String > getFeatures()
	{
		return FEATURES;
	}

	@Override
	public Map< String, String > getFeatureShortNames()
	{
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map< String, String > getFeatureNames()
	{
		return FEATURE_NAMES;
	}

	@Override
	public Map< String, Dimension > getFeatureDimensions()
	{
		return FEATURE_DIMENSIONS;
	}

	@Override
	public Map< String, Boolean > getIsIntFeature()
	{
		return IS_INT;
	}

	@Override
	public boolean isManualFeature()
	{
		return false;
	}
}
