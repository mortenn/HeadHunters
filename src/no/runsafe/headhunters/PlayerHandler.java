package no.runsafe.headhunters;

import no.runsafe.framework.minecraft.Buff;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 10-6-13
 * Time: 23:32
 */
public class PlayerHandler
{
	final HashMap<String, HashMap<String, Object>> playerData;

	Boolean winner = false;
	RunsafePlayer leader;
	final int leaderAmount = -1;
	int winAmount = 0;
	private final AreaHandler areaHandler;

	final EquipmentManager equipmentManager;
	private RunsafeLocation defaultSpawn;

	public PlayerHandler(EquipmentManager manager, AreaHandler areaHandler)
	{

		playerData = new HashMap<String, HashMap<String, Object>>();
		this.equipmentManager = manager;
		this.areaHandler = areaHandler;
	}

	public void setDefaultSpawn(RunsafeLocation location)
	{
		this.defaultSpawn = location;
	}

	public int getWinAmount()
	{
		return winAmount;
	}


	public String getWorldName()
	{
		return areaHandler.getWorld();
	}

	public void remove(RunsafePlayer player)
	{
		if (isIngame(player))
		{
			playerData.get(player.getName()).put("remove", true);
			unEquip(player);
			//Todo: add dropping all usable items
			RunsafeMeta heads = Item.Decoration.Head.Human.getItem();
			heads.setAmount(Util.amountMaterial(player, heads));
			player.getWorld().dropItem(player.getEyeLocation(), heads);
			player.getInventory().clear();
			player.teleport(defaultSpawn);

			ArrayList<RunsafePlayer> ingame = getIngamePlayers();

			if (ingame.size() == 1)
			{
				winner = true;
				leader = ingame.get(0);
			}
		}
	}


	public boolean isIngame(RunsafePlayer player)
	{
		return playerData.containsKey(player.getName()) && !((Boolean) playerData.get(player.getName()).get("remove"));
	}


	public void addPlayer(RunsafePlayer player)
	{

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("remove", false);
		data.put("player", player);

		playerData.put(player.getName(), data);

	}

	public void addPlayers(ArrayList<RunsafePlayer> players)
	{
		for (RunsafePlayer player : players) addPlayer(player);
	}

	public void teleportAllPlayers(RunsafeLocation location)
	{

		for (String k : playerData.keySet())
		{
			if (!(Boolean) playerData.get(k).get("remove"))
				((RunsafePlayer) playerData.get(k).get("player")).teleport(location);
		}

	}

	public void teleport()
	{
		for (String k : playerData.keySet())
		{
			if (!(Boolean) playerData.get(k).get("remove"))
			{
				RunsafePlayer player = ((RunsafePlayer) playerData.get(k).get("player"));
				player.teleport(areaHandler.getSafeLocation());
			}
		}
	}

	public void start(ArrayList<RunsafePlayer> players)
	{
		addPlayers(players);
		unEquipAll();
		setUpPlayers();
		teleport();
		winAmount = (int) ((players.size() / 2) + players.size() * 3.5);
	}

	public void setUpPlayers()
	{
		for (String k : playerData.keySet()) setUpPlayer((RunsafePlayer) playerData.get(k).get("player"));
	}

	public void setUpPlayer(RunsafePlayer player)
	{
		player.setGameMode(GameMode.SURVIVAL);
		equipmentManager.equip(player);
		player.setSaturation(10f);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.removeBuffs();
		Buff.Resistance.Damage.amplification(2).duration(6).applyTo(player);
	}

	public void reset()
	{
		winAmount = 0;
		leader = null;
		playerData.clear();
		winner = false;
	}


	public ArrayList<String> tick()
	{
		ArrayList<String> out = new ArrayList<String>();
		int currLAmount = 0;
		RunsafePlayer currLeader = null;
		for (String k : playerData.keySet())
		{
			if (!(Boolean) playerData.get(k).get("remove"))
			{
				RunsafePlayer player = (RunsafePlayer) playerData.get(k).get("player");
				int amount = Util.amountMaterial(player, Item.Decoration.Head.Human.getItem());
				if (amount != 0 && amount > leaderAmount && amount > currLAmount)
				{
					currLeader = player;
					currLAmount = amount;
				}
				player.setSaturation(10f);
				if (!player.getWorld().getName().equalsIgnoreCase(getWorldName()))
				{
					remove(player);
					player.sendColouredMessage("&3You have been removed from the match.");
					out.add(String.format("&3Player %s &3has been removed from the match.", player.getPrettyName()));
				}
			}
		}
		if ((currLeader != null && leader != null && !currLeader.getName().equalsIgnoreCase(leader.getName()))
			|| (currLeader != null && leader == null))
		{
			leader = currLeader;
			out.add(String.format(Constants.MSG_NEW_LEADER, leader.getPrettyName(), currLAmount));
		}

		if (currLAmount >= winAmount)
		{
			winner = true;
		}
		return out;
	}


	public void unEquipAll()
	{
		for (String k : playerData.keySet()) this.unEquip((RunsafePlayer) playerData.get(k).get("player"));
	}

	public void unEquip(RunsafePlayer player)
	{
		player.getInventory().clear();
	}

	public void end()
	{

		this.teleportAllPlayers(defaultSpawn);
		this.unEquipAll();
		this.reset();

	}

	public RunsafePlayer getCurrentLeader()
	{
		return leader;
	}

	public ArrayList<RunsafePlayer> getIngamePlayers()
	{
		ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();
		for (String k : playerData.keySet())
			if (!(Boolean) playerData.get(k).get("remove"))
				players.add((RunsafePlayer) playerData.get(k).get("player"));
		return players;
	}

	public ArrayList<RunsafePlayer> getIngamePlayers(RunsafeLocation location, int range)
	{
		ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();
		for (String k : playerData.keySet())
			if (!(Boolean) playerData.get(k).get("remove") &&
				Util.distance(location, ((RunsafePlayer) playerData.get(k).get("player")).getLocation()) < range)
				players.add((RunsafePlayer) playerData.get(k).get("player"));
		return players;
	}
}