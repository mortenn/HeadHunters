package no.runsafe.headhunters.event;


import no.runsafe.framework.api.event.player.IPlayerRightClick;
import no.runsafe.framework.minecraft.Buff;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.headhunters.AreaHandler;
import no.runsafe.headhunters.PlayerHandler;
import no.runsafe.headhunters.Util;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 12:55
 */
public class PlayerRightClick implements IPlayerRightClick {

    private RunsafeServer server;
    private final PlayerHandler playerHandler;
    private final AreaHandler areaHandler;


    public PlayerRightClick( RunsafeServer server, PlayerHandler playerHandler, AreaHandler areaHandler){
        this.server = server;
        this.playerHandler = playerHandler;
        this.areaHandler = areaHandler;
    }


    @Override
    public boolean OnPlayerRightClick(RunsafePlayer player, RunsafeMeta usingItem, RunsafeBlock targetBlock) {

        if(playerHandler.isIngame(player)){
            boolean used = false;

            if(usingItem != null){

                RunsafeLocation location = (targetBlock != null) ? targetBlock.getLocation() : player.getLocation();

                int itemID = usingItem.getItemId();
                if(itemID == Material.SLIME_BALL.getId()){

                    //visual effect...
                    server.getWorld(playerHandler.getWorldName()).playEffect(location, Effect.POTION_BREAK, 16426);

                    ArrayList<RunsafePlayer> hitPlayers = playerHandler.getIngamePlayers(location, 5);
                    for(RunsafePlayer hitPlayer : hitPlayers)
                        if(!hitPlayer.getName().equalsIgnoreCase(player.getName())) {
                            Buff.Utility.Movement.DecreaseSpeed.duration(3).amplification(5).applyTo(hitPlayer);
                            Buff.Utility.DigSpeed.Decrease.duration(6).amplification(5);

                        }
                    used = true;

                }else if(itemID == Material.MAGMA_CREAM.getId()){

                    ArrayList<RunsafePlayer> hitPlayers = playerHandler.getIngamePlayers(location, 5);
                    for(RunsafePlayer hitPlayer : hitPlayers)
                        if(!hitPlayer.getName().equalsIgnoreCase(player.getName())) {
                            hitPlayer.strikeWithLightning(true);
                            hitPlayer.setHealth(Math.max(hitPlayer.getHealth() - 4, 0));
                            hitPlayer.setFireTicks(90);
                        }
                    used = true;

                }else if(itemID == Material.NETHER_STAR.getId()){
                    used = true;

                    if(Util.actPercentage(95)){
                        RunsafeLocation newLocation = areaHandler.getSafeLocation();
                        if(newLocation != null)
                            player.teleport(newLocation);
                        else
                            playerHandler.remove(player);
                    }else{
                        server.getWorld(playerHandler.getWorldName()).createExplosion(player.getLocation(), 2f, true, false);
                    }

                }else if(itemID == Material.BLAZE_ROD.getId()){

                    player.Launch(Material.FIREBALL.name());
                    RunsafeServer.Instance.getWorld(playerHandler.getWorldName()).getRaw().playSound(player.getLocation().getRaw(), Sound.GHAST_FIREBALL, 1f, 1f);
                    used = true;

                }else if(itemID == Material.INK_SACK.getId()){
                    ArrayList<RunsafePlayer> hitPlayers = playerHandler.getIngamePlayers(location, 5);
                    for(RunsafePlayer hitPlayer : hitPlayers)
                        if(!hitPlayer.getName().equalsIgnoreCase(player.getName())){
                            Buff.Combat.Blindness.duration(3).amplification(6).applyTo(hitPlayer);
                            Buff.Combat.Damage.Decrease.duration(3).amplification(3).applyTo(hitPlayer);
                        }
                    used = true;

                }else if(itemID == Material.GHAST_TEAR.getId()){
                    playerHandler.teleportAllPlayers(player.getLocation());
                    player.teleport(areaHandler.getSafeLocation());
                    used = true;
                }

            }

            if(used){

                RunsafeMeta items = player.getItemInHand();
                items.setAmount(items.getAmount() - 1);

                player.getInventory().setItemInHand(items);
            }

            return !used;
        }

        return true;

    }

}
