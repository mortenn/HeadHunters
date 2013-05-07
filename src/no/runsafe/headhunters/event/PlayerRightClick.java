package no.runsafe.headhunters.event;

import no.runsafe.framework.event.player.IPlayerRightClick;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.headhunters.Core;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 3-5-13
 * Time: 12:55
 */
public class PlayerRightClick implements IPlayerRightClick {

    private final Core core;

    public PlayerRightClick(Core core){
        this.core = core;
    }


    @Override
    public boolean OnPlayerRightClick(RunsafePlayer player, RunsafeItemStack usingItem, RunsafeBlock targetBlock) {

        return !this.core.rightClick(player, usingItem, targetBlock);

    }
}
