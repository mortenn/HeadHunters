package nl.naxanria.headhunters.handler;

import nl.naxanria.headhunters.Constants;
import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;

public class VoteHandler
{
	public VoteHandler()
	{
		votedPlayers = new ArrayList<String>();
	}

	public void setCanVote(boolean vote)
	{
		canVote = vote;
	}

	public void setMinVotes(int votes)
	{
		minVotes = votes;
	}

	public void setMinPercentage(int percent)
	{
		minPercentage = percent;
	}

	public boolean votePass(int amount)
	{
		return (canVote && votedPlayers.size() >= minVotes && (votedPlayers.size() / amount) * 100 > minPercentage);
	}

	public void resetVotes()
	{
		votedPlayers.clear();
	}

	public String vote(RunsafePlayer player)
	{
		if (!canVote)
			return Constants.MSG_CANT_VOTE;
		if (votedPlayers.contains(player.getName()))
			return Constants.MSG_ALREADY_VOTED;
		votedPlayers.add(player.getName());
		return Constants.MSG_VOTE_CAST;
	}

	private final ArrayList<String> votedPlayers;
	private int minVotes = 1;
	private int minPercentage = 50;
	private boolean canVote = false;
}
