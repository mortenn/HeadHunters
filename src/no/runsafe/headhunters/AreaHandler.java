package no.runsafe.headhunters;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 28-6-13
 * Time: 13:50
 */
public class AreaHandler
{
	private final HashMap<Integer, SimpleArea> areas;
	private int currentArea;
	private int nextArea;
	private String world = "world";
	private final StringBuilder availableRegions;

	private ArrayList<String> __areas__;

	public AreaHandler()
	{
		areas = new HashMap<Integer, SimpleArea>();
		currentArea = 0;
		nextArea = 0;
		availableRegions = new StringBuilder();
	}

	public void loadAreas(List<String> areaList)
	{
		__areas__ = (ArrayList<String>) areaList;
		areas.clear();
		int index = 0;
		boolean first = true;
		for (String area : areaList)
		{
			SimpleArea simpleArea = new SimpleArea(RunsafeServer.Instance.getWorld(world), area);
			areas.put(index, simpleArea);
			if (!first) availableRegions.append(",");
			else first = false;
			availableRegions.append(simpleArea.getRegionName());
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
		{
			currentArea = 0;
		}
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
		this.world = world;
	}

	public String getWorld()
	{
		return world;
	}

	public int getAmountLoadedAreas()
	{
		return areas.size();
	}

	public String getCombatRegion(RunsafeLocation location)
	{
		for (int i = 0; i < areas.size(); i++)
			if (areas.get(i).pointInArea(location))
			{
				return areas.get(i).getRegionName();
			}

		return null;
	}

	public String getAvailableRegions()
	{
		return availableRegions.toString();
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

	public ArrayList<String> get__areas__()
	{
		return __areas__;
	}

	public void removeEntities(List<RunsafeEntity> entities)
	{
		if (areas.containsKey(currentArea))
			for (RunsafeEntity entity : entities) //lets not delete players...
				if (!(entity instanceof RunsafePlayer) && areas.get(currentArea).pointInArea(entity.getLocation()))
					entity.remove();

	}
}
