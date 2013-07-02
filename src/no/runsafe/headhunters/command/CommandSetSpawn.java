package no.runsafe.headhunters.command;

import no.runsafe.framework.api.IConfiguration;

import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 22:42
 */
public class CommandSetSpawn extends PlayerCommand {


    private IConfiguration config;
    private Core core;
    private final AreaHandler areaHandler;

    public CommandSetSpawn(Core core, IConfiguration config, AreaHandler areaHandler){
        super("spawn", "Sets the spawn point for in the wait room", "headhunters.set-spawn");
        this.core = core;
        this.config = config;
        this.areaHandler = areaHandler;
    }

    @Override
    public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters) {

        if(player.getWorld().getName().equalsIgnoreCase(areaHandler.getWorld())){
            config.setConfigValue("waitingroomspawn.x", player.getLocation().getBlockX());
            config.setConfigValue("waitingroomspawn.y", player.getLocation().getBlockY());
            config.setConfigValue("waitingroomspawn.z", player.getLocation().getBlockZ());

            config.save();

            core.setWaitRoomSpawn(player.getLocation());

            return "Successfully set spawn";
        }


        return Constants.ERROR_COLOR + "Please move to the correct world";
    }
}
