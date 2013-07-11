package no.runsafe.headhunters;

import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import no.runsafe.framework.internal.configuration.ConfigurationEngine;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.framework.text.ChatColour;
import no.runsafe.framework.text.ConsoleColour;
import no.runsafe.headhunters.exception.RegionNotFoundException;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;

public class Core implements IConfigurationChanged, IPluginEnabled
{
	public Core(IConfiguration config, IOutput console, IScheduler scheduler, RunsafeServer server, VoteHandler voteHandler,
							PlayerHandler playerHandler, AreaHandler areaHandler, WorldGuardInterface worldGuardInterface)
	{

		this.config = config;
		this.console = console;
		this.server = server;

		this.playerHandler = playerHandler;
		this.voteHandler = voteHandler;
		this.areaHandler = areaHandler;

		this.gamestarted = false;

		SimpleArea.setWorldGuardInterface(worldGuardInterface);

		scheduler.startSyncRepeatingTask(
			new Runnable()
			{
				@Override
				public void run()
				{
					tick();
				}
			}, 1, 1
		);
	}

	public boolean enable()
	{
        this.enabled = true;
        loadAreasFromConfig();
		if (areaHandler.getAmountLoadedAreas() == 0)
		{
			return false;
		}
		else
		{
			this.config.setConfigValue("enabled", true);
			this.config.save();

			end();
			return true;
		}
	}

	public void disable()
	{
		if (this.gamestarted)
		{
			this.end();
		}

		gamestarted = false;
		this.config.setConfigValue("enabled", false);
		this.config.save();
		this.enabled = false;
	}

