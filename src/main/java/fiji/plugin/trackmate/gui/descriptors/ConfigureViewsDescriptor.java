package fiji.plugin.trackmate.gui.descriptors;

import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fiji.plugin.trackmate.gui.panels.ConfigureViewsPanel;
import fiji.plugin.trackmate.interfaces.TrackableObject;
import fiji.plugin.trackmate.visualization.FeatureColorGenerator;
import fiji.plugin.trackmate.visualization.PerEdgeFeatureColorGenerator;
import fiji.plugin.trackmate.visualization.PerTrackFeatureColorGenerator;

public class ConfigureViewsDescriptor implements WizardPanelDescriptor
{

	public static final String KEY = "ConfigureViews";

	private final ConfigureViewsPanel panel;

	private final TrackMateGUIController controller;

	public ConfigureViewsDescriptor( final TrackMate trackmate, final FeatureColorGenerator< TrackableObject > spotColorGenerator, final PerEdgeFeatureColorGenerator edgeColorGenerator, final PerTrackFeatureColorGenerator trackColorGenerator, final FeatureColorGenerator< TrackableObject > spotColorGeneratorPerTrackFeature, final TrackMateGUIController controller )
	{
		this.controller = controller;
		this.panel = new ConfigureViewsPanel( trackmate.getModel() );
		panel.setSpotColorGenerator( spotColorGenerator );
		panel.setEdgeColorGenerator( edgeColorGenerator );
		panel.setTrackColorGenerator( trackColorGenerator );
		panel.setSpotColorGeneratorPerTrackFeature( spotColorGeneratorPerTrackFeature );
	}

	@Override
	public ConfigureViewsPanel getComponent()
	{
		return panel;
	}

	@Override
	public void aboutToDisplayPanel()
	{
		panel.refreshGUI();
		controller.getGUI().setNextButtonEnabled( true );
	}

	@Override
	public void displayingPanel()
	{
		panel.refreshColorFeatures();
	}

	@Override
	public void aboutToHidePanel()
	{}

	@Override
	public void comingBackToPanel()
	{}

	@Override
	public String getKey()
	{
		return KEY;
	}
}
