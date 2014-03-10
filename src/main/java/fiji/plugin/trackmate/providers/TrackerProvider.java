package fiji.plugin.trackmate.providers;

import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.interfaces.TrackerFactory;

public class TrackerProvider<T extends TrackableObject> extends AbstractProvider< TrackerFactory >
{


	public TrackerProvider()
	{
		super( TrackerFactory.class );
	}

	public static void main( final String[] args )
	{
		final TrackerProvider provider = new TrackerProvider();
		System.out.println( provider.echo() );
	}
}
