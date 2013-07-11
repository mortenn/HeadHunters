package no.runsafe.headhunters.command;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Util;
import no.runsafe.headhunters.exception.RegionNotFoundException;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandSetWaitroom extends PlayerCommand {

    public CommandSetWaitroom(AreaHandler areaHandler, WorldGuardInterface worldGuardInterface, IConfiguration config)
    {
        super("waitroom", "Define a region as the wait room", "headhunters.regions.modify.waitroom");
        this.areaHandler = areaHandler;
        this.worldGuardInterface = worldGuardInterface;
        this.config = config;
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
    {
        if(!executor.getWorld().getName().equalsIgnoreCase(areaHandler.getWorld()))
        {

        }
        else
        {
            ArrayList<String> areas = areaHandler.get__areas__();
            List<String> region = worldGuardInterface.getRegionsAtLocation(executor.getLocation());
            if(region.size() == 0) return "No region found";
            if(region.size() > 1)  return "Multiple regions found";

            String thisRegion = region.get(0);
            if(Util.arrayListContainsIgnoreCase(areas, thisRegion))
                return "Region is registered as a combat region";

            try
            {

                areaHandler.setWaitRoom(thisRegion);

                config.setConfigValue("waitingarea", thisRegion);

                config.save();

                return "&aSuccesfully set headhunters region as &f" + thisRegion;
            }
            catch (RegionNotFoundException e)
            {
                return e.getMessage();
            }

        }

        return null;
    }

    private final AreaHandler areaHandler;
    private final WorldGuardInterface worldGuardInterface;
    private final IConfiguration config;
}
