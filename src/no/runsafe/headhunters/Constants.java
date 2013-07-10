package no.runsafe.headhunters;


import no.runsafe.framework.text.ChatColour;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:09
 */
public class Constants
{
	public static final String MSG_COLOR = ChatColour.AQUA + "";
	public static final String ERROR_COLOR = ChatColour.RED + "";

	public static final String GAME_STOPPED_IN_PROGRESS_MSG = "Headhunters has been disabled.";
	public static final String MSG_DISABLED = "Headhunters is disabled";
	public static final String GAME_WON = "%s " + MSG_COLOR + "claims victory in headhunters!";
	public static final String MSG_GAME_STOPPED = "%s " + MSG_COLOR + "has stopped the game";
	public static final String MSG_GAME_START_IN = MSG_COLOR + "Headhunters game will start in %d %s!";
	public static final String MSG_NOT_ENOUGH_PLAYERS = MSG_COLOR + "Not enough players to start; %d of %d needed.";
	public static final String MSG_TIME_REMAINING = MSG_COLOR + "Time remaining: %d %s.";
	public static final String MSG_NEW_LEADER = MSG_COLOR + "%s has taken the lead with %d heads!";
	public static final String MSG_START_MESSAGE = MSG_COLOR + "Gather %d heads to win this match!";
	public static final String MSG_USE_LEAVE = MSG_COLOR + "Use" + ChatColour.BLUE + " /hh leave " + MSG_COLOR + "instead!";
	public static final String MSG_LEAVE = "%s has left the game.";
	public static final String MSG_TOP3 = MSG_COLOR + "Top 3 at the moment:";
	public static final String MSG_TOP_PLAYER = "%s" + MSG_COLOR + " with %d heads.";
	public static final String MSG_NEXT_MAP = MSG_COLOR + "Next map: " + ChatColour.WHITE + "%s";
	public static final String MSG_CANT_VOTE = ERROR_COLOR + "You can not vote at the moment.";
	public static final String MSG_ALREADY_VOTED = ERROR_COLOR + "You have already voted!";
	public static final String MSG_VOTE_CAST = MSG_COLOR + "Your vote has been cast";
	public static final String MSG_NEED_IN_WAITROOM = ERROR_COLOR + "You need to be in the waitroom to do that!";
	public static final String MSG_NEW_NEXT_MAP_VOTED = MSG_COLOR + "Vote passed! Next map will be " + ChatColour.WHITE + "%s";
}
