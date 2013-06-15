package no.runsafe.headhunters;


import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import no.runsafe.framework.minecraft.Buff;
import no.runsafe.framework.minecraft.Item;

import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.entity.RunsafeEntity;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.framework.text.ChatColour;
import no.runsafe.framework.text.ConsoleColour;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:04
 */
public class Core implements IConfigurationChanged, IPluginEnabled {

    private String worldName;
    private SimpleArea waitingRoom;
    private RunsafeLocation waitingRoomSpawn;
    private boolean gamestarted;
    private boolean enabled = true;

    private IConfiguration config;
    private IOutput console;
    private IScheduler scheduler;
    private RunsafeServer server;

    private int countdownToStart = -1;
    private int countdownToEnd = -1;

    private int minPlayers = 2;
    private RunsafePlayer leader = null;

    private ArrayList<String> regions;
    private ArrayList<SimpleArea> areas;
    private int currRegion;
    private int nextRegion;

    private VoteHandler voteHandler;

    public EquipmentManager equipmentManager;
    public PlayerHandler playerHandler;

    public Core(IConfiguration config, IOutput console, IScheduler scheduler, RunsafeServer server, EquipmentManager equipmentManager, VoteHandler voteHandler,
                    PlayerHandler playerHandler) {

        this.config = config;
        this.console = console;
        this.scheduler = scheduler;
        this.server = server;

        this.equipmentManager = equipmentManager;
        this.playerHandler = playerHandler;
        this.voteHandler = voteHandler;

        this.gamestarted = false;

        this.scheduler.startSyncRepeatingTask(
                new Runnable() {
                    @Override
                    public void run() {
                        tick();
                    }
                }, 1, 1
        );

    }

    public boolean enable() {
       loadAreas();
        if(this.areas.size() == 0){
            return false;
        }else{
            this.config.setConfigValue("enabled", true);
            this.config.save();
            this.enabled = true;
            end(null);
            return true;
        }
    }

    public void disable() {

        if(this.gamestarted){
            this.end(Constants.GAME_STOPPED_IN_PROGRESS_MSG);
        }

        gamestarted = false;
        this.config.setConfigValue("enabled", false);
        this.config.save();
        this.enabled = false;

    }

    public boolean getGamestarted(){
        return gamestarted;
    }

    public boolean getEnabled(){
        return enabled;
    }

    public int getTimeToStart(){
        return countdownToStart;
    }

    public int getTimeToEnd(){
        return countdownToEnd;
    }

    public String startInTime(Integer time) {

        if(!this.enabled) return Constants.MSG_DISABLED;

        if(!gamestarted){

            if(time <= 0) time = 1;

            countdownToStart = time;

            return Constants.MSG_COLOR + "Game will start in " + ChatColour.WHITE + time + Constants.MSG_COLOR + " seconds";
        }

        return "Game already started";
    }

    public void start(){

        if(areas.size() == 0) {
            console.write("Can not start headhunters game; There are no areas");
            disable();
            return;
        }

        ArrayList<RunsafePlayer> players = waitingRoom.getPlayers(GameMode.SURVIVAL);
        if(players.size() < this.minPlayers){
            this.resetWaittime();
            sendMessage(String.format(Constants.MSG_NOT_ENOUGH_PLAYERS, players.size(), this.minPlayers));
            return;
        }

        this.gamestarted = true;
        currRegion = nextRegion;
        playerHandler.start(players, areas.get(currRegion));
        sendMessage(String.format("Map: &f%s", regions.get(currRegion)));
        sendMessage(String.format(Constants.MSG_START_MESSAGE, playerHandler.getWinAmount()));
    }

    public void end(String endMessage){

        playerHandler.end();

        List<RunsafeEntity> entities = server.getWorld(worldName).getEntities();
        for(RunsafeEntity entity : entities)
            if(!(entity instanceof RunsafePlayer) && areas.get(currRegion).pointInArea(entity.getLocation())) entity.remove();

        nextRegion = Util.getRandom(0, regions.size());
        gamestarted = false;

        resetWaittime();
        resetRuntime();

    }

    public void teleportIntoWaitRoom(ArrayList<RunsafePlayer> players){
        for(RunsafePlayer player : players)
            this.teleportIntoWaitRoom(player);

    }

    private void teleportIntoWaitRoom(ArrayList<RunsafePlayer> players, GameMode mode) {
        if(mode == null) teleportIntoWaitRoom(players);

        for(RunsafePlayer player : players)
            if(player.getGameMode() == mode) teleportIntoWaitRoom(player);
    }

