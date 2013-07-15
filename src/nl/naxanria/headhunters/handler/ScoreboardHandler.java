package nl.naxanria.headhunters.handler;

import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    public ScoreboardHandler()
    {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.emptyScoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("heads", "heads");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    public void addScoreboard(RunsafePlayer player)
    {
        Player rawPlayer = (Player) player.getRaw();
        rawPlayer.setScoreboard(scoreboard);
    }

    public void removeScoreBoard(RunsafePlayer player)
    {
        Player rawPlayer = (Player) player.getRaw();
        scoreboard.resetScores(rawPlayer);
        rawPlayer.setScoreboard(emptyScoreboard);
    }


    public void updateScoreboard(RunsafePlayer player, int heads)
    {
        Player rawPlayer = (Player) player.getRaw();
        Score score = objective.getScore(rawPlayer);
        score.setScore(heads);
    }

    private final Scoreboard scoreboard;
    private final Scoreboard emptyScoreboard;
    private final Objective objective;

}
