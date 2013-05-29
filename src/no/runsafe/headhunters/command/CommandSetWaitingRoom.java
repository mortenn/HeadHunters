package no.runsafe.headhunters.command;

import no.runsafe.framework.command.player.PlayerCommand;
import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.Util;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 21-4-13
 * Time: 17:14
 */
public class CommandSetWaitingRoom extends PlayerCommand {

    private Core core;
    private IConfiguration config;

    public CommandSetWaitingRoom(Core core, IConfiguration config){
        super("waitroom", "Sets the waitroom coords", "headhunters.set-room", "posnumber");
        this.config = config;
        this.core = core;
        this.captureTail();
    }

    @Override
    public String OnExecute(RunsafePlayer player, HashMap<String, String> parameters) {

        if(player.getWorld().getName().equalsIgnoreCase(core.getWorldName())){
            if(parameters.get("posnumber").equalsIgnoreCase("pos1")){

                RunsafeLocation location = player.getLocation();

                this.config.setConfigValue("waitingarea.x1",location.getBlockX());
                this.config.setConfigValue("waitingarea.y1",location.getBlockY());
                this.config.setConfigValue("waitingarea.z1",location.getBlockZ());

                core.setWaitRoomPos(true, location);

                this.config.save();

                return Constants.MSG_COLOR + "First position set at " + Util.prettyLocation(location);

            }else if(parameters.get("posnumber").equalsIgnoreCase("pos2")){
                RunsafeLocation location = player.getLocation();

                this.config.setConfigValue("waitingarea.x2",location.getBlockX());
                this.config.setConfigValue("waitingarea.y2",location.getBlockY());
                this.config.setConfigValue("waitingarea.z2",location.getBlockZ());

                core.setWaitRoomPos(false, location);

                this.config.save();

                return Constants.MSG_COLOR + "Second position set at " + Util.prettyLocation(location);
            }
        }else{

         return Constants.ERROR_COLOR + "Please move to the correct world";
        }

        return null;
    }

}