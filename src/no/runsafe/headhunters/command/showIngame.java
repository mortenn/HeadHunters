package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 27-4-13
 * Time: 16:03
 */
public class ShowIngame extends PlayerCommand {


    Core core;

    public ShowIngame(Core core){
        super("show", "shows ingame players", "headhunters.show-ingame-players");
        this.core = core;

    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {


        StringBuilder out = new StringBuilder(Constants.MSG_COLOR);

        for(RunsafePlayer player: core.getIngamePlayers()){
            out.append(player.getPrettyName() + ", ");
        }

        return out.toString();
    }
}
