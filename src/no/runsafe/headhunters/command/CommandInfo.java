package no.runsafe.headhunters.command;

import no.runsafe.framework.command.ExecutableCommand;
import no.runsafe.framework.server.ICommandExecutor;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;
import no.runsafe.headhunters.Util;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 8-5-13
 * Time: 13:53
 */
public class CommandInfo extends ExecutableCommand {

    private Core core;


    public CommandInfo(Core core){
        super("info", "shows info about the current game", null);

        this.core = core;

    }

    @Override
    public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters) {

        ArrayList<String> info = new ArrayList<String>();
        if(core.getEnabled()){
            info.add("-------------");

            if(core.getGamestarted()){
                info.add("Current headhunters match info");
                int min = (int)Math.floor(core.getTimeToEnd() / 60);
                int sec = core.getTimeToEnd() - min * 60;
                info.add(String.format("Time remaining &f%s:%s", min, Util.fillZeros(sec)));
                info.add(String.format("Current leader: %s", core.pickWinner().getPrettyName()));
                info.add(String.format("Heads needed: &f%d", core.getAmountNeeded()));
                info.add(String.format("Current Map: &f%s", core.getCurrentMap()));
            }else{

                int min = (int)Math.floor(core.getTimeToStart() / 60);
                int sec = core.getTimeToStart() % 60;
                info.add(String.format("Game will start in &f%d:%s", min, Util.fillZeros(sec)));
                info.add(String.format(Constants.MSG_NEXT_MAP, core.getNextRegion()));


            }
            info.add("------------");

            for(String str : info)
                executor.sendColouredMessage(Constants.MSG_COLOR + str);
            return null;
        }
        return Constants.ERROR_COLOR + "Headhunters is disabled";
    }
}
