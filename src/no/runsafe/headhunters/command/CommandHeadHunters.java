package no.runsafe.headhunters.command;

import no.runsafe.framework.command.Command;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 13-5-13
 * Time: 16:41
 */
public class CommandHeadHunters extends Command {

    String commandName;
    String description;


    /**
     * Defines the command
     *
     * @param commandName The name of the command. For top level commands, this must be as defined in plugin.yml
     * @param description A short descriptive text of what the command does
     * @param permission  A permission string that a player must have to run the command or null to allow anyone to run it
     * @param arguments   Optional list of required command parameters
     */
    public CommandHeadHunters(String commandName, String description, String permission, String... arguments) {
        super(commandName, description, permission, arguments);

        this.description = description;
        this.commandName = commandName;

    }

    @Override
    public String getUsage(){

        return null;
    }


}
