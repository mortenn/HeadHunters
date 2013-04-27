package no.runsafe.headhunters;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.command.Command;
import no.runsafe.headhunters.command.*;
import no.runsafe.headhunters.event.PlayerDeath;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		this.addComponent(Core.class);

        // commands
        Command command = new Command("headhunters", "Control the headhunters plugin", "headhunters.command");
        command.addSubCommand(getInstance(disable.class));
        command.addSubCommand(getInstance(enable.class));
        command.addSubCommand(getInstance(CombatArea.class));
        command.addSubCommand(getInstance(WaitingRoom.class));
        command.addSubCommand(getInstance(Start.class));
        command.addSubCommand(getInstance(Spawn.class));
        command.addSubCommand(getInstance(DebugTeleportAll.class));
        command.addSubCommand(getInstance(Join.class));
        command.addSubCommand(getInstance(showIngame.class));

        this.addComponent(command);

        //events
        this.addComponent(PlayerDeath.class);



	}
}
