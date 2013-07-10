package no.runsafe.headhunters;


import no.runsafe.framework.minecraft.player.RunsafePlayer;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 16-5-13
 * Time: 17:10
 */
public class VoteHandler {


    private ArrayList<String> votedPlayers;
    private int minVotes = 1;
    private int minPerc = 50;
    private boolean canVote = false;

    public VoteHandler(){
          votedPlayers = new ArrayList<String>();
    }

    public void setCanVote(boolean vote){
        canVote = vote;
    }

    public void setMinVotes(int votes){
        minVotes = votes;
    }

    public void setMinPerc(int perc){
        minPerc = perc;
    }

    public boolean votePass(int amount){
        return (canVote && votedPlayers.size() >= minVotes && (votedPlayers.size()/amount)*100 > minPerc);
    }

    public void resetVotes(){
        votedPlayers.clear();
    }

    public String vote(RunsafePlayer player){
        if(!canVote) return Constants.MSG_CANT_VOTE;
        if(votedPlayers.contains(player.getName())) return Constants.MSG_ALREADY_VOTED;
        votedPlayers.add(player.getName());
        return Constants.MSG_VOTE_CAST;
    }


}
