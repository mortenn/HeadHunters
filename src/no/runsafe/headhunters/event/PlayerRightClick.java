package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerRightClick;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.server.potion.RunsafePotionEffect;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.Util;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 12:55
 */
public class PlayerRightClick implements IPlayerRightClick {

    private Core core;
    private RunsafeServer server;

    public PlayerRightClick(Core core, RunsafeServer server){
        this.core = core;
        this.server = server;

    }


    @Override
    public boolean OnPlayerRightClick(RunsafePlayer player, RunsafeItemStack usingItem, RunsafeBlock targetBlock) {

        if(core.isIngame(player)){
            boolean used = false;

            if(usingItem != null){

                RunsafeLocation location = (targetBlock != null) ? targetBlock.getLocation() : player.getLocation();

                int itemID = usingItem.getItemId();
                if(itemID == Material.SLIME_BALL.getId()){
                    RunsafePotionEffect slow = new RunsafePotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 4));
                    RunsafePotionEffect hitSlow = new RunsafePotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 60, 4));
                    //visual effect...
                    server.getWorld(core.getWorldName()).playEffect(location, Effect.POTION_BREAK, 2);

                    ArrayList<RunsafePlayer> hitPlayers = core.getPlayers(location, 5);
                    for(RunsafePlayer hitPlayer : hitPlayers)
                        if(!hitPlayer.getName().equalsIgnoreCase(player.getName())) {
                            hitPlayer.addPotionEffect(slow);
                            hitPlayer.addPotionEffect(hitSlow);
                        }
                    used = true;

                }else if(itemID == Material.MAGMA_CREAM.getId()){



                    ArrayList<RunsafePlayer> hitPlayers = core.getPlayers(location, 5);
                    for(RunsafePlayer hitPlayer : hitPlayers)
                        if(!hitPlayer.getName().equalsIgnoreCase(player.getName())) {
                            hitPlayer.strikeWithLightning(false);
                        }
                    used = true;


                }else if(itemID == Material.NETHER_STAR.getId()){
                    used = true;

                    if(Util.actPercentage(95)){
                        player.teleport(core.safeLocation());
                    }else{
                        server.getWorld(core.getWorldName()).createExplosion(player.getLocation(), 2f, true);
                    }


                }

            }

            if(used){

                RunsafeItemStack stack = player.getItemInHand();
                stack.setAmount(stack.getAmount() - 1);

                player.getInventory().setItemInHand(stack);
            }

            return !used;
        }

        return true;

    }
}
