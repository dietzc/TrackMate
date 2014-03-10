package fiji.plugin.trackmate.visualization;

import java.awt.Color;
import java.util.Set;

import org.jfree.chart.renderer.InterpolatePaintScale;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.ModelChangeEvent;
import fiji.plugin.trackmate.ModelChangeListener;
import fiji.plugin.trackmate.interfaces.TrackableObject;

public class SpotColorGenerator implements FeatureColorGenerator< TrackableObject >, ModelChangeListener
{

	private final Model model;

	private String feature = null;

	private double min;

	private double max;

	private static final InterpolatePaintScale generator = InterpolatePaintScale.Jet;

	public SpotColorGenerator( final Model model )
	{
		this.model = model;
		model.addModelChangeListener( this );
	}

	@Override
	public Color color( final TrackableObject spot )
	{
		if ( null == feature )
		{
			return TrackMateModelView.DEFAULT_SPOT_COLOR;
		}
		else
		{
			final Double val = spot.getFeature( feature );
			return generator.getPaint( ( val - min ) / ( max - min ) );
		}
	}

	@Override
	public String getFeature()
	{
		return feature;
	}

	@Override
	public void terminate()
	{
		model.removeModelChangeListener( this );
	}

	@Override
	public void activate()
	{
		model.addModelChangeListener( this );
	}

	@Override
	public void modelChanged( final ModelChangeEvent event )
	{
		if ( null == feature ) { return; }
		if ( event.getEventID() == ModelChangeEvent.MODEL_MODIFIED )
		{
			final Set< TrackableObject > spots = event.getSpots();
			if ( spots.size() > 0 )
			{
				computeSpotColors();
			}
		}
		else if ( event.getEventID() == ModelChangeEvent.SPOTS_COMPUTED )
		{
			computeSpotColors();
		}
	}

	/**
	 * Sets the feature that will be used to color spots. <code>null</code> is
	 * accepted; it will color all the spot with the same default color.
	 *
	 * @param feature
	 *            the feature to color spots with.
	 */
	@Override
	public void setFeature( final String feature )
	{
		if ( null != feature )
		{
			if ( feature.equals( this.feature ) ) { return; }
			this.feature = feature;
			computeSpotColors();
		}
		else
		{
			this.feature = null;
		}
	}

	/*
	 * PRIVATE METHODS
	 */

	private void computeSpotColors()
	{
		if ( null == feature ) { return; }

		// Get min & max
		min = Float.POSITIVE_INFINITY;
		max = Float.NEGATIVE_INFINITY;
		Double val;
		for ( final int ikey : model.getSpots().keySet() )
		{
			for ( final TrackableObject spot : model.getSpots().iterable( ikey, false ) )
			{
				val = spot.getFeature( feature );
				if ( null == val )
					continue;
				if ( val > max )
					max = val.doubleValue();
				if ( val < min )
					min = val.doubleValue();
			}
		}
	}
}
