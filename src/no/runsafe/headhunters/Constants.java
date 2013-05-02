package no.runsafe.headhunters;

import no.runsafe.framework.output.ChatColour;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:09
 */
public class Constants {

    public static final String MSG_COLOR = ChatColour.AQUA + "";
    public static final String ERROR_COLOR = ChatColour.RED + "";

    public static final String GAME_STOPPED_IN_PROGRESS_MSG = "Headhunters has been disabled.";
    public static final String MSG_DISABLED = "Headhunters is disabled";
    public static final String GAME_WON = "%s " + MSG_COLOR + "claims victory in headhunters!";
    public static final String MSG_GAMESTOPPED = "%s " + MSG_COLOR + "has stopped the game";
    public static final String MSG_GAME_START_IN = MSG_COLOR + "Headhunters game will start in %d %s!";
    public static final String MSG_NOT_ENOUGH_PLAYERS = MSG_COLOR + "Not enough players to start; %d of %d needed.";
    public static final String MSG_TIME_REMAINING = MSG_COLOR + "Time remaining: %d %s.";
}
