package nl.naxanria.headhunters.handler;

import nl.naxanria.headhunters.Constants;
import nl.naxanria.headhunters.Util;
import nl.naxanria.headhunters.handler.AreaHandler;
import nl.naxanria.headhunters.handler.EquipmentManager;
import no.runsafe.framework.minecraft.Buff;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerHandler
{
	public PlayerHandler(EquipmentManager manager, AreaHandler areaHandler, ScoreboardHandler scoreboardHandler)
	{
		playerData = new HashMap<String, HashMap<String, Object>>();
		this.equipmentManager = manager;
		this.areaHandler = areaHandler;
        this.scoreboardHandler = scoreboardHandler;
	}

	public boolean isWinner()
	{
		return winner;
	}

	public int getWinAmount()
	{
		return winAmount;
	}

	public String getWorldName()
	{
		return areaHandler.getWorld().getName();
	}

	public void remove(RunsafePlayer player)
	{
		if (isIngame(player))
		{

			playerData.get(player.getName()).put("remove", true);

            scoreboardHandler.removeScoreBoard(player);

			unEquip(player);
			//Todo: add dropping all usable items
			RunsafeMeta heads = Item.Decoration.Head.Human.getItem();
			heads.setAmount(Util.amountMaterial(player, heads));
			player.getWorld().dropItem(player.getEyeLocation(), heads);
			player.getInventory().clear();
			player.teleport(areaHandler.getWaitRoomSpawn());

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
		return
			areaHandler.isInGameWorld(player)
				&& playerData.containsKey(player.getName())
				&& !((Boolean) playerData.get(player.getName()).get("remove"));
	}


	public void addPlayer(RunsafePlayer player)
	{
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("remove", false);
		data.put("player", player);

		playerData.put(player.getName(), data);
        scoreboardHandler.addScoreboard(player);
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

    public void resetScoreboard()
    {
        for(String k : playerData.keySet())
            scoreboardHandler.removeScoreBoard((RunsafePlayer) playerData.get(k).get("player"));
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
                scoreboardHandler.updateScoreboard(player, amount);
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

		this.teleportAllPlayers(areaHandler.getWaitRoomSpawn());
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
        {
            RunsafePlayer player = (RunsafePlayer) playerData.get(k).get("player");
			if (!(Boolean) playerData.get(k).get("remove") &&
				location.distance(player.getLocation()) < range)
				players.add(player);
        }
		return players;
	}

	private final AreaHandler areaHandler;
	private final HashMap<String, HashMap<String, Object>> playerData;
	private final int leaderAmount = -1;
    private final ScoreboardHandler scoreboardHandler;
	private final EquipmentManager equipmentManager;
	Boolean winner = false;
	private RunsafePlayer leader;
	private int winAmount = 0;
}