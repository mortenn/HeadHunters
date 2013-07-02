package no.runsafe.headhunters.command;


import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.*;
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
    private final PlayerHandler playerHandler;
    private AreaHandler areaHandler;

    public CommandTeleport(Core core, PlayerHandler playerHandler, AreaHandler areaHandler){
        super("teleport", "teleports you to a given area", "headhunters.teleport", "region");
        this.core = core;
        this.playerHandler = playerHandler;
        this.areaHandler = areaHandler;
    }

    @Override
    public String getUsageCommandParams(){
       return "<map> &aAvailable maps: &f" + areaHandler.getAvailableRegions() + "\n";
    }

    @Override
    public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters) {

        if(playerHandler.isIngame(executor)){
            return Constants.ERROR_COLOR + "You can not use this command while in game!";
        }

        String region = parameters.get("region");
        int regionId = areaHandler.getAreaByName(region);
        if(regionId == -1) return getUsageCommandParams();
        areaHandler.teleport(regionId, executor);
        executor.setGameMode(GameMode.CREATIVE);
        return null;
    }
}
