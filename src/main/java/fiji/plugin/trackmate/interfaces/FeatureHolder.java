package fiji.plugin.trackmate.interfaces;

import java.util.Map;

public interface FeatureHolder {

	public Map< String, Double > getFeatures();

	public Double getFeature( final String feature );

	public void putFeature( final String feature, final Double value );

	public double diffTo( final FeatureHolder other, final String feature );

	public double normalizeDiffTo( final FeatureHolder other, final String feature );

	public double squareDistanceTo( final FeatureHolder other );

	/** The name of the frame feature. */
	public static final String FRAME = "FRAME";
	
	/** The name of the frame feature. */
	public static final String RADIUS = "RADIUS";
	
	/** The name of the frame feature. */
	public static final String QUALITY = "QUALITY";
	
	/** The name of the time feature. */
	public static final String POSITION_T = "POSITION_T";
	
	public static final String POSITION_X = "POSITION_X";
	
	public static final String POSITION_Y = "POSITION_Y";
	
	public static final String POSITION_Z = "POSITION_Z";
	
	/** The position features. */
	public final static String[] POSITION_FEATURES = new String[] { POSITION_X, POSITION_Y, POSITION_Z };
}
