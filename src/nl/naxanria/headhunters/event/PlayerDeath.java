package nl.naxanria.headhunters.event;

import net.minecraft.server.v1_6_R2.Packet205ClientCommand;
import nl.naxanria.headhunters.Core;
import nl.naxanria.headhunters.RandomItem;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.player.IPlayerDeathEvent;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import nl.naxanria.headhunters.PlayerHandler;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;

import java.util.List;

public class PlayerDeath implements IPlayerDeathEvent
{
	public PlayerDeath(Core core, RandomItem randomItem, PlayerHandler playerHandler, IScheduler scheduler)
	{
		this.core = core;
		this.randomItem = randomItem;
		this.playerHandler = playerHandler;
		this.scheduler = scheduler;
	}

	@Override
	public void OnPlayerDeathEvent(RunsafePlayerDeathEvent event)
	{

		final RunsafePlayer player = event.getEntity();

		if (playerHandler.isIngame(player))
		{


			RunsafePlayer killer = player.getKiller();
			if (killer != null) killer.setHealth(Math.min(killer.getHealth() + 4, 20));
			event.setDroppedXP(0);
			int amount = core.amountHeads(event.getEntity());
			List<RunsafeMeta> items = event.getDrops();
			List<RunsafeMeta> toDrop = randomItem.getCleanedDrops(items);
			items.clear();
			items.addAll(toDrop);
			items.add(randomItem.get());
			event.setDrops(items);
			RunsafeMeta heads = Item.Decoration.Head.Human.getItem();
			heads.setAmount(amount + 1);
			player.getWorld().dropItem(
				player.getEyeLocation(),
				heads
			);
			//autorespawning
			scheduler.startSyncTask(new Runnable()
			{
				@Override
				public void run()
				{
					Packet205ClientCommand packet205ClientCommand = new Packet205ClientCommand();
					packet205ClientCommand.a = 1;
					((CraftPlayer) player.getRawPlayer()).getHandle().playerConnection.a(packet205ClientCommand);
				}
			}, 10L);

		}
	}

	private final RandomItem randomItem;
	private final Core core;
	private final PlayerHandler playerHandler;
	private final IScheduler scheduler;
}