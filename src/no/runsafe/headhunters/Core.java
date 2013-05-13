package no.runsafe.headhunters;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.event.IPluginEnabled;
import no.runsafe.framework.output.ChatColour;
import no.runsafe.framework.output.ConsoleColors;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.entity.RunsafeEntity;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.material.RunsafeMaterial;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.server.potion.RunsafePotionEffect;
import no.runsafe.framework.timer.IScheduler;
import no.runsafe.worldguardbridge.WorldGuardInterface;
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
public class Core implements IConfigurationChanged, IPluginEnabled{

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

    private ArrayList<RunsafePlayer> ingamePlayers = new ArrayList<RunsafePlayer>();
    private ArrayList<String> ingamePlayersNames = new ArrayList<String>();
    private int winAmount = 30;
    private int minPlayers = 2;
    private RunsafePlayer leader = null;

    private ArrayList<String> regions;
    private ArrayList<SimpleArea> areas;
    private int currRegion;
    private int nextRegion;


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

        ArrayList<RunsafePlayer> players = waitingRoom.getPlayers(GameMode.SURVIVAL);
        if(players.size() < this.minPlayers){
            this.resetWaittime();
            sendMessage(players, String.format(Constants.MSG_NOT_ENOUGH_PLAYERS, players.size(), this.minPlayers));
            return;
        }
        this.gamestarted = true;
        currRegion = nextRegion;
        winAmount = (int) ((players.size() / 2) + players.size() * 3.5);
        this.ingamePlayers = players;
        for(RunsafePlayer player : ingamePlayers){
            ingamePlayersNames.add(player.getName());

        }

        teleportIntoGame(this.ingamePlayers);
        sendMessage(ingamePlayers, String.format("Map: &f%s", regions.get(currRegion)));
        sendMessage(ingamePlayers, String.format(Constants.MSG_START_MESSAGE, winAmount));

    }

    public void end(String endMessage){
        for(RunsafePlayer player: areas.get(currRegion).getPlayers(GameMode.SURVIVAL)){
            teleportIntoWaitRoom(player);
            player.getInventory().clear();
            if(endMessage != null) player.sendColouredMessage(Constants.MSG_COLOR + endMessage);
        }

        List<RunsafeEntity> entities = server.getWorld(worldName).getEntities();
        for(RunsafeEntity entity : entities)
            if(areas.get(currRegion).pointInArea(entity.getLocation())) entity.remove();

        nextRegion = Util.getRandom(0, regions.size());
        gamestarted = false;
        ingamePlayers.clear();
        ingamePlayersNames.clear();
        resetWaittime();
        resetRuntime();
        leader = null;

    }

    public ArrayList<RunsafePlayer> getIngamePlayers(){
        return ingamePlayers;
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

        this.countdownToStart = config.getConfigValueAsInt("waittime");
        this.countdownToEnd = config.getConfigValueAsInt("runtime");

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

    private void sendMessage(ArrayList<RunsafePlayer> players, String msg){
         for(RunsafePlayer player : players)  player.sendColouredMessage(msg);
    }
    
    public void tick(){

        if(enabled) {
            if (!this.gamestarted) {
                for(SimpleArea combatArea: areas)
                    this.teleportIntoWaitRoom(combatArea.getPlayers(), GameMode.SURVIVAL);

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
                    sendMessage(areas.get(currRegion).getPlayers(), String.format(Constants.MSG_NEW_LEADER, leader.getPrettyName(), amountHeads(leader)));

                }

                if (countdownToEnd % 300 == 0) {
                    sendMessage(areas.get(currRegion).getPlayers(), String.format(Constants.MSG_TIME_REMAINING, (countdownToEnd / 300) * 5, "minutes"));
                } else {

                    ArrayList<RunsafePlayer> sendTo = areas.get(currRegion).getPlayers();

                    switch (countdownToEnd) {
                        case 180:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 3, "minutes"));
                            break;
                        case 120:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 2, "minutes"));
                            break;
                        case 60:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 1, "minute"));
                            break;
                        case 30:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 30, "seconds"));
                            break;
                        case 20:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
                            break;
                        case 10:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 10, "seconds"));
                            break;
                        case 5:
                            sendMessage(sendTo, String.format(Constants.MSG_TIME_REMAINING, 5, "seconds"));
                            break;

                    }
                }
                this.countdownToEnd--;
                if (countdownToEnd <= 0) {

                    winner(pickWinner());
                    return;

                }

                if (ingamePlayers.size() == 1) winner(ingamePlayers.get(0));

                for(int i = 0; i < areas.size(); i++) {
                    for (RunsafePlayer player : areas.get(i).getPlayers()) {

                        if (player.getGameMode() == GameMode.CREATIVE) continue;

                        if (!ingamePlayersNames.contains(player.getName())) teleportIntoWaitRoom(player);
                    }
                }
            }
        }
        else for(SimpleArea combatArea: areas)
            this.teleportIntoWaitRoom(combatArea.getPlayers(), GameMode.SURVIVAL);
    }

    public RunsafePlayer pickWinner() {
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

    public void equip(RunsafePlayer player){
        equipmentManager.equip(player);
    }

    public void stop(RunsafePlayer executor) {
        if(gamestarted){
            winner(pickWinner());
            sendMessage(waitingRoom.getPlayers(), String.format(Constants.MSG_GAME_STOPPED, executor.getPrettyName()));
        }

    }

    public int amountHeads(RunsafePlayer player){
        return Util.amountMaterial(player, new RunsafeMaterial(Material.SKULL_ITEM));
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

    public void setWaitRoomPos(boolean first, RunsafeLocation location) {

        if(first) waitingRoom.setFirstPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        else waitingRoom.setSecondPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

    }

    public void setWaitRoomSpawn(RunsafeLocation location) {
        this.waitingRoomSpawn = location;

    }

    public Object getAmountNeeded() {
        return winAmount;
    }

    @Override
    public void OnPluginEnabled() {

        this.loadAreas();
        console.writeColoured((enabled) ? ConsoleColors.GREEN + "Enabled": ConsoleColors.RED + "Disabled");
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

    public void addRegion(String region){
        if(!regions.contains(region)){
            regions.add(region);
            loadAreas();
        }
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
}
