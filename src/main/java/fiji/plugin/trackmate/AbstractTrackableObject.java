package fiji.plugin.trackmate;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.imglib2.AbstractEuclideanSpace;

import fiji.plugin.trackmate.interfaces.FeatureHolder;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.interfaces.TrackableObjectUtils;

public abstract class AbstractTrackableObject extends AbstractEuclideanSpace implements TrackableObject {
	
	/** A user-supplied name for this object. */
	protected String name;
	
	public Comparator< TrackableObject > nameComparator = TrackableObjectUtils.nameComparator();

	protected final ConcurrentMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	
	public AbstractTrackableObject(int n) {
		super(n);
	}
	
	/**
	 * @return the name for this Object.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Set the name of this Object.
	 */
	public void setName( final String name )
	{
		this.name = name;
	}
	
	/**
	 * Exposes the storage map of features for this object. Altering the returned
	 * map will alter the spot.
	 *
	 * @return a map of {@link String}s to {@link Double}s.
	 */
	public Map< String, Double > getFeatures()
	{
		return features;
	}
	
	/**
	 * Returns the value corresponding to the specified object feature.
	 *
	 * @param feature
	 *            The feature string to retrieve the stored value for.
	 * @return the feature value, as a {@link Double}. Will be <code>null</code>
	 *         if it has not been set.
	 */
	public final Double getFeature( final String feature )
	{
		return features.get( feature );
	}

	/**
	 * Stores the specified feature value for this object.
	 *
	 * @param feature
	 *            the name of the feature to store, as a {@link String}.
	 * @param value
	 *            the value to store, as a {@link Double}. Using
	 *            <code>null</code> will have unpredicted outcomes.
	 */
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	
	
	/**
	 * Returns the difference of the feature value for this object with the one of
	 * the specified spot. By construction, this operation is anti-symmetric (
	 * <code>A.diffTo(B) = - B.diffTo(A)</code>).
	 * <p>
	 * Will generate a {@link NullPointerException} if one of the objects does not
	 * store the named feature.
	 *
	 * @param s
	 *            the object to compare to.
	 * @param feature
	 *            the name of the feature to use for calculation.
	 */
	public double diffTo( final FeatureHolder other, final String feature )
	{
		final double f1 = features.get( feature ).doubleValue();
		final double f2 = other.getFeature( feature ).doubleValue();
		return f1 - f2;
	}

	/**
	 * Returns the absolute normalized difference of the feature value of this
	 * object with the one of the given spot.
	 * <p>
	 * If <code>a</code> and <code>b</code> are the feature values, then the
	 * absolute normalized difference is defined as
	 * <code>Math.abs( a - b) / ( (a+b)/2 )</code>.
	 * <p>
	 * By construction, this operation is symmetric (
	 * <code>A.normalizeDiffTo(B) =
	 * B.normalizeDiffTo(A)</code>).
	 * <p>
	 * Will generate a {@link NullPointerException} if one of the object does not
	 * store the named feature.
	 *
	 * @param s
	 *            the object to compare to.
	 * @param feature
	 *            the name of the feature to use for calculation.
	 */
	public double normalizeDiffTo( final FeatureHolder other, final String feature )
	{
		final double a = features.get( feature ).doubleValue();
		final double b = other.getFeature( feature ).doubleValue();
		if ( a == -b )
			return 0d;
		else
			return Math.abs( a - b ) / ( ( a + b ) / 2 );
	}

	/**
	 * Returns the square distance from this object to the specified spot.
	 * 
	 * @param s
	 *            the object to compute the square distance to.
	 * @return the square distance as a <code>double</code>.
	 */
	public double squareDistanceTo( final FeatureHolder other )
	{
		double sumSquared = 0d;
		double thisVal, otherVal;

		for ( final String f : POSITION_FEATURES )
		{
			thisVal = features.get( f ).doubleValue();
			otherVal = other.getFeature( f ).doubleValue();
			sumSquared += ( otherVal - thisVal ) * ( otherVal - thisVal );
		}
		return sumSquared;
	}
	
	@Override
	public void localize( final float[] position )
	{
		assert ( position.length >= n );
		for ( int d = 0; d < n; ++d )
			position[ d ] = getFloatPosition( d );
	}

	@Override
	public void localize( final double[] position )
	{
		assert ( position.length >= n );
		for ( int d = 0; d < n; ++d )
			position[ d ] = getDoublePosition( d );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return ( float ) getDoublePosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getFeature( POSITION_FEATURES[ d ] );
	}
	
	

}
