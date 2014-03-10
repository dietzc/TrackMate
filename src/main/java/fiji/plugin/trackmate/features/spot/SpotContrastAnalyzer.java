package fiji.plugin.trackmate.features.spot;

import static fiji.plugin.trackmate.features.spot.SpotContrastAnalyzerFactory.KEY;

import java.util.Iterator;

import net.imglib2.meta.ImgPlus;
import net.imglib2.type.numeric.RealType;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.util.SpotNeighborhood;
import fiji.plugin.trackmate.util.SpotNeighborhoodCursor;

public class SpotContrastAnalyzer< T extends RealType< T >> extends IndependentSpotFeatureAnalyzer< T >
{

	protected static final double RAD_PERCENTAGE = .5f;

	public SpotContrastAnalyzer( final ImgPlus< T > img, final Iterator< TrackableObject > spots )
	{
		super( img, spots );
	}

	public final void process( final TrackableObject spot )
	{
		double contrast = getContrast( spot );
		spot.putFeature( KEY, Math.abs( contrast ) );
	}

	/**
	 * Compute the contrast for the given spot.
	 * 
	 * @param spot
	 * @param diameter
	 *            the diameter to search for is in physical units
	 * @return
	 */
	private final double getContrast( final TrackableObject spot )
	{

		final SpotNeighborhood< T > neighborhood = new SpotNeighborhood< T >( spot, img );

		final double radius = spot.getFeature( Spot.RADIUS );
		long innerRingVolume = 0;
		long outerRingVolume = 0;
		double radius2 = radius * radius;
		double innerRadius2 = radius2 * ( 1 - RAD_PERCENTAGE ) * ( 1 - RAD_PERCENTAGE );
		double innerTotalIntensity = 0;
		double outerTotalIntensity = 0;
		double dist2;

		SpotNeighborhoodCursor< T > cursor = neighborhood.cursor();
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			dist2 = cursor.getDistanceSquared();
			if ( dist2 > radius2 )
			{
				outerRingVolume++;
				outerTotalIntensity += cursor.get().getRealDouble();
			}
			else if ( dist2 > innerRadius2 )
			{
				innerRingVolume++;
				innerTotalIntensity += cursor.get().getRealDouble();
			}
		}

		double innerMeanIntensity = innerTotalIntensity / innerRingVolume;
		double outerMeanIntensity = outerTotalIntensity / outerRingVolume;
		return innerMeanIntensity - outerMeanIntensity;
	}
}
