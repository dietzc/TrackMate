package fiji.plugin.trackmate;

import fiji.plugin.trackmate.features.FeatureAnalyzer;
import fiji.plugin.trackmate.features.FeatureFilter;
import fiji.plugin.trackmate.features.edges.EdgeAnalyzer;
import fiji.plugin.trackmate.features.track.TrackAnalyzer;
import fiji.plugin.trackmate.tracking.TrackerKeys;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileInfo;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to store user settings for the {@link TrackMate}
 * trackmate. It is simply made of public fields
 */
public class Settings
{

	/**
	 * The ImagePlus to operate on. Will also be used by some
	 * {@link TrackMateModelView} as a GUI target.
	 */
	public ImagePlus imp;

	/**
	 * The polygon of interest. This will be used to crop the image and to
	 * discard found spots out of the polygon. If <code>null</code>, the whole
	 * image is considered.
	 */
	public Polygon polygon;

	// Crop cube
	/**
	 * The time-frame index, <b>0-based</b>, of the first time-point to process.
	 */
	public int tstart;

	/** The time-frame index, <b>0-based</b>, of the last time-point to process. */
	public int tend;

	/** The lowest pixel X position, <b>0-based</b>, of the volume to process. */
	public int xstart;

	/** The highest pixel X position, <b>0-based</b>, of the volume to process. */
	public int xend;

	/** The lowest pixel Y position, <b>0-based</b>, of the volume to process. */
	public int ystart;

	/** The lowest pixel Y position, <b>0-based</b>, of the volume to process. */
	public int yend;

	/** The lowest pixel Z position, <b>0-based</b>, of the volume to process. */
	public int zstart;

	/** The lowest pixel Z position, <b>0-based</b>, of the volume to process. */
	public int zend;

	/** Target channel for detection, <b>1-based</b>. */
	// public int detectionChannel = 1;
	// Image info
	public double dt = 1;

	public double dx = 1;

	public double dy = 1;

	public double dz = 1;

	public int width;

	public int height;

	public int nslices;

	public int nframes;

	public String imageFolder = "";

	public String imageFileName = "";

	/**
	 * Settings map for {@link SpotDetector}.
	 *
	 * @see DetectorKeys for parameters and defaults.
	 */
	public Map< String, Object > detectorSettings = new HashMap< String, Object >();

	/**
	 * Settings map for {@link SpotTracker}.
	 *
	 * @see TrackerKeys for parameters and defaults.
	 */
	public Map< String, Object > trackerSettings = new HashMap< String, Object >();

	// Filters

	/**
	 * The feature filter list that is used to generate {@link #filteredSpots}
	 * from {@link #spots}.
	 */
	protected List< FeatureFilter > spotFilters = new ArrayList< FeatureFilter >();

	/**
	 * The initial quality filter value that is used to clip spots of low
	 * quality from {@link Model#spots}.
	 */
	public Double initialSpotFilterValue = Double.valueOf( 0 );

	/** The track filter list that is used to prune track and spots. */
	protected List< FeatureFilter > trackFilters = new ArrayList< FeatureFilter >();

	protected String errorMessage;

	// Spot features

	// Edge features

	/**
	 * The {@link EdgeAnalyzer}s that will be used to compute edge features.
	 * They are ordered in a {@link List} in case some analyzers requires the
	 * results of another analyzer to proceed.
	 */
	protected List< ? extends FeatureAnalyzer > edgeAnalyzers = new ArrayList< EdgeAnalyzer >();

	// Track features

	/**
	 * The {@link TrackAnalyzer}s that will be used to compute track features.
	 * They are ordered in a {@link List} in case some analyzers requires the
	 * results of another analyzer to proceed.
	 */
	protected List< TrackAnalyzer > trackAnalyzers = new ArrayList< TrackAnalyzer >();

	/*
	 * METHODS
	 */

