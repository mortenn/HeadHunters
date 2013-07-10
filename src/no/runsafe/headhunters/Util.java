package no.runsafe.headhunters;

import com.google.common.collect.Lists;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.Random;

public class Util
{
	public static int getRandom(int min, int max)
	{

		if (min > max)
		{
			int t = min;
			min = max;
			max = t;
		}

		if (min == max)
		{
			return min;
		}

		return random.nextInt(max - min) + min;
	}

	public static int getRandom(int min, int max, int not)
	{

		if (min > max)
		{
			int t = min;
			min = max;
			max = t;
		}

		if (min == max)
		{
			return min;
		}

		if (max - min == 0 || max - min == 1) return max - min;

		int r = random.nextInt(max - min) + min;
		if (r == not) r = getRandom(min, max, not);

		return r;
	}

	public static boolean actPercentage(int percentage)
	{
		return (getRandom(0, 100) < percentage);
	}

	public static double flatDistance(double x, double y)
	{
		return x < y ? y - x : x - y;
	}

	public static double distance(RunsafeLocation loc1, RunsafeLocation loc2)
	{

		return Math.sqrt(
			Math.pow(flatDistance(loc1.getX(), loc2.getX()), 2) +
				Math.pow(flatDistance(loc1.getY(), loc2.getY()), 2) +
				Math.pow(flatDistance(loc1.getZ(), loc2.getZ()), 2)
		);
	}

	public static String prettyLocation(RunsafeLocation location)
	{
		return String.format("&a%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public static int amountMaterial(RunsafePlayer player, RunsafeMeta search)
	{
		int amount = 0;
		for (RunsafeMeta content : player.getInventory().getContents())
			if (content.getItemId() == search.getItemId()) amount += content.getAmount();
		return amount;
	}

	public static String fillZeros(int i)
	{
		String s = i + "";
		if (i < 10) s = "0" + s;
		return s;
	}

	public static boolean arrayListContainsIgnoreCase(ArrayList<String> arrayList, String check)
	{
		for (String toCheck : arrayList)
		{
			if (toCheck.equalsIgnoreCase(check)) return true;
		}
		return false;
	}

	public static void arraylistRemoveIgnoreCase(ArrayList<String> arrayList, String removal)
	{
		ArrayList<String> copy = Lists.newArrayList(arrayList);
		for (String key : copy)
		{
			if (key.equalsIgnoreCase(removal))
			{
				arrayList.remove(key);
				return;
			}
		}

	}

	static final Random random = new Random(System.currentTimeMillis());
}
