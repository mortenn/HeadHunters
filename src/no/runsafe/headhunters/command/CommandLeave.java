package no.runsafe.headhunters.command;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 2-5-13
 * Time: 20:04
 */
public class CommandLeave extends PlayerCommand {

    private Core core;
    private final PlayerHandler playerHandler;

    public CommandLeave(Core core, PlayerHandler playerHandler){
        super("leave", "Leaves the current game", null);
        this.core = core;
        this.playerHandler = playerHandler;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {
        playerHandler.remove(executor);
        return null;
    }
}
