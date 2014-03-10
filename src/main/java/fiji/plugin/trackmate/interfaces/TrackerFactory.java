package fiji.plugin.trackmate.interfaces;

import java.util.Map;

import org.jdom2.Element;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.TrackMateModule;
import fiji.plugin.trackmate.TrackableObjectCollection;
import fiji.plugin.trackmate.gui.ConfigurationPanel;

public interface TrackerFactory extends TrackMateModule {

	public Tracker create( final TrackableObjectCollection spots, final Map< String, Object > settings );

	public ConfigurationPanel getTrackerConfigurationPanel( final Model model );

	public boolean marshall( final Map< String, Object > settings, final Element element );

	public boolean unmarshall( final Element element, final Map< String, Object > settings );

	public String toString( final Map< String, Object > sm );

	public Map< String, Object > getDefaultSettings();

	public boolean checkSettingsValidity( final Map< String, Object > settings );

	public String getErrorMessage();
}
