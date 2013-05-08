package no.runsafe.headhunters;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.framework.command.Command;
import no.runsafe.headhunters.command.*;
import no.runsafe.headhunters.event.*;

public class Plugin extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{

        this.addComponent(EquipmentManager.class);
        this.addComponent(RandomItem.class);


		this.addComponent(Core.class);

        // commands


        Command command = new Command("headhunters", "Headhunters plugin commands", null);

        command.addSubCommand(this.getInstance(disable.class));
        command.addSubCommand(this.getInstance(enable.class));
        command.addSubCommand(this.getInstance(CombatArea.class));
        command.addSubCommand(this.getInstance(WaitingRoom.class));
        command.addSubCommand(this.getInstance(Start.class));
        command.addSubCommand(this.getInstance(Spawn.class));
        command.addSubCommand(this.getInstance(Join.class));
        command.addSubCommand(this.getInstance(showIngame.class));
        command.addSubCommand(this.getInstance(Leave.class));
        command.addSubCommand(this.getInstance(Stop.class));

        this.addComponent(command);

        //events
        this.addComponent(PlayerDeath.class);
        this.addComponent(PlayerRespawn.class);
        this.addComponent(PlayerDisconnect.class);
        this.addComponent(PlayerItemPickUp.class);
        this.addComponent(PlayerRightClick.class);
        this.addComponent(PlayerMove.class);



	}
}
