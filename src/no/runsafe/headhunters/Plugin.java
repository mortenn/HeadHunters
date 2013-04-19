package no.runsafe.headhunters;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.command.Command;

public class Plugin extends RunsafePlugin
{
	@Override
	protected void PluginSetup()
	{
		//addComponent(SomeComponent.class); // Replace this with your own components, this is just an example.

        Command command = new Command("headhunters", "Control the headhunters plugin", "headhunters.command");



	}
}
