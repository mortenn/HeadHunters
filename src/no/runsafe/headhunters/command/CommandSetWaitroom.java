package no.runsafe.headhunters.command;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Util;
import no.runsafe.headhunters.database.AreaRepository;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandSetWaitroom extends PlayerCommand
{
	public CommandSetWaitroom(AreaHandler areaHandler, WorldGuardInterface worldGuardInterface, AreaRepository areaRepository)
	{
		super("waitroom", "Define a region as the wait room", "headhunters.regions.modify.waitroom");
		this.areaHandler = areaHandler;
		this.worldGuardInterface = worldGuardInterface;
		this.areaRepository = areaRepository;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		if (!areaHandler.isInGameWorld(executor))
			return Constants.ERROR_COLOR + "Move to the correct world";

		ArrayList<String> areas = areaRepository.getAreas();
		List<String> region = worldGuardInterface.getRegionsAtLocation(executor.getLocation());
		if (region.size() == 0) return "No region found";
		if (region.size() > 1) return "Multiple regions found";

		String thisRegion = region.get(0);
		if (Util.arrayListContainsIgnoreCase(areas, thisRegion))
			return "Region is registered as a combat region";

		areaHandler.setWaitRoom(thisRegion);
		return "&aSuccesfully set headhunters waitroom as &f" + thisRegion;
	}

	private final AreaHandler areaHandler;
	private final WorldGuardInterface worldGuardInterface;
	private final AreaRepository areaRepository;
}