	public void setFrom( final ImagePlus imp )
	{
		// Source image
		this.imp = imp;

		if ( null == imp ) { return; // we leave field default values
		}

		// File info
		final FileInfo fileInfo = imp.getOriginalFileInfo();
		if ( null != fileInfo )
		{
			this.imageFileName = fileInfo.fileName;
			this.imageFolder = fileInfo.directory;
		}
		else
		{
			this.imageFileName = imp.getShortTitle();
			this.imageFolder = "";

		}
		// Image size
		this.width = imp.getWidth();
		this.height = imp.getHeight();
		this.nslices = imp.getNSlices();
		this.nframes = imp.getNFrames();
		this.dx = ( float ) imp.getCalibration().pixelWidth;
		this.dy = ( float ) imp.getCalibration().pixelHeight;
		this.dz = ( float ) imp.getCalibration().pixelDepth;
		this.dt = ( float ) imp.getCalibration().frameInterval;

		if ( dt == 0 )
		{
			dt = 1;
		}

		// Crop cube
		this.zstart = 0;
		this.zend = imp.getNSlices() - 1;
		this.tstart = 0;
		this.tend = imp.getNFrames() - 1;
		final Roi roi = imp.getRoi();
		if ( roi == null )
		{
			this.xstart = 0;
			this.xend = width - 1;
			this.ystart = 0;
			this.yend = height - 1;
			this.polygon = null;
		}
		else
		{
			final Rectangle boundingRect = roi.getBounds();
			this.xstart = boundingRect.x;
			this.xend = boundingRect.width + boundingRect.x;
			this.ystart = boundingRect.y;
			this.yend = boundingRect.height + boundingRect.y;
			this.polygon = roi.getPolygon();

		}
		// The rest is left to the user
	}

	/*
	 * METHODS
	 */

	/**
	 * Returns a string description of the target image.
	 */
	public String toStringImageInfo()
	{
		final StringBuilder str = new StringBuilder();

		str.append( "Image data:\n" );
		if ( null == imp )
		{
			str.append( "Source image not set.\n" );
		}
		else
		{
			str.append( "For the image named: " + imp.getTitle() + ".\n" );
		}
		if ( imageFileName == null || imageFileName == "" )
		{
			str.append( "Not matching any file.\n" );
		}
		else
		{
			str.append( "Matching file " + imageFileName + " " );
			if ( imageFolder == null || imageFolder == "" )
			{
				str.append( "in current folder.\n" );
			}
			else
			{
				str.append( "in folder: " + imageFolder + "\n" );
			}
		}

		str.append( "Geometry:\n" );
		str.append( String.format( "  X = %4d - %4d, dx = %g\n", xstart, xend, dx ) );
		str.append( String.format( "  Y = %4d - %4d, dy = %g\n", ystart, yend, dy ) );
		str.append( String.format( "  Z = %4d - %4d, dz = %g\n", zstart, zend, dz ) );
		str.append( String.format( "  T = %4d - %4d, dt = %g\n", tstart, tend, dt ) );

		return str.toString();
	}

	public String toStringFeatureAnalyzersInfo()
	{
		final StringBuilder str = new StringBuilder();

		if ( edgeAnalyzers.isEmpty() )
		{
			str.append( "No edge feature analyzers.\n" );
		}
		else
		{
			str.append( "Edge feature analyzers:\n" );
			prettyPrintFeatureAnalyzer( edgeAnalyzers, str );
		}

		if ( trackAnalyzers.isEmpty() )
		{
			str.append( "No track feature analyzers.\n" );
		}
		else
		{
			str.append( "Track feature analyzers:\n" );
			prettyPrintFeatureAnalyzer( trackAnalyzers, str );
		}

		return str.toString();
	}

