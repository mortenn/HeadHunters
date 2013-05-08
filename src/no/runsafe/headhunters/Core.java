package no.runsafe.headhunters;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.ChatColour;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.entity.RunsafeEntity;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.material.RunsafeMaterial;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.server.potion.RunsafePotionEffect;
import no.runsafe.framework.timer.IScheduler;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:04
 */
public class Core implements IConfigurationChanged{

    private String worldName;
    private SimpleArea waitingRoom;
    private SimpleArea combatArea;
    private RunsafeLocation waitingRoomSpawn;
    private boolean gamestarted;
    private boolean enabled = true;

    private IConfiguration config;
    private IOutput console;
    private IScheduler scheduler;
    private RunsafeServer server;


    private int countdownToStart = -1;
    private int countdownToEnd = -1;

    private ArrayList<RunsafePlayer> ingamePlayers = new ArrayList<RunsafePlayer>();
    private ArrayList<String> ingamePlayersNames = new ArrayList<String>();
    private int winAmount = 30;
    private int minPlayers = 2;
    private RunsafePlayer leader = null;

    private int top3 = 0;
    private int top3Trigger = 60;


    public EquipmentManager equipmentManager;


    public Core(IConfiguration config, IOutput console, IScheduler scheduler, RunsafeServer server, EquipmentManager equipmentManager) {

        this.config = config;
        this.console = console;
        this.scheduler = scheduler;
        this.server = server;

        this.equipmentManager = equipmentManager;


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


    public void enable() {

        this.config.setConfigValue("enabled", true);
        this.config.save();
        this.enabled = true;
        end(null);
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

    public boolean setWaitingRoomFirstPos(RunsafePlayer player) {

        RunsafeLocation location = player.getLocation();

        if(player.getWorld().getName().equalsIgnoreCase(this.worldName)){
            this.config.setConfigValue("waitingarea.x1",location.getX());
            this.config.setConfigValue("waitingarea.y1",location.getY());
            this.config.setConfigValue("waitingarea.z1",location.getZ());

            this.waitingRoom.setFirstPos(location.getX(), location.getY(), location.getZ());

            this.config.save();
            return true;
        }

        return false;


    }

    public boolean setWaitingRoomSecondPos(RunsafePlayer player) {

        RunsafeLocation location = player.getLocation();

        if(player.getWorld().getName().equalsIgnoreCase(this.worldName)){
            this.config.setConfigValue("waitingarea.x2",location.getX());
            this.config.setConfigValue("waitingarea.y2",location.getY());
            this.config.setConfigValue("waitingarea.z2",location.getZ());

            this.waitingRoom.setSecondPos(location.getX(), location.getY(), location.getZ());

            this.config.save();
            return true;
        }

        return false;


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

        ArrayList<RunsafePlayer> players = waitingRoom.getPlayers(GameMode.SURVIVAL);
        if(players.size() < this.minPlayers){
            this.resetWaittime();
            sendMessage(players, String.format(Constants.MSG_NOT_ENOUGH_PLAYERS, players.size(), this.minPlayers));
            return;
        }
        this.gamestarted = true;
        winAmount = (int) ((players.size() / 2) + players.size() * 3.5);
        this.ingamePlayers = players;
        for(RunsafePlayer player : ingamePlayers){
            ingamePlayersNames.add(player.getName());

        }

        teleportIntoGame(this.ingamePlayers);
        sendMessage(ingamePlayers, String.format(Constants.MSG_START_MESSAGE, winAmount));


    }

    public void end(String endMessage){
        for(RunsafePlayer player: this.combatArea.getPlayers(GameMode.SURVIVAL)){
            teleportIntoWaitRoom(player);
            player.getInventory().clear();
            if(endMessage != null) player.sendColouredMessage(Constants.MSG_COLOR + endMessage);
        }

        List<RunsafeEntity> entities = server.getWorld(worldName).getEntities();
        for(RunsafeEntity entity : entities)
            if(combatArea.pointInArea(entity.getLocation())) entity.remove();


        top3 = 0;
        gamestarted = false;
        ingamePlayers.clear();
        ingamePlayersNames.clear();
        resetWaittime();
        resetRuntime();
        leader = null;

    }

    public String showIngame(){

        String out = "";

        for(RunsafePlayer player: ingamePlayers){
            out = out + player.getPrettyName() + ", ";
        }

        return out;

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

    public void teleportIntoGame(ArrayList<RunsafePlayer> players){
        for(RunsafePlayer player : players){
            teleportIntoGame(player, safeLocation());
        }
    }


    public void teleportIntoGame(RunsafePlayer player, RunsafeLocation loc){
        player.getInventory().clear();
        this.equip(player);
        player.removeBuffs();

        player.addPotionEffect(new RunsafePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 2 )));
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(2f);

        if(loc != null) player.teleport(loc);
    }

    public RunsafeLocation safeLocation(){
        return combatArea.safeLocation();

    }

    public String setSpawn(RunsafePlayer executor) {

        if(waitingRoom.pointInArea(executor.getLocation())){

            this.waitingRoomSpawn = executor.getLocation();

            config.setConfigValue("waitingroomspawn.x", executor.getLocation().getX());
            config.setConfigValue("waitingroomspawn.y", executor.getLocation().getY());
            config.setConfigValue("waitingroomspawn.z", executor.getLocation().getZ());

            config.save();
            return "Successfully set spawn";
        }

        return "You need to be in the waitingroom";
    }

    @Override
    public void OnConfigurationChanged(IConfiguration configuration) {

        this.config = configuration;

        this.enabled = config.getConfigValueAsBoolean("enabled");

        this.worldName = config.getConfigValueAsString("world");
        if(this.worldName == null){
            worldName = "world";
        }

        this.waitingRoomSpawn = new RunsafeLocation(
                server.getWorld(this.worldName),
                config.getConfigValueAsDouble("waitingroomspawn.x"),
                config.getConfigValueAsDouble("waitingroomspawn.y"),
                config.getConfigValueAsDouble("waitingroomspawn.z")
        );

        this.waitingRoom = new SimpleArea(
                config.getConfigValueAsDouble("waitingarea.x1"),
                config.getConfigValueAsDouble("waitingarea.y1"),
                config.getConfigValueAsDouble("waitingarea.z1"),
                config.getConfigValueAsDouble("waitingarea.x2"),
                config.getConfigValueAsDouble("waitingarea.y2"),
                config.getConfigValueAsDouble("waitingarea.z2"),
                server.getWorld(this.worldName)
        );

        this.combatArea = new SimpleArea(
                config.getConfigValueAsDouble("combatarea.x1"),
                config.getConfigValueAsDouble("combatarea.y1"),
                config.getConfigValueAsDouble("combatarea.z1"),
                config.getConfigValueAsDouble("combatarea.x2"),
                config.getConfigValueAsDouble("combatarea.y2"),
                config.getConfigValueAsDouble("combatarea.z2"),
                server.getWorld(this.worldName)
        );

        this.countdownToStart = config.getConfigValueAsInt("waittime");
        this.countdownToEnd = config.getConfigValueAsInt("runtime");


    }

    private void resetWaittime(){
        this.countdownToStart = config.getConfigValueAsInt("waittime");
    }

    private void resetRuntime(){
        this.countdownToEnd = config.getConfigValueAsInt("runtime");
    }

    private void sendMessage(ArrayList<RunsafePlayer> players, String msg){
         for(RunsafePlayer player : players)  player.sendColouredMessage(msg);
    }
    
    public void tick(){

        if(enabled) {
            if (!this.gamestarted) {
                this.teleportIntoWaitRoom(this.combatArea.getPlayers(), GameMode.SURVIVAL);

                if (countdownToStart % 300 == 0) { //every 5 minutes
                    server.broadcastMessage(String.format(Constants.MSG_GAME_START_IN, (countdownToStart / 300) * 5, "minutes"));
                } else {
                    switch (countdownToStart) {
                        case 180:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 3, "minutes"));
                            break;
                        case 120:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 2, "minutes"));
                            break;
                        case 60:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 1, "minute"));
                            break;
                        case 30:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 30, "seconds"));
                            break;
                        case 20:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 20, "seconds"));
                            break;
                        case 10:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 10, "seconds"));
                            break;
                        case 5:
                            sendMessage(this.waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_START_IN, 5, "seconds"));
                            break;


                    }
                }

                countdownToStart--;
                if (countdownToStart <= 0) {
                    start();
                }
            } else {

                RunsafePlayer l = pickWinner();
                int amount = amountHeads(l);
                if(amount >= winAmount){
                    winner(l);
                    return;
                }
                if (l != leader && amount > 0) {
                    leader = l;
                    sendMessage(combatArea.getPlayers(), String.format(Constants.MSG_NEW_LEADER, leader.getPrettyName(), amountHeads(leader)));

                }

                top3++;
                if(top3 >= top3Trigger){
                    top3 = 0;
                    getTop3();
                }


                if (countdownToEnd % 300 == 0) {
                    sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, (countdownToEnd / 300) * 5, "minutes"));
                } else {
                    switch (countdownToEnd) {
                        case 180:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 3, "minutes"));
                            break;
                        case 120:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 2, "minutes"));
                            break;
                        case 60:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 1, "minute"));
                            break;
                        case 30:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 30, "seconds"));
                            break;
                        case 20:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
                            break;
                        case 10:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
                            break;
                        case 5:
                            sendMessage(this.combatArea.getPlayers(), String.format(Constants.MSG_TIME_REMAINING, 5, "seconds"));
                            break;

                    }
                }
                this.countdownToEnd--;
                if (countdownToEnd <= 0) {

                    winner(pickWinner());
                    return;

                }

                if (ingamePlayers.size() == 1) winner(ingamePlayers.get(0));

                for (RunsafePlayer player : combatArea.getPlayers()) {

                    if (player.getGameMode() == GameMode.CREATIVE) continue;

                    if (!ingamePlayersNames.contains(player.getName())) teleportIntoWaitRoom(player);


                }
            }
        }
        else this.teleportIntoWaitRoom(this.combatArea.getPlayers(), GameMode.SURVIVAL);



        
    }

    private RunsafePlayer pickWinner() {
        int topHeads = -1;
        RunsafePlayer winner = null;

        for(RunsafePlayer player : ingamePlayers){
            if(topHeads == -1){
                winner = player;
                topHeads = amountHeads(player);
                continue;
            }
            int playerAmount = amountHeads(player);
            if(playerAmount > topHeads) {
                winner = player;
                topHeads = playerAmount;
            }
        }

        return winner;

    }

    private void winner(RunsafePlayer player) {

        server.broadcastMessage(String.format(Constants.GAME_WON, player.getPrettyName()));
        end(null);

    }


    public String join(RunsafePlayer executor) {

        if(this.enabled){

            if(!isIngame(executor)){
                this.teleportIntoWaitRoom(executor);
                executor.setGameMode(GameMode.SURVIVAL);
                return null;
            }else{
                return Constants.ERROR_COLOR + "You are already in game!";
            }
        }

        return Constants.MSG_COLOR + "headhunters is not enabled";

    }

    //le crappy code xD
    private void getTop3(){

        RunsafePlayer first = null;
        RunsafePlayer second = null;
        RunsafePlayer third = null;

        int headsFirst = 0;
        int headsSecond = 0;
        int headsThird = 0;

        for(RunsafePlayer player : ingamePlayers){
            int amount = amountHeads(player);
            if(amount == 0) continue;
            if(first == null){
                first = player;
                headsFirst = amount;
            }else if(second == null){

                if(amount > headsFirst){
                    second = first;
                    headsSecond = headsFirst;

                    first = player;
                    headsFirst = amount;
                }
            }else if(third == null){
                if(amount > headsFirst){
                    third = second;
                    headsThird = headsSecond;

                    second = first;
                    headsSecond = headsFirst;

                    first = player;
                    headsFirst = amount;
                }else if(amount > headsSecond){
                    third = second;
                    headsThird = headsSecond;

                    second = player;
                    headsSecond = amount;
                }else if(amount > headsThird){
                    third = player;
                    headsThird = amount;
                }
            }else{

                if(amount > headsFirst){
                    third = second;
                    headsThird = headsSecond;

                    second = first;
                    headsSecond = headsFirst;

                    first = player;
                    headsFirst = amount;
                }else if(amount > headsSecond){
                    third = second;
                    headsThird = headsSecond;

                    second = player;
                    headsSecond = amount;
                }else if(amount > headsThird){
                    third = player;
                    headsThird = amount;
                }
            }
        }

        ArrayList<RunsafePlayer> toSendPlayers = ingamePlayers;

        if(first != null) sendMessage(toSendPlayers, Constants.MSG_TOP3);
        if(first != null) sendMessage(toSendPlayers, String.format(Constants.MSG_TOP_PLAYER, first.getPrettyName(), headsFirst));
        if(second != null) sendMessage(toSendPlayers, String.format(Constants.MSG_TOP_PLAYER, second.getPrettyName(), headsSecond));
        if(third != null) sendMessage(toSendPlayers, String.format(Constants.MSG_TOP_PLAYER, third.getPrettyName(), headsThird));
    }



    public void equip(RunsafePlayer player){
        equipmentManager.equip(player);
    }

    public void stop(RunsafePlayer executor) {
        if(gamestarted){
            winner(pickWinner());
            sendMessage(waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_STOPPED, executor.getPrettyName()));
        }

    }

    public int amountMaterial(RunsafePlayer player, RunsafeMaterial search){
        int amount = 0;
        for(RunsafeItemStack content : (ArrayList<RunsafeItemStack>) player.getInventory().getContents())
            if(content.getItemId() == search.getMaterialId()) amount += content.getAmount();
        return amount;
    }

    public int amountHeads(RunsafePlayer player){
        return amountMaterial(player, new RunsafeMaterial(Material.SKULL_ITEM));
    }

    public void leave(RunsafePlayer player) {

        if(isIngame(player)){
            player.getWorld().dropItem(player.getEyeLocation(), new RunsafeItemStack(Material.SKULL_ITEM.getId(), amountHeads(player), (short) 3));
            player.getInventory().clear();
            player.teleport(waitingRoomSpawn);
            ingamePlayers.remove(player);
            ingamePlayersNames.remove(player.getName());
            sendMessage(ingamePlayers, String.format(Constants.MSG_LEAVE, player.getPrettyName()));
            if(ingamePlayersNames.size() <= 1) winner(pickWinner());
        }

    }

    public ArrayList<RunsafePlayer> getPlayers(RunsafeLocation loc, int radius){

        ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();
        for(RunsafePlayer player : ingamePlayers)
            if(Util.distance(loc, player.getLocation()) < radius) players.add(player);

        return players;

    }

    public boolean isIngame(RunsafePlayer player){
        return (ingamePlayersNames.contains(player.getName()));
    }

    public String getWorldName() {
        return worldName;
    }

    public void setAreaPos(boolean first, String areaname, RunsafeLocation location) {

        if(first) combatArea.setFirstPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        else combatArea.setSecondPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());


    }

    public void setWaitRoomPos(boolean first, RunsafeLocation location) {

        if(first) waitingRoom.setFirstPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        else waitingRoom.setSecondPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

    }
}
