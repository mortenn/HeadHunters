package no.runsafe.headhunters.command;


import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.PlayerHandler;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 27-4-13
 * Time: 16:03
 */
public class CommandShowIngame extends PlayerCommand {


    Core core;
    private final PlayerHandler playerHandler;

    public CommandShowIngame(Core core, PlayerHandler playerHandler){
        super("show", "shows ingame players", "headhunters.play");
        this.core = core;

        this.playerHandler = playerHandler;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {


        StringBuilder out = new StringBuilder(Constants.MSG_COLOR);

        for(RunsafePlayer player: playerHandler.getIngamePlayers()){
            out.append(player.getPrettyName() + ", ");
        }

        return out.toString();
    }
}