	@Override
	public String toString()
	{
		final StringBuilder str = new StringBuilder();

		str.append( toStringImageInfo() );

		str.append( '\n' );
		str.append( "Spot detection:\n" );

		str.append( '\n' );
		str.append( toStringFeatureAnalyzersInfo() );

		str.append( '\n' );
		str.append( "Initial spot filter:\n" );
		if ( null == initialSpotFilterValue )
		{
			str.append( "No initial quality filter.\n" );
		}
		else
		{
			str.append( "Initial quality filter value: " + initialSpotFilterValue + ".\n" );
		}

		str.append( '\n' );
		str.append( "Spot feature filters:\n" );
		if ( spotFilters == null || spotFilters.size() == 0 )
		{
			str.append( "No spot feature filters.\n" );
		}
		else
		{
			str.append( "Set with " + spotFilters.size() + " spot feature filters:\n" );
			for ( final FeatureFilter featureFilter : spotFilters )
			{
				str.append( " - " + featureFilter + "\n" );
			}
		}

		str.append( '\n' );
		str.append( "Particle linking:\n" );

		str.append( '\n' );
		str.append( "Track feature filters:\n" );
		if ( trackFilters == null || trackFilters.size() == 0 )
		{
			str.append( "No track feature filters.\n" );
		}
		else
		{
			str.append( "Set with " + trackFilters.size() + " track feature filters:\n" );
			for ( final FeatureFilter featureFilter : trackFilters )
			{
				str.append( " - " + featureFilter + "\n" );
			}
		}

		return str.toString();
	}

	public boolean checkValidity()
	{
		if ( null == imp )
		{
			errorMessage = "The source image is null.\n";
			return false;
		}
		if ( null == detectorSettings )
		{
			errorMessage = "The detector settings is null.\n";
			return false;
		}
		if ( null == initialSpotFilterValue )
		{
			errorMessage = "Initial spot quality threshold is not set.\n";
			return false;
		}
		return true;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	/*
	 * FEATURE FILTERS
	 */

	/**
	 * Add a filter to the list of spot filters to deal with when executing
	 * {@link #execFiltering()}.
	 */
	public void addSpotFilter( final FeatureFilter filter )
	{
		spotFilters.add( filter );
	}

	public void removeSpotFilter( final FeatureFilter filter )
	{
		spotFilters.remove( filter );
	}

	/** Remove all spot filters stored in this model. */
	public void clearSpotFilters()
	{
		spotFilters.clear();
	}

	public List< FeatureFilter > getSpotFilters()
	{
		return spotFilters;
	}

	public void setSpotFilters( final List< FeatureFilter > spotFilters )
	{
		this.spotFilters = spotFilters;
	}

	/** Add a filter to the list of track filters. */
	public void addTrackFilter( final FeatureFilter filter )
	{
		trackFilters.add( filter );
	}

	public void removeTrackFilter( final FeatureFilter filter )
	{
		trackFilters.remove( filter );
	}

	/** Remove all track filters stored in this model. */
	public void clearTrackFilters()
	{
		trackFilters.clear();
	}

	public List< FeatureFilter > getTrackFilters()
	{
		return trackFilters;
	}

	public void setTrackFilters( final List< FeatureFilter > trackFilters )
	{
		this.trackFilters = trackFilters;
	}

	/*
	 * PRIVATE METHODS
	 */

	private final void prettyPrintFeatureAnalyzer( final List< ? extends FeatureAnalyzer > analyzers, final StringBuilder str )
	{
		for ( final FeatureAnalyzer analyzer : analyzers )
		{
			str.append( " - " + analyzer.getClass().getSimpleName() + " provides: " );
			for ( final String feature : analyzer.getFeatures() )
			{
				str.append( analyzer.getFeatureShortNames().get( feature ) + ", " );
			}
			str.deleteCharAt( str.length() - 1 );
			str.deleteCharAt( str.length() - 1 );
			// be precise
			if ( str.charAt( str.length() - 1 ) != '.' )
			{
				str.append( '.' );
			}
			// manual?
			if ( analyzer.isManualFeature() )
			{
				str.deleteCharAt( str.length() - 1 );
				str.append( "; is manual." );
			}
			str.append( '\n' );
		}
	}

}
