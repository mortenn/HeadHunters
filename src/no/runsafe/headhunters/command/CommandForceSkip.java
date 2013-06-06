package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 5-6-13
 * Time: 11:55
 */
public class CommandForceSkip extends PlayerCommand {


    private Core core;

    public CommandForceSkip(Core core){
        super("forceskip", "skips the current map for another one", "headhunters.skip", "map");
        this.core = core;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {

        if(core.getEnabled()){
            String nextMap = parameters.get("map");
            if(nextMap.equalsIgnoreCase("random"))
                if(core.getAreas().size() > 1) core.nextRandomRegion();
                else return Constants.ERROR_COLOR + "Define atleast 2 areas to be able to use random.";

            else if(core.getRegions().contains(nextMap))
                core.setNextRegion(nextMap);
            else return Constants.ERROR_COLOR + "Please specify a correct map, or use &frandom" + Constants.ERROR_COLOR + " for a random map. Available:&f" + core.getAvailableAreas();
            return core.getNextRegion();
        }
        return Constants.ERROR_COLOR + "Only use this when headhunters is enabled!";
    }
}
