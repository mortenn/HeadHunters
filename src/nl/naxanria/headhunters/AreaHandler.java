package nl.naxanria.headhunters;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AreaHandler implements IConfigurationChanged
{
	public AreaHandler()
	{
		currentArea = 0;
		nextArea = 0;
	}

	public void loadAreas(List<String> areaList)
	{
		areas.clear();
		availableRegions.clear();
		availableRegions.addAll(areaList);
		int index = 0;
		for (String area : areaList)
		{
			SimpleArea simpleArea = new SimpleArea(world, area);
			areas.put(index, simpleArea);
			index++;
		}
	}

	public int getCurrentArea()
	{
		return currentArea;
	}

	public void setNextAsCurrentArea()
	{
		currentArea = nextArea;
		if (!areas.containsKey(currentArea))
			currentArea = 0;
	}

	public int getNextArea()
	{
		return nextArea;
	}

	public String getAreaName(int index)
	{
		return areas.get(index).getRegionName();
	}

	public int randomNextArea()
	{
		return (nextArea = Util.getRandom(0, areas.size(), nextArea));
	}

	public void setNextArea(int newNextArea)
	{
		if (newNextArea < 0) newNextArea = 0;
		nextArea = newNextArea;
	}

	public int getAreaByName(String name)
	{
		for (int i = 0; i < areas.size(); i++)
		{
			if (areas.get(i).getRegionName().equalsIgnoreCase(name))
				return i;
		}
		return -1;
	}

	public RunsafeLocation getSafeLocation()
	{
		return areas.get(currentArea).safeLocation();
	}

	public void setWorld(String world)
	{
		this.world = RunsafeServer.Instance.getWorld(world);
		this.configuration.setConfigValue("world", world);
		this.configuration.save();
	}

	public RunsafeWorld getWorld()
	{
		return world;
	}

	public int getAmountLoadedAreas()
	{
		return areas.size();
	}

	public String getAvailableRegions()
	{
		return Strings.join(availableRegions, ", ");
	}

	public void teleport(int region, RunsafePlayer player)
	{
		areas.get(region).teleportToArea(player);
	}

	public boolean isInCombatRegion(RunsafeLocation location)
	{
		for (int i = 0; i < areas.size(); i++)
		{
			if (areas.get(i).pointInArea(location)) return true;
		}
		return false;
	}

	public boolean isInCurrentCombatRegion(RunsafeLocation location)
	{
		return isInCombatRegion(location, currentArea);
	}

	public boolean isInCombatRegion(RunsafeLocation location, int area)
	{
		return areas.get(area).pointInArea(location);
	}

	public void removeEntities()
	{
		if (areas.containsKey(currentArea))
			for (RunsafeEntity entity : world.getEntities()) //lets not delete players...
				if (!(entity instanceof RunsafePlayer) && areas.get(currentArea).pointInArea(entity.getLocation()))
					entity.remove();
	}

	public void setWaitRoom(String region)
	{
		if (waitRoom.getRegionName().equalsIgnoreCase(region))
			return;
		waitRoom = new SimpleArea(world, region);
		configuration.setConfigValue("waitingarea", region);
		configuration.save();
	}

	public boolean isInGameWorld(RunsafePlayer player)
	{
		return world.equals(player.getWorld());
	}

	public boolean isInWaitRoom(RunsafePlayer player)
	{
		return waitRoom.pointInArea(player.getLocation());
	}

	public RunsafeLocation getWaitRoomSpawn()
	{
		return waitroomSpawn;
	}

	public ArrayList<RunsafePlayer> getWaitRoomPlayers()
	{
		return waitRoom.getPlayers();
	}

	public ArrayList<RunsafePlayer> getWaitRoomPlayers(GameMode mode)
	{
		return waitRoom.getPlayers(mode);
	}

	public void setWaitRoomSpawn(RunsafeLocation location)
	{
		waitroomSpawn = location;
		configuration.setConfigValue("waitingroomspawn.x", location.getBlockX());
		configuration.setConfigValue("waitingroomspawn.y", location.getBlockY());
		configuration.setConfigValue("waitingroomspawn.z", location.getBlockZ());
		configuration.save();
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.configuration = configuration;
		world = RunsafeServer.Instance.getWorld(configuration.getConfigValueAsString("world"));
		waitRoom = new SimpleArea(world, configuration.getConfigValueAsString("waitroom"));
		setWaitRoomSpawn(
			new RunsafeLocation(
				world,
				configuration.getConfigValueAsDouble("waitingroomspawn.x"),
				configuration.getConfigValueAsDouble("waitingroomspawn.y"),
				configuration.getConfigValueAsDouble("waitingroomspawn.z")
			)
		);
	}

	private final HashMap<Integer, SimpleArea> areas = new HashMap<Integer, SimpleArea>();
	private final List<String> availableRegions = new ArrayList<String>();
	private IConfiguration configuration;
	private int currentArea;
	private int nextArea;
	private RunsafeWorld world;
	private SimpleArea waitRoom;
	private RunsafeLocation waitroomSpawn;
}
