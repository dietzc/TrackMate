package fiji.plugin.trackmate.tracking;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;
import fiji.plugin.trackmate.FeatureHolder;

public interface TrackableObject< T extends TrackableObject< T >> extends RealLocalizable, EuclideanSpace, Comparable< T >, FeatureHolder
{

	int ID();

	String getName();

	void setName( String name );

	int frame();

	void setFrame( int frame );

	double radius();

	void setVisible( boolean visibility );

	boolean isVisible();
}
