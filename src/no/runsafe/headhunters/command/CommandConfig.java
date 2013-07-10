package no.runsafe.headhunters.command;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;

import no.runsafe.headhunters.Constants;
import no.runsafe.headhunters.Core;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 6-6-13
 * Time: 15:19
 */
public class CommandConfig extends ExecutableCommand {

    private Core core;
    private IConfiguration config;

    public CommandConfig(IConfiguration config, Core core) {
        super("config", "Change the config", "headhunters.admin.config", "key", "value");
        this.captureTail();
        this.config = config;
        this.core = core;
    }

    @Override
    public String OnExecute(ICommandExecutor executor, HashMap<String, String> parameters) {

        if(core.getEnabled()) return Constants.ERROR_COLOR + "Disable headhunters first!";

        String key = parameters.get("key");
        String value = parameters.get("value");

        if(config.getConfigValueAsString(key) == null)
            return "&cKey &f" + key + "&c does not exist";

        config.setConfigValue(key, value);

        return "&bSet &f" + key + "&b to &e" + value;
    }
}