	public boolean getGamestarted()
	{
		return gamestarted;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public int getTimeToStart()
	{
		return countdownToStart;
	}

	public int getTimeToEnd()
	{
		return countdownToEnd;
	}

	public String startInTime(Integer time)
	{
		if (!this.enabled) return Constants.MSG_DISABLED;

		if (!gamestarted)
		{

			if (time <= 0) time = 1;

			countdownToStart = time;

			return Constants.MSG_COLOR + "Game will start in " + ChatColour.WHITE + time + Constants.MSG_COLOR + " seconds";
		}

		return "Game already started";
	}

	public void start()
	{
		if (areaHandler.getAmountLoadedAreas() == 0)
		{
			console.write("Can not start headhunters game; There are no areas");
			disable();
			return;
		}

		ArrayList<RunsafePlayer> players = areaHandler.getWaitRoomPlayers(GameMode.SURVIVAL);
		int minPlayers = 2;
		if (players.size() < minPlayers)
		{
			this.resetWaittime();
			sendMessage(String.format(Constants.MSG_NOT_ENOUGH_PLAYERS, players.size(), minPlayers));
			return;
		}

		this.gamestarted = true;
		areaHandler.setNextAsCurrentArea();
		playerHandler.start(players);
		sendMessage(String.format("Map: &f%s", areaHandler.getAreaName(areaHandler.getCurrentArea())));
		sendMessage(String.format(Constants.MSG_START_MESSAGE, playerHandler.getWinAmount()));
	}

	public void end()
	{
		playerHandler.end();

		List<RunsafeEntity> entities = server.getWorld(worldName).getEntities();
		areaHandler.removeEntities(entities);


		areaHandler.randomNextArea();
		gamestarted = false;

		resetWaittime();
		resetRuntime();

	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.config = configuration;
        this.enabled = config.getConfigValueAsBoolean("enabled");
        this.countdownToStart = config.getConfigValueAsInt("waittime");
        this.countdownToEnd = config.getConfigValueAsInt("runtime");
        this.voteHandler.setMinPercentage(config.getConfigValueAsInt("vote.min-percent"));
        this.voteHandler.setMinVotes(config.getConfigValueAsInt("vote.min-votes"));
        this.worldName = config.getConfigValueAsString("world");
        if (this.worldName == null)
        {
            worldName = "world";
        }
        areaHandler.setWorld(worldName);

        areaHandler.setWaitRoomSpawn(new RunsafeLocation(
                server.getWorld(this.worldName),
                config.getConfigValueAsDouble("waitingroomspawn.x"),
                config.getConfigValueAsDouble("waitingroomspawn.y"),
                config.getConfigValueAsDouble("waitingroomspawn.z")
        ));

        loadAreasFromConfig();
    }

    public void loadAreasFromConfig(){

        if(enabled){
          try
            {
                areaHandler.setWaitRoom(config.getConfigValueAsString("waitroom"));
                areaHandler.loadAreas(config.getConfigValueAsList("regions"));
            }catch (RegionNotFoundException e){
                System.out.println(e.getMessage());
                this.disable();
            }
        }

	}

	private void resetWaittime()
	{
		this.countdownToStart = config.getConfigValueAsInt("waittime");
	}

	private void resetRuntime()
	{
		this.countdownToEnd = config.getConfigValueAsInt("runtime");
	}

	public void sendMessage(String msg)
	{
		for (RunsafePlayer player : server.getWorld(worldName).getPlayers()) player.sendColouredMessage(msg);
	}

	public void tick()
	{
		if (enabled)
		{
			if (!this.gamestarted)
			{
				ArrayList<RunsafePlayer> waitingRoomPlayers = areaHandler.getWaitRoomPlayers();
				voteHandler.setCanVote(true);
				if (voteHandler.votePass(waitingRoomPlayers.size()))
				{
					sendMessage(String.format(Constants.MSG_NEW_NEXT_MAP_VOTED, areaHandler.getAreaName(areaHandler.randomNextArea())));
					voteHandler.resetVotes();
				}
				if (waitingRoomPlayers.size() > 1)
				{
					if (countdownToStart % 300 == 0)
					{ //every 5 minutes
						server.broadcastMessage(String.format(Constants.MSG_GAME_START_IN, (countdownToStart / 300) * 5, "minutes"));
					}
					else
					{
						switch (countdownToStart)
						{
							case 180:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 3, "minutes"));
								break;
							case 120:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 2, "minutes"));
								break;
							case 60:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 1, "minute"));
								break;
							case 30:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 30, "seconds"));
								break;
							case 20:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 20, "seconds"));
								break;
							case 10:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 10, "seconds"));
								break;
							case 5:
								sendMessage(String.format(Constants.MSG_GAME_START_IN, 5, "seconds"));
								break;
						}
					}

					countdownToStart--;
				}
				else
				{
					resetWaittime();
				}
				if (countdownToStart <= 0)
				{
					start();
					voteHandler.setCanVote(false);
					voteHandler.resetVotes();
				}
				else
					//moving all players not in creative mode into the waitroom
					for (RunsafePlayer p : RunsafeServer.Instance.getWorld(worldName).getPlayers())
						if (p.getGameMode() != GameMode.CREATIVE && !areaHandler.isInWaitRoom(p))
							p.teleport(areaHandler.getWaitRoomSpawn());

			}
			else
			{
				for (String s : playerHandler.tick())
					sendMessage(s);

				if (playerHandler.isWinner())
				{
					winner();
					return;
				}

				if (countdownToEnd % 300 == 0)
				{
					sendMessage(String.format(Constants.MSG_TIME_REMAINING, (countdownToEnd / 300) * 5, "minutes"));
				}
				else
				{

					switch (countdownToEnd)
					{
						case 180:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 3, "minutes"));
							break;
						case 120:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 2, "minutes"));
							break;
						case 60:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 1, "minute"));
							break;
						case 30:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 30, "seconds"));
							break;
						case 20:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 20, "seconds"));
							break;
						case 10:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
							break;
						case 5:
							sendMessage(String.format(Constants.MSG_TIME_REMAINING, 5, "seconds"));
							break;
					}
				}
				this.countdownToEnd--;
				if (countdownToEnd <= 0)
				{

					winner();

					return;

				}

				// checking all players in world that are not creative mode, move them to the waiting room if not in game
				for (RunsafePlayer p : RunsafeServer.Instance.getWorld(worldName).getPlayers())
					if (!playerHandler.isIngame(p) && p.getGameMode() != GameMode.CREATIVE && !areaHandler.isInWaitRoom(p))
						p.teleport(areaHandler.getWaitRoomSpawn());
			}
		}
	}

	private void winner()
	{
		RunsafePlayer winPlayer = playerHandler.getCurrentLeader();
		if (winPlayer != null)
			server.broadcastMessage(String.format(Constants.GAME_WON, winPlayer.getPrettyName()));
		end();
		playerHandler.reset();
	}

	public String join(RunsafePlayer executor)
	{
		if (this.enabled)
		{

			if (!playerHandler.isIngame(executor))
			{
				executor.teleport(areaHandler.getWaitRoomSpawn());
				executor.setGameMode(GameMode.SURVIVAL);
				return null;
			}
			else
			{
				return Constants.ERROR_COLOR + "You are already in game!";
			}
		}

		return Constants.MSG_COLOR + "headhunters is not enabled";
	}

	public void stop(RunsafePlayer executor)
	{
		if (gamestarted)
		{
			winner();
			sendMessage(String.format(Constants.MSG_GAME_STOPPED, executor.getPrettyName()));
		}
	}

	public int amountHeads(RunsafePlayer player)
	{
		return Util.amountMaterial(player, Item.Decoration.Head.Human.getItem());
	}

	@Override
	public void OnPluginEnabled()
	{
		console.writeColoured((enabled) ? ConsoleColour.GREEN + "Enabled" : ConsoleColour.RED + "Disabled");
		console.write(String.format("loaded %d areas", areaHandler.getAmountLoadedAreas()));
	}

	private final IOutput console;
	private final RunsafeServer server;
	private final VoteHandler voteHandler;
	private final PlayerHandler playerHandler;
	private final AreaHandler areaHandler;
	private String worldName;
	private boolean gamestarted;
	private boolean enabled = false;
	private IConfiguration config;
	private int countdownToStart = -1;
	private int countdownToEnd = -1;
}
