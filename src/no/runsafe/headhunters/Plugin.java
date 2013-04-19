package no.runsafe.headhunters;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.command.Command;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(Core.class);

        Command command = new Command("headhunters", "Control the headhunters plugin", "headhunters.command");
        this.addComponent(command);



	}
}