    public void teleportIntoWaitRoom(RunsafePlayer player){
        player.teleport(this.waitingRoomSpawn);
        player.getInventory().clear();
    }

    public RunsafeLocation safeLocation(){
        return areas.get(currRegion).safeLocation();
    }

    @Override
    public void OnConfigurationChanged(IConfiguration configuration) {

        this.config = configuration;

        this.enabled = config.getConfigValueAsBoolean("enabled");

        this.worldName = config.getConfigValueAsString("world");
        if(this.worldName == null){
            worldName = "world";
        }
        playerHandler.setOperatingWorld(worldName);

        this.waitingRoomSpawn = new RunsafeLocation(
                server.getWorld(this.worldName),
                config.getConfigValueAsDouble("waitingroomspawn.x"),
                config.getConfigValueAsDouble("waitingroomspawn.y"),
                config.getConfigValueAsDouble("waitingroomspawn.z")
        );

        playerHandler.setDefaultSpawn(waitingRoomSpawn);

        this.waitingRoom = new SimpleArea(
                config.getConfigValueAsDouble("waitingarea.x1"),
                config.getConfigValueAsDouble("waitingarea.y1"),
                config.getConfigValueAsDouble("waitingarea.z1"),
                config.getConfigValueAsDouble("waitingarea.x2"),
                config.getConfigValueAsDouble("waitingarea.y2"),
                config.getConfigValueAsDouble("waitingarea.z2"),
                server.getWorld(this.worldName)
        );

        this.countdownToStart = config.getConfigValueAsInt("waittime");
        this.countdownToEnd = config.getConfigValueAsInt("runtime");
        this.voteHandler.setMinPerc(config.getConfigValueAsInt("vote.min-percent"));
        this.voteHandler.setMinVotes(config.getConfigValueAsInt("vote.min-votes"));
        regions = (ArrayList<String>) config.getConfigValueAsList("regions");
    }

    public void loadAreas(){
        areas =  new ArrayList<SimpleArea>();
        for(String reg : regions)
            areas.add(new SimpleArea(reg, server.getWorld(worldName)));
        nextRegion = Util.getRandom(0, regions.size());
    }

    private void resetWaittime(){
        this.countdownToStart = config.getConfigValueAsInt("waittime");
    }

    private void resetRuntime(){
        this.countdownToEnd = config.getConfigValueAsInt("runtime");
    }

    private void sendMessage( String msg){

         for(RunsafePlayer player : server.getWorld(worldName).getPlayers())  player.sendColouredMessage(msg);
    }
    
