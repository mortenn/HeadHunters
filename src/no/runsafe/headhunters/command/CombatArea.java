package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 17:14
 */
public class CombatArea extends PlayerCommand {

    Core core;

    public CombatArea(Core core){
        super("combatarea", "Sets the combat area coords", "headhunters.set-room", "posnumber");
        this.core = core;
        this.captureTail();
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {
        boolean firstpos = (parameters.get("posnumber").equalsIgnoreCase("1"));

        boolean success;
        if(firstpos){
            success = this.core.setCombatAreaFirstPos(executor);
            if(success){
                return Constants.MSG_COLOR + "First position set succesfully at " + executor.getLocation().toString();
            }
        }
        else{
            success = this.core.setCombatAreaSecondPos(executor);
            if(success){
                return Constants.MSG_COLOR + "Second position set succesfully at " + executor.getLocation().toString();
            }
        }

        return Constants.ERROR_COLOR + "Please move to the correct world";
    }
}
