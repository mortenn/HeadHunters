package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 22:42
 */
public class Spawn extends PlayerCommand {


    private IConfiguration config;
    private Core core;

    public Spawn(Core core, IConfiguration config){
        super("spawn", "Sets the spawn point for in the wait room", "headhunters.set-spawn");
        this.core = core;
        this.config = config;
    }

    @Override
    public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters) {

        if(player.getWorld().getName().equalsIgnoreCase(core.getWorldName())){
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
