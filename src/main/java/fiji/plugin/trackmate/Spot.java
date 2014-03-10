package fiji.plugin.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Util;
import fiji.plugin.trackmate.interfaces.FeatureHolder;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.interfaces.TrackableObjectUtils;

/**
 * A {@link RealLocalizable} implementation, used in TrackMate to represent a
 * detection.
 * <p>
 * On top of being a {@link RealLocalizable}, it can store additional numerical
 * named features, with a {@link Map}-like syntax. Constructors enforce the
 * specification of the spot location in 3D space (if Z is unused, put 0), the
 * spot radius, and the spot quality. This somewhat cumbersome syntax is made to
 * avoid any bad surprise with missing features in a subsequent use. The spot
 * temporal features ({@link #FRAME} and {@link #POSITION_T}) are set upon
 * adding to a {@link SpotCollection}.
 * <p>
 * Each spot received at creation a unique ID (as an <code>int</code>), used
 * later for saving, retrieving and loading. Interfering with this value will
 * predictively cause undesired behavior.
 *
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> 2010, 2013
 *
 */
public class Spot extends AbstractTrackableObject
{

	/*
	 * FIELDS
	 */

	public static AtomicInteger IDcounter = new AtomicInteger( -1 );

	/** Store the individual features, and their values. */
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();

	

	/** This spot ID */
	private final int ID;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Creates a new spot.
	 *
	 * @param x
	 *            the spot X coordinates, in image units.
	 * @param y
	 *            the spot Y coordinates, in image units.
	 * @param z
	 *            the spot Z coordinates, in image units.
	 * @param radius
	 *            the spot radius, in image units.
	 * @param quality
	 *            the spot quality.
	 * @param name
	 *            the spot name.
	 */
	public Spot( final double x, final double y, final double z, final double radius, final double quality, final String name )
	{
		super( 3 );
		this.ID = IDcounter.incrementAndGet();
		putFeature( POSITION_X, Double.valueOf( x ) );
		putFeature( POSITION_Y, Double.valueOf( y ) );
		putFeature( POSITION_Z, Double.valueOf( z ) );
		putFeature( RADIUS, Double.valueOf( radius ) );
		putFeature( QUALITY, Double.valueOf( quality ) );
		if ( null == name )
		{
			this.name = "ID" + ID;
		}
		else
		{
			this.name = name;
		}
	}

	/**
	 * Creates a new spot, and gives it a default name.
	 *
	 * @param x
	 *            the spot X coordinates, in image units.
	 * @param y
	 *            the spot Y coordinates, in image units.
	 * @param z
	 *            the spot Z coordinates, in image units.
	 * @param radius
	 *            the spot radius, in image units.
	 * @param quality
	 *            the spot quality.
	 */
	public Spot( final double x, final double y, final double z, final double radius, final double quality )
	{
		this( x, y, z, radius, quality, null );
	}

	/**
	 * Creates a new spot, taking its 3D coordinates from a
	 * {@link RealLocalizable}. The {@link RealLocalizable} must have at least 3
	 * dimensions, and must return coordinates in image units.
	 *
	 * @param location
	 *            the {@link RealLocalizable} that contains the spot locatiob.
	 * @param radius
	 *            the spot radius, in image units.
	 * @param quality
	 *            the spot quality.
	 * @param name
	 *            the spot name.
	 */
	public Spot( final RealLocalizable location, final double radius, final double quality, final String name )
	{
		this( location.getDoublePosition( 0 ), location.getDoublePosition( 1 ), location.getDoublePosition( 2 ), radius, quality, name );
	}

	/**
	 * Creates a new spot, taking its 3D coordinates from a
	 * {@link RealLocalizable}. The {@link RealLocalizable} must have at least 3
	 * dimensions, and must return coordinates in image units. The spot will get
	 * a default name.
	 *
	 * @param location
	 *            the {@link RealLocalizable} that contains the spot locatiob.
	 * @param radius
	 *            the spot radius, in image units.
	 * @param quality
	 *            the spot quality.
	 */
	public Spot( final RealLocalizable location, final double radius, final double quality )
	{
		this( location, radius, quality, null );
	}

	/**
	 * Creates a new spot, taking its location, its radius, its quality value
	 * and its name from the specified spot.
	 *
	 * @param spot
	 *            the spot to read from.
	 */
	public Spot( final TrackableObject spot )
	{
		this( spot, spot.getFeature( RADIUS ), spot.getFeature( QUALITY ), spot.getName() );
	}

	/**
	 * Blank constructor meant to be used when loading a spot collection from a
	 * file. <b>Will</b> mess with the {@link #IDcounter} field, so this
	 * constructor <u>should not be used for normal spot creation</u>.
	 *
	 * @param ID
	 *            the spot ID to set
	 */
	public Spot( final int ID )
	{
		super( 3 );
		this.ID = ID;
		synchronized ( IDcounter )
		{
			if ( IDcounter.get() < ID )
			{
				IDcounter.set( ID );
			}
		}
	}

