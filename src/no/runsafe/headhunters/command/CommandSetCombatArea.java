package no.runsafe.headhunters.command;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.player.PlayerCommand;

import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.framework.text.ChatColour;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.Util;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 17:14
 */
public class CommandSetCombatArea extends PlayerCommand {

    private Core core;
    private IConfiguration config;
    private AreaHandler areaHandler;
    private WorldGuardInterface worldGuardInterface;

    public CommandSetCombatArea(Core core, IConfiguration config, AreaHandler areaHandler, WorldGuardInterface worldGuardInterface){
        super("combatarea", "Adds or removes the WorldGuard region you are in as a combat area.", "headhunters.regions.modify.areas", "p");
        this.core = core;
        this.config = config;
        this.captureTail();
        this.areaHandler = areaHandler;
        this.worldGuardInterface = worldGuardInterface;
    }

    @Override
    public String getUsageCommandParams(){
        return ChatColour.YELLOW + this.getName() + "&f<" +  ChatColour.YELLOW + "add " + ChatColour.RESET + "|"+ ChatColour.YELLOW + " del"+ ChatColour.RESET + ">";
    }

    @Override
    public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters) {

        String worldName;
        boolean add;

        String arg = parameters.get("p");
        if(arg.equalsIgnoreCase("add")) add = true;
        else if(arg.equalsIgnoreCase("del")) add = false;
        else return this.getUsage(player);

        if(core.getEnabled()) return Constants.ERROR_COLOR + "Only use this when headhunters is disabled!";

        worldName = areaHandler.getWorld();

        if(player.getWorld().getName().equalsIgnoreCase(worldName)){

            List<String> regions = worldGuardInterface.getRegionsAtLocation(player.getLocation());

            if(regions.size() == 0)
                return Constants.ERROR_COLOR + "No region found!";
            if(regions.size() > 1)
                return Constants.ERROR_COLOR + "Found multiple regions!";

            ArrayList<String> areas = areaHandler.get__areas__();
            String thisRegion = regions.get(0);

            if(add){

                if(Util.arrayListContainsIgnoreCase(areas, thisRegion))
                    return Constants.ERROR_COLOR + "Region already exists";

                areas.add(thisRegion);
                areaHandler.loadAreas(areas);

                config.setConfigValue("regions", areas);
                config.save();

                return Constants.MSG_COLOR + "Added region &f" + thisRegion;
            }else{
                if(!Util.arrayListContainsIgnoreCase(areas, thisRegion)) return Constants.ERROR_COLOR + "Region does not exist as a combat area.";

                Util.arraylistRemoveIgnoreCase(areas, thisRegion);
                areaHandler.loadAreas(areas);

                config.setConfigValue("regions", areas);
                config.save();

                return Constants.MSG_COLOR + "Removed area &f" + thisRegion;
            }

        }else{
            return Constants.ERROR_COLOR + "Please move to the correct world";
        }

    }


}