    public void tick(){

        if(enabled) {
            if (!this.gamestarted) {

                ArrayList<RunsafePlayer> waitingRoomPlayers = this.waitingRoom.getPlayers();
                voteHandler.setCanVote(true);
                if(voteHandler.votePass(waitingRoomPlayers.size())){
                    nextRandomRegion();
                    sendMessage(String.format(Constants.MSG_NEW_NEXT_MAP_VOTED, regions.get(nextRegion)));
                    voteHandler.resetVotes();
                }

                for(SimpleArea combatArea: areas)
                    this.teleportIntoWaitRoom(combatArea.getPlayers(), GameMode.SURVIVAL);

                if (countdownToStart % 300 == 0) { //every 5 minutes
                    server.broadcastMessage(String.format(Constants.MSG_GAME_START_IN, (countdownToStart / 300) * 5, "minutes"));
                } else {
                    switch (countdownToStart) {
                        case 180:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 3, "minutes"));
                            break;
                        case 120:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 2, "minutes"));
                            break;
                        case 60:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 1, "minute"));
                            break;
                        case 30:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 30, "seconds"));
                            break;
                        case 20:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 20, "seconds"));
                            break;
                        case 10:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 10, "seconds"));
                            break;
                        case 5:
                            sendMessage(String.format(Constants.MSG_GAME_START_IN, 5, "seconds"));
                            break;
                    }
                }

                countdownToStart--;
                if (countdownToStart <= 0) {
                    start();
                    voteHandler.setCanVote(false);
                    voteHandler.resetVotes();
                }

                //moving all players not in creative mode into waitroom
                for(RunsafePlayer p : RunsafeServer.Instance.getWorld(worldName).getPlayers())
                    if(p.getGameMode() != GameMode.CREATIVE && !waitingRoom.pointInArea(p.getLocation()))
                        p.teleport(waitingRoomSpawn);

            } else {
                  playerHandler.tick();

                if (countdownToEnd % 300 == 0) {
                    sendMessage(String.format(Constants.MSG_TIME_REMAINING, (countdownToEnd / 300) * 5, "minutes"));
                } else {

                    switch (countdownToEnd) {
                        case 180:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 3, "minutes"));
                            break;
                        case 120:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 2, "minutes"));
                            break;
                        case 60:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 1, "minute"));
                            break;
                        case 30:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 30, "seconds"));
                            break;
                        case 20:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
                            break;
                        case 10:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
                            break;
                        case 5:
                            sendMessage(String.format(Constants.MSG_TIME_REMAINING, 5, "seconds"));
                            break;

                    }
                }
                this.countdownToEnd--;
                if (countdownToEnd <= 0) {

                    //todo: winner at timeout
                    return;

                }

                // checking all players in world that are not creative mode, move them to the waiting room if not in game

               for(RunsafePlayer p : RunsafeServer.Instance.getWorld(worldName).getPlayers())
                   if(!playerHandler.isIngame(p) && p.getGameMode() != GameMode.CREATIVE && !waitingRoom.pointInArea(p.getLocation()))
                       p.teleport(waitingRoomSpawn);
            }
        }
    }




    public RunsafePlayer pickWinner() {


        //todo:handled by playerHandler
//        int topHeads = -1;
//        RunsafePlayer winner = null;
//
//        for(RunsafePlayer player : ingamePlayers){
//            if(topHeads == -1){
//                winner = player;
//                topHeads = amountHeads(player);
//                continue;
//            }
//            int playerAmount = amountHeads(player);
//            if(playerAmount > topHeads) {
//                winner = player;
//                topHeads = playerAmount;
//            }
//        }

        return null;

    }

    private void winner(RunsafePlayer player) {

        server.broadcastMessage(String.format(Constants.GAME_WON, player.getPrettyName()));
        end(null);

    }

    public String join(RunsafePlayer executor) {

        if(this.enabled){

            if(!playerHandler.isIngame(executor)){
                this.teleportIntoWaitRoom(executor);
                executor.setGameMode(GameMode.SURVIVAL);
                return null;
            }else{
                return Constants.ERROR_COLOR + "You are already in game!";
            }
        }

        return Constants.MSG_COLOR + "headhunters is not enabled";

    }

    public void stop(RunsafePlayer executor) {
        if(gamestarted){
            winner(pickWinner());
            sendMessage(String.format(Constants.MSG_GAME_STOPPED, executor.getPrettyName()));
        }

    }

    public int amountHeads(RunsafePlayer player){
        return Util.amountMaterial(player, Item.Decoration.Head.Human.getItem());
    }


    public ArrayList<RunsafePlayer> getPlayers(RunsafeLocation loc, int radius){
        //todo:handled by player handler
//        ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();
//        for(RunsafePlayer player : ingamePlayers)
//            if(Util.distance(loc, player.getLocation()) < radius) players.add(player);

        return null;

    }

    public void setWaitRoomPos(boolean first, RunsafeLocation location) {

        if(first) waitingRoom.setFirstPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        else waitingRoom.setSecondPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        waitingRoom.sortCoords();

    }

    public void setWaitRoomSpawn(RunsafeLocation location) {
        this.waitingRoomSpawn = location;

    }

    @Override
    public void OnPluginEnabled() {

        this.loadAreas();
        console.writeColoured((enabled) ? ConsoleColour.GREEN + "Enabled" : ConsoleColour.RED + "Disabled");
        console.write(String.format("loaded %d areas", areas.size()));

    }

    public ArrayList<SimpleArea> getAreas() {
        return areas;
    }

    public ArrayList<String> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<String> regions) {
        this.regions = regions;
    }

    public String getCurrentMap() {
        return regions.get(currRegion);
    }

    public String isInCombatRegion(RunsafeLocation loc) {

        for(SimpleArea area : areas)
            if(area.pointInArea(loc)){
                return area.getRegionName();
            }

        return null;
    }

    public String getNextRegion() {
        return regions.get(nextRegion);
    }

    public boolean isInWaitroom(RunsafePlayer player) {

        return (waitingRoom.pointInArea(player.getLocation()));

    }

    public void nextRandomRegion() {
        nextRegion = Util.getRandom(0, areas.size(), nextRegion);
    }


    public void setNextRegion(String next) {

        int i = 0;

        for(String region : regions){
            if(region.equalsIgnoreCase(next)){
                nextRegion = i;
            }
            i++;
        }

    }

    public String getAvailableAreas() {
        StringBuilder available = new StringBuilder();
        boolean first = true;
        for(String region : regions){
            if(!first) available.append(",");
            available.append(region);
            first = false;
        }

        return available.toString();
    }

    public String getWorldName() {
        return worldName;
    }
}
