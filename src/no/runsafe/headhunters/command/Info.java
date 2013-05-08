package no.runsafe.headhunters.command;

import no.runsafe.framework.command.ExecutableCommand;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.ICommandExecutor;
import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 8-5-13
 * Time: 13:53
 */
public class Info extends ExecutableCommand {

    private Core core;
    private IOutput console;

    public Info(Core core, IOutput console){
        super("info", "shows info about the current game", null);

        this.core = core;
        this.console = console;

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
                info.add(String.format("Time remaining &f%d:%d", min, sec));
                info.add(String.format("Current leader: %s", core.pickWinner().getPrettyName()));
                info.add(String.format("Heads needed: &f%d", core.getAmountNeeded()));
            }else{

                int min = (int)Math.floor(core.getTimeToStart() / 60);
                int sec = core.getTimeToStart() - min * 60;
                info.add(String.format("Game will start in &f%d:%d", min, sec));


            }
            info.add("------------");

            for(String str : info)
                executor.sendColouredMessage(Constants.MSG_COLOR + str);
            return null;
        }
        return Constants.ERROR_COLOR + "Headhunters is disabled";
    }
}