	/*
	 * PUBLIC METHODS
	 */

	@Override
	public int hashCode()
	{
		return ID;
	}

	@Override
	public boolean equals( final Object other )
	{
		if ( other == null )
			return false;
		if ( other == this )
			return true;
		if ( !( other instanceof Spot ) )
			return false;
		final Spot os = ( Spot ) other;
		return os.ID == this.ID;
	}



	public int ID()
	{
		return ID;
	}

	@Override
	public String toString()
	{
		String str;
		if ( null == name || name.equals( "" ) )
			str = "ID" + ID;
		else
			str = name;
		return str;
	}

	/**
	 * Return a string representation of this spot, with calculated features.
	 */
	public String echo()
	{
		final StringBuilder s = new StringBuilder();

		// Name
		if ( null == name )
			s.append( "Spot: <no name>\n" );
		else
			s.append( "Spot: " + name + "\n" );

		// Frame
		s.append( "Time: " + getFeature( POSITION_T ) + '\n' );

		// Coordinates
		final double[] coordinates = new double[ 3 ];
		localize( coordinates );
		s.append( "Position: " + Util.printCoordinates( coordinates ) + "\n" );

		// Feature list
		if ( null == features || features.size() < 1 )
			s.append( "No features calculated\n" );
		else
		{
			s.append( "Feature list:\n" );
			double val;
			for ( final String key : features.keySet() )
			{
				s.append( "\t" + key.toString() + ": " );
				val = features.get( key );
				if ( val >= 1e4 )
					s.append( String.format( "%.1g", val ) );
				else
					s.append( String.format( "%.1f", val ) );
				s.append( '\n' );
			}
		}
		return s.toString();
	}

	/*
	 * STATIC KEYS
	 */


	/**
	 * The 7 privileged spot features that must be set by a spot detector:
	 * {@link #QUALITY}, {@link #POSITION_X}, {@link #POSITION_Y},
	 * {@link #POSITION_Z}, {@link #POSITION_Z}, {@link #RADIUS}, {@link #FRAME}
	 * .
	 */
	public final static Collection< String > FEATURES = new ArrayList< String >( 7 );

	/** The 7 privileged spot feature names. */
	public final static Map< String, String > FEATURE_NAMES = new HashMap< String, String >( 7 );

	/** The 7 privileged spot feature short names. */
	public final static Map< String, String > FEATURE_SHORT_NAMES = new HashMap< String, String >( 7 );

	/** The 7 privileged spot feature dimensions. */
	public final static Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap< String, Dimension >( 7 );

	static
	{
		FEATURES.add( QUALITY );
		FEATURES.add( POSITION_X );
		FEATURES.add( POSITION_Y );
		FEATURES.add( POSITION_Z );
		FEATURES.add( POSITION_T );
		FEATURES.add( FRAME );
		FEATURES.add( RADIUS );

		FEATURE_NAMES.put( POSITION_X, "X" );
		FEATURE_NAMES.put( POSITION_Y, "Y" );
		FEATURE_NAMES.put( POSITION_Z, "Z" );
		FEATURE_NAMES.put( POSITION_T, "T" );
		FEATURE_NAMES.put( FRAME, "Frame" );
		FEATURE_NAMES.put( RADIUS, "Radius" );
		FEATURE_NAMES.put( QUALITY, "Quality" );

		FEATURE_SHORT_NAMES.put( POSITION_X, "X" );
		FEATURE_SHORT_NAMES.put( POSITION_Y, "Y" );
		FEATURE_SHORT_NAMES.put( POSITION_Z, "Z" );
		FEATURE_SHORT_NAMES.put( POSITION_T, "T" );
		FEATURE_SHORT_NAMES.put( FRAME, "Frame" );
		FEATURE_SHORT_NAMES.put( RADIUS, "R" );
		FEATURE_SHORT_NAMES.put( QUALITY, "Quality" );

		FEATURE_DIMENSIONS.put( POSITION_X, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( POSITION_Y, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( POSITION_Z, Dimension.POSITION );
		FEATURE_DIMENSIONS.put( POSITION_T, Dimension.TIME );
		FEATURE_DIMENSIONS.put( FRAME, Dimension.NONE );
		FEATURE_DIMENSIONS.put( RADIUS, Dimension.LENGTH );
		FEATURE_DIMENSIONS.put( QUALITY, Dimension.QUALITY );
	}

	/*
	 * STATIC UTILITY
	 */

	/**
	 * A comparator used to sort spots by name. The comparison uses numerical
	 * natural sorting, So that "Spot_4" comes before "Spot_122".
	 */
	

}
