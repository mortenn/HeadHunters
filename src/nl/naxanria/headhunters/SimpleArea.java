package nl.naxanria.headhunters;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.block.RunsafeSign;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.GameMode;

import java.util.ArrayList;

public class SimpleArea
{
	public SimpleArea(RunsafeWorld world, String regionName)
	{
		this.world = world;
		this.regionName = regionName;

		if (worldGuardInterface != null && worldGuardInterface.serverHasWorldGuard())
		{
			region = worldGuardInterface.getRegion(world, regionName);

		}
	}


	public boolean pointInArea(RunsafeLocation location)
	{
		return pointInArea(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public boolean pointInArea(int x, int y, int z)
	{
		return region.contains(x, y, z);
	}

	public ArrayList<RunsafePlayer> getPlayers()
	{
		ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();

		for (RunsafePlayer player : this.world.getPlayers())
			if (pointInArea(player.getLocation())) players.add(player);

		return players;
	}

	public ArrayList<RunsafePlayer> getPlayers(GameMode mode)
	{
		ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();

		for (RunsafePlayer player : this.world.getPlayers())
			if (pointInArea(player.getLocation()) && player.getGameMode() == mode) players.add(player);

		return players;
	}

	public double getMinX()
	{
		return region.getMinimumPoint().getX();
	}

	public double getMaxX()
	{
		return region.getMaximumPoint().getX();
	}

	public double getMinY()
	{
		return region.getMinimumPoint().getY();
	}

	public double getMaxY()
	{
		return region.getMaximumPoint().getY();
	}

	public double getMinZ()
	{
		return region.getMinimumPoint().getZ();
	}

	public double getMaxZ()
	{
		return region.getMaximumPoint().getZ();
	}

	public RunsafeLocation getCenter()
	{
		return new RunsafeLocation(
			world,
			(getMaxX() - getMinX()) / 2,
			(getMaxY() - getMinY()) / 2,
			(getMaxZ() - getMinY()) / 2
		);
	}

	@Override
	public String toString()
	{
		return String.format(
			"[%.4f,%.4f,%.4f - %.4f,%.4f,%.4f @%s]",
			region.getMinimumPoint().getX(),
			region.getMinimumPoint().getY(),
			region.getMinimumPoint().getZ(),
			region.getMaximumPoint().getX(),
			region.getMaximumPoint().getY(),
			region.getMaximumPoint().getZ(),
			world.getName()
		);
	}

	public void teleportToArea(RunsafePlayer player)
	{
		player.teleport(new RunsafeLocation(world, this.getMinX(), this.getMaxY() + 15, this.getMinZ()));
	}

	public RunsafeLocation safeLocation()
	{
		int x, y, z;

		int minY = (int) this.getMinY();
		int maxY = (int) this.getMaxY();
		int tries = 2000;
		boolean foundGround;
		while (tries > 0)
		{
			foundGround = false;

			x = Util.getRandom((int) this.getMinX() + 3, (int) this.getMaxX() - 3);
			z = Util.getRandom((int) this.getMinZ() + 3, (int) this.getMaxZ() - 3);

			int air = 0;
			for (y = minY; y < maxY - 1; y++)
			{
				RunsafeBlock block = world.getBlockAt(x, y, z);
				if (block.getBlockState() instanceof RunsafeSign)
				{
					RunsafeSign sign = (RunsafeSign) block.getBlockState();
					if (sign.getLine(0).equalsIgnoreCase("skip")) continue;
				}
				if (!block.isAir())
				{
					air = 0;
					foundGround = true;
				}
				else
				{
					air++;
					if (foundGround && air > 1)
						return new RunsafeLocation(world, (double) x, (double) y - 1, (double) z);
				}
			}
			tries--;
		}
		return null;
	}

	public String getRegionName()
	{
		return this.regionName;
	}

	public static void setWorldGuardInterface(WorldGuardInterface worldGuardInterface)
	{
		SimpleArea.worldGuardInterface = worldGuardInterface;
	}

	private static WorldGuardInterface worldGuardInterface;
	private ProtectedRegion region;
	private final RunsafeWorld world;
	private final String regionName;

}