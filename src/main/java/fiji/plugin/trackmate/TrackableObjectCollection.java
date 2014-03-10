package fiji.plugin.trackmate;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

import fiji.plugin.trackmate.features.FeatureFilter;
import fiji.plugin.trackmate.interfaces.TrackableObject;

public interface TrackableObjectCollection {
	
	public static Double ZERO = Double.valueOf( 0d );

	public static Double ONE = Double.valueOf( 1d );

	public static String VISIBILITY = "VISIBILITY";
	
	/*
	 * METHODS
	 */

	public TrackableObject search( final int ID );

	public String toString();

	public void add( final TrackableObject trackableObject, final Integer frame );

	public boolean remove( final TrackableObject trackableObject, final Integer frame );

	public void setVisible( final boolean visible );

	public void filter( final FeatureFilter featurefilter );

	public void filter( final Collection< FeatureFilter > filters );

	public TrackableObject getClosestObject( final TrackableObject trackableObject, final int frame, final boolean visibleObjectsOnly );

	public TrackableObject getObjectAt( final TrackableObject trackableObject, final int frame, final boolean visibleObjectsOnly );

	public  List< TrackableObject > getNClosestObjects( final TrackableObject trackableObject, final int frame, int n, final boolean visibleObjectsOnly );

	public int getNObjects( final boolean visibleObjectsOnly );

	public int getNObjects( final int frame, final boolean visibleObjectsOnly );

	public Map< String, double[] > collectValues( final Collection< String > features, final boolean visibleOnly );

	public double[] collectValues( final String feature, final boolean visibleOnly );

	public Iterator< TrackableObject > iterator( final boolean visibleObjectsOnly );

	public Iterator< TrackableObject > iterator( final Integer frame, final boolean visibleObjectsOnly );

	public Iterable< TrackableObject > iterable( final boolean visibleObjectsOnly );

	public Iterable< TrackableObject > iterable( final int frame, final boolean visibleObjectsOnly );

	/*
	 * SORTEDMAP
	 */

	public void put( final int frame, final Collection< TrackableObject > spots );
	
	public Integer firstKey();

	public Integer lastKey();

	public NavigableSet< Integer > keySet();

	public void clear();

	public TrackableObjectCollection crop();


}
