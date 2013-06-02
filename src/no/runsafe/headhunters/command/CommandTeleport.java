package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.SimpleArea;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 31-5-13
 * Time: 13:55
 */
public class CommandTeleport extends PlayerCommand {

    private Core core;

    public CommandTeleport(Core core){
        super("teleport", "teleports you to a given area", "headhunters.teleport", "region");
        this.core = core;
    }

    @Override
    public String getUsageCommandParams(){
        StringBuilder available = new StringBuilder();
        ArrayList<String> regions =  core.getRegions();
        boolean first = true;
        for(String region : regions){
            if(!first) available.append(",");
            available.append(region);
            first = false;
        }

        return "<map> &aAvailable maps &f" + available.toString() + "\n";
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {

        if(core.isIngame(executor)){
            return Constants.ERROR_COLOR + "You can not use this command while in game!";
        }

        ArrayList<String> regions = core.getRegions();
        String region = parameters.get("region");
        if(regions.contains(region)){
            for(SimpleArea area :core.getAreas()){
                if(area.getRegionName().equalsIgnoreCase(region)){
                    area.teleportToArea(executor);
                    executor.setGameMode(GameMode.CREATIVE);
                    return null;
                }
            }
        }else{
            return Constants.ERROR_COLOR + "Region " + region + " not found!";
        }


        return null;
    }
}
