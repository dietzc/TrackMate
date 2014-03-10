package fiji.plugin.trackmate.interfaces;

import net.imglib2.EuclideanSpace;
import net.imglib2.RealLocalizable;

public interface TrackableObject extends RealLocalizable, EuclideanSpace, FeatureHolder {
	
	public int ID();
	
	public String getName();

	public void setName(String name);

}
