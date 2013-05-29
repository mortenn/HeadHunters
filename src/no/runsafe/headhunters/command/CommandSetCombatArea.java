package no.runsafe.headhunters.command;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.output.ChatColour;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.Util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 17:14
 */
public class CommandSetCombatArea extends PlayerCommand {

    private Core core;
    private IConfiguration config;
    private WorldGuardPlugin worldGuard;

    public CommandSetCombatArea(Core core, IConfiguration config){
        super("combatarea", "Adds or removes the WorldGuard region you are in as a combat area.", "headhunters.set-room", "p");
        this.core = core;
        this.config = config;
        this.captureTail();
        this.worldGuard = RunsafeServer.Instance.getPlugin("WorldGuard");


    }

    @Override
    public String getUsageCommandParams(){
        return this.getName() + "<" +  ChatColour.YELLOW + "add " + ChatColour.RESET + "|"+ ChatColour.YELLOW + " del"+ ChatColour.RESET + ">";
    }

    @Override
    public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters) {

        boolean add;

        String arg = parameters.get("p");
        if(arg.equalsIgnoreCase("add")) add = true;
        else if(arg.equalsIgnoreCase("del")) add = false;
        else return this.getUsage();

        if(core.getEnabled()) return Constants.ERROR_COLOR + "Only use this when headhunters is disabled";


        if(player.getWorld().getName().equalsIgnoreCase(core.getWorldName())){

            RegionManager manager = worldGuard.getGlobalRegionManager().get(RunsafeServer.Instance.getWorld(core.getWorldName()).getRaw());
            ApplicableRegionSet set = manager.getApplicableRegions(player.getLocation().getRaw());

            if(set.size() == 0)
                return Constants.ERROR_COLOR + "No region found";
            if(set.size() > 1)
                return Constants.ERROR_COLOR + "Found multiple regions";

            ArrayList<String> areas = core.getRegions();
            String thisRegion = null;
            for(ProtectedRegion r : set)
                thisRegion = r.getId();

            if(add){

                if(areas.contains(thisRegion))
                    return Constants.ERROR_COLOR + "Region already exists";

                areas.add(thisRegion);
                core.setRegions(areas);

                config.setConfigValue("regions", areas);
                config.save();

                core.loadAreas();

                return Constants.MSG_COLOR + "Added region &f" + thisRegion;
            }else{
                if(!areas.contains(thisRegion)) return Constants.ERROR_COLOR + "Region does not exist as a combat area.";

                areas.remove(thisRegion);
                core.setRegions(areas);

                config.setConfigValue("regions", areas);
                config.save();

                core.loadAreas();

                return Constants.MSG_COLOR + "Removed area &f" + thisRegion;
            }

        }else{
            return Constants.ERROR_COLOR + "Please move to the correct world";
        }

    }


}