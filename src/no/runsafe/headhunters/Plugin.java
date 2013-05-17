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
        this.addComponent(VoteHandler.class);

		this.addComponent(Core.class);

        // commands


        Command command = new Command("headhunters", "Headhunters plugin commands", null);

        command.addSubCommand(this.getInstance(CommandDisable.class));
        command.addSubCommand(this.getInstance(CommandEnable.class));
        command.addSubCommand(this.getInstance(CommandSetCombatArea.class));
        command.addSubCommand(this.getInstance(CommandSetWaitingRoom.class));
        command.addSubCommand(this.getInstance(CommandStart.class));
        command.addSubCommand(this.getInstance(CommandSetSpawn.class));
        command.addSubCommand(this.getInstance(CommandJoin.class));
        command.addSubCommand(this.getInstance(CommandShowIngame.class));
        command.addSubCommand(this.getInstance(CommandLeave.class));
        command.addSubCommand(this.getInstance(CommandStop.class));
        command.addSubCommand(this.getInstance(CommandInfo.class));
        command.addSubCommand(this.getInstance(CommandVote.class));

        this.addComponent(command);

        //events
        this.addComponent(PlayerDeath.class);
        this.addComponent(PlayerRespawn.class);
        this.addComponent(PlayerDisconnect.class);
        this.addComponent(PlayerItemPickUp.class);
        this.addComponent(PlayerRightClick.class);
        this.addComponent(PlayerMove.class);
        this.addComponent(BlockBreak.class);
        this.addComponent(BlockPlace.class);


	}
}
