package no.runsafe.headhunters;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.block.RunsafeBlock;
import no.runsafe.framework.server.enchantment.RunsafeEnchantment;
import no.runsafe.framework.server.entity.RunsafeEntity;
import no.runsafe.framework.server.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerPickupItemEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerQuitEvent;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.server.potion.RunsafePotionEffect;
import no.runsafe.framework.timer.IScheduler;

import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
    private Random rand;

    private int countdownToStart = -1;
    private int countdownToEnd = -1;
    private HashMap<String, Integer> headsCount;

    private ArrayList<RunsafePlayer> ingamePlayers = new ArrayList<RunsafePlayer>();
    private ArrayList<String> ingamePlayersNames = new ArrayList<String>();
    private int winAmount = 30;
    private int minPlayers = 2;
    private RunsafePlayer leader = null;


    public Core(IConfiguration config, IOutput console, IScheduler scheduler, RunsafeServer server) {

        this.config = config;
        this.console = console;
        this.scheduler = scheduler;
        this.server = server;
        this.rand = new Random(System.currentTimeMillis());


        this.gamestarted = false;

        headsCount = new HashMap<String, Integer>();

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

    public boolean setCombatAreaFirstPos(RunsafePlayer player) {

        RunsafeLocation location = player.getLocation();

        if(player.getWorld().getName().equalsIgnoreCase(this.worldName)){
            this.config.setConfigValue("combatarea.x1",location.getX());
            this.config.setConfigValue("combatarea.y1",location.getY());
            this.config.setConfigValue("combatarea.z1",location.getZ());

            this.combatArea.setFirstPos(location.getX(), location.getY(), location.getZ());

            this.config.save();
            return true;
        }

        return false;


    }

    public boolean setCombatAreaSecondPos(RunsafePlayer player) {

        RunsafeLocation location = player.getLocation();

        if(player.getWorld().getName().equalsIgnoreCase(this.worldName)){
            this.config.setConfigValue("combatarea.x2",location.getX());
            this.config.setConfigValue("combatarea.y2",location.getY());
            this.config.setConfigValue("combatarea.z2",location.getZ());

            this.combatArea.setSecondPos(location.getX(), location.getY(), location.getZ());

            this.config.save();
            return true;
        }

        return false;


    }

    public String start(Integer time) {

        if(!this.enabled) return Constants.MSG_DISABLED;

        if(!gamestarted){

            if(time <= 0) time = 1;

            countdownToStart = time;

            return Constants.MSG_COLOR + "Game will start in " + time + " seconds";
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
        winAmount = (int) 2.5 * players.size() + 5;
        this.ingamePlayers = players;
        for(RunsafePlayer player : ingamePlayers){
            ingamePlayersNames.add(player.getName());
            headsCount.put(player.getName(), 0);
        }

        teleportIntoGame(this.ingamePlayers);
        sendMessage(ingamePlayers, String.format(Constants.MSG_START_MESSAGE, winAmount));
        this.gamestarted = true;

    }

    public void end(String endMessage){
        for(RunsafePlayer player: this.combatArea.getPlayers()){
            teleportIntoWaitRoom(player);
            player.getInventory().clear();
            if(endMessage != null) player.sendColouredMessage(Constants.MSG_COLOR + endMessage);
        }

        List<RunsafeEntity> entities = server.getWorld(worldName).getEntities();
        for(RunsafeEntity entity : entities)
            if(combatArea.pointInArea(entity.getLocation())) entity.remove();

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
            out = out + player.getName() + " ";
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
            teleportIntoGame(player);
        }
    }

    public int getRandom(int min, int max){

        if(min > max){
            int t = min;
            min = max;
            max = t;
        }

        if(min == max){
            return 0;
        }

        return this.rand.nextInt(max - min) + min;
    }


    public void teleportIntoGame(RunsafePlayer player){
        player.getInventory().clear();
        this.equip(player);
        player.removeBuffs();


        player.addPotionEffect(new RunsafePotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 2 )));

        player.teleport(safeLocation());
    }

    public RunsafeLocation safeLocation(){
        int x,y,z;

        int ysmall = (int) Math.min(combatArea.getY1(), combatArea.getY2());
        int ylarge = (int) Math.max(combatArea.getY1(), combatArea.getY2());
        int tries = 350;
        while(tries > 0){


            x = getRandom((int) combatArea.getX1(),(int) combatArea.getX2());
            z = getRandom((int) combatArea.getZ1(),(int) combatArea.getZ2());
            RunsafeWorld world =  server.getWorld(this.worldName);

            for(y = ysmall; y < ylarge; y++){

                if((new RunsafeLocation(world,(double) x, (double) y, (double) z).getBlock().getBlockState().getMaterialID() == 0)
                        &&
                        (new RunsafeLocation(world,(double) x, (double) y + 1, (double) z).getBlock().getBlockState().getMaterialID() == 0)){
                   return new RunsafeLocation(server.getWorld(this.worldName), (double) x, (double) y, (double) z);
                }


            }
            tries--;
        }
        return this.waitingRoomSpawn;

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
                    winner(leader);
                    return;
                }
                if (l != leader && amount > 0) {
                    leader = l;
                    sendMessage(combatArea.getPlayers(), String.format(Constants.MSG_NEW_LEADER, leader.getPrettyName(), amountHeads(leader)));

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

            if(!this.ingamePlayersNames.contains(executor.getName())){
                this.teleportIntoWaitRoom(executor);
                executor.setGameMode(GameMode.SURVIVAL);
                return null;
            }else{
                return Constants.ERROR_COLOR + "You are already in game!";
            }
        }

        return Constants.MSG_COLOR + "headhunters is not enabled";

    }

    public void playerDeath(RunsafePlayerDeathEvent event) {

        RunsafePlayer player = event.getEntity();

        if(this.ingamePlayersNames.contains(player.getName())){

            event.setDroppedXP(0);
            int amount = amountHeads(event.getEntity());
            List<RunsafeItemStack> items = event.getDrops();
            items.clear();
            if(getRandom(0, 100) > 90) items.add(randomItem());
            event.setDrops(items);
            player.getWorld().dropItem(
                    player.getEyeLocation(),
                    new RunsafeItemStack(Material.SKULL_ITEM.getId(), amount + 1, (short)3)
            );
        }


    }

    private RunsafeItemStack randomItem() {

        ArrayList<RunsafeItemStack> rItems = new ArrayList<RunsafeItemStack>();
        rItems.add(new RunsafeItemStack(Material.GOLDEN_APPLE.getId()));
        rItems.add(new RunsafeItemStack(Material.SLIME_BALL.getId()));
        rItems.add(new RunsafeItemStack(Material.SUGAR.getId()));


        return rItems.get(getRandom(0, rItems.size()));
    }

    public void equip(RunsafePlayer player){

        player.getInventory().setChestplate(new RunsafeItemStack(Material.CHAINMAIL_CHESTPLATE.getId()));
        player.getInventory().setLeggings(new RunsafeItemStack(Material.CHAINMAIL_LEGGINGS.getId()));
        player.getInventory().setBoots(new RunsafeItemStack(Material.IRON_BOOTS.getId()));
        player.getInventory().setHelmet(new RunsafeItemStack(Material.GOLD_HELMET.getId()));


        RunsafeItemStack bow = new RunsafeItemStack(Material.BOW.getId());
        bow.addEnchantment(new RunsafeEnchantment(Enchantment.ARROW_INFINITE), 1);

        player.getInventory().addItems(
                new RunsafeItemStack(Material.IRON_SWORD.getId()),
                bow,
                new RunsafeItemStack(Material.ARROW.getId()),
                new RunsafeItemStack(Material.COOKED_BEEF.getId(), 5, (short) 0)
        );

    }

    public RunsafeLocation playerRespawn(RunsafePlayer player) {
        if(ingamePlayersNames.contains(player.getName())){
            equip(player);
            teleportIntoGame(player);
            return safeLocation();
        }
        if(combatArea.pointInArea(player.getLocation())) return waitingRoomSpawn;
        return  null;
    }

    public void stop(RunsafePlayer executor) {
        if(gamestarted){
            winner(pickWinner());
            sendMessage(waitingRoom.getPlayers(), String.format(Constants.MSG_GAMESTOPPED, executor.getPrettyName()));
        }

    }

    public int amountHeads(RunsafePlayer player){
        int amount = 0;
        for(RunsafeItemStack content : (ArrayList<RunsafeItemStack>) player.getInventory().getContents()) if(content.getItemId() == Material.SKULL_ITEM.getId()) amount += content.getAmount();
        return amount;
    }

    public void leave(RunsafePlayer player) {

        if(ingamePlayersNames.contains(player.getName())){
            player.getWorld().dropItem(player.getEyeLocation(), new RunsafeItemStack(Material.SKULL_ITEM.getId(), amountHeads(player), (short) 3));
            player.getInventory().clear();
            player.teleport(waitingRoomSpawn);
            ingamePlayers.remove(player);
            ingamePlayersNames.remove(player.getName());
            sendMessage(ingamePlayers, String.format(Constants.MSG_LEAVE, player.getPrettyName()));
            if(ingamePlayersNames.size() <= 1) end(null);
        }

    }

    public void disconnect(RunsafePlayerQuitEvent event) {
        leave(event.getPlayer());
    }

    public void rightClick(RunsafePlayer player, RunsafeItemStack usingItem, RunsafeBlock targetBlock) {
        //special items
        if(ingamePlayersNames.contains(player.getName()) && usingItem != null && targetBlock != null){
            boolean used = false;
            if(usingItem.getItemId() == Material.SLIME_BALL.getId()){
                RunsafePotionEffect slow = new RunsafePotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 4));
                //visual effect...
                server.getWorld(worldName).playEffect(targetBlock.getLocation(), Effect.POTION_BREAK, 2);

                ArrayList<RunsafePlayer> hitPlayers = getPlayers(targetBlock.getLocation(), 5);
                for(RunsafePlayer hitPlayer : hitPlayers)
                    if(!hitPlayer.getName().equalsIgnoreCase(player.getName())) hitPlayer.addPotionEffect(slow);
                used = true;
            }
            if(used){

                RunsafeItemStack stack = player.getItemInHand();
                stack.setAmount(stack.getAmount() - 1);

                player.getInventory().setItemInHand(stack);
            }


        }
    }

    public double distance(double x, double y){
        if(x < y){
            double t = x;
            x = y;
            y = t;
        }
        return (x-y);
    }

    public double distance(RunsafeLocation loc1, RunsafeLocation loc2){

        return Math.sqrt(
                Math.pow(distance(loc1.getX(), loc2.getX()), 2) +
                Math.pow(distance(loc1.getY(), loc2.getY()), 2) +
                Math.pow(distance(loc1.getZ(), loc2.getZ()), 2)
        );
    }

    public ArrayList<RunsafePlayer> getPlayers(RunsafeLocation loc, int radius){

        ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();
        for(RunsafePlayer player : ingamePlayers)
            if(distance(loc, player.getLocation()) < radius) players.add(player);

        return players;

    }

    public void pickUp(RunsafePlayerPickupItemEvent event) {

        console.fine(String.format("%s picked up %d", event.getPlayer().getPrettyName(), event.getItem().getItemStack().getItemId()));
        RunsafePlayer player = event.getPlayer();

        if(ingamePlayersNames.contains(player.getName())){
            RunsafeItemStack item = event.getItem().getItemStack();
            boolean used = false;
            if( item.getItemId() == Material.GOLDEN_APPLE.getId()){
                PotionEffect regen = new PotionEffect(PotionEffectType.REGENERATION, 60, 2);
                player.addPotionEffect(new RunsafePotionEffect(regen));
                used = true;
            }else if(item.getItemId() == Material.SUGAR.getId()){
                PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 120, 3);
                player.addPotionEffect(new RunsafePotionEffect(speed));
                used = true;
            }
            if(used){
                event.getItem().remove();
                event.setCancelled(true);
            }
        }


    }

    public void playerMove(RunsafePlayer player) {
        if(ingamePlayersNames.contains(player.getName()) && !ingamePlayersNames.isEmpty()){
            if(!combatArea.pointInArea(player.getLocation())){
                leave(player);
                player.sendColouredMessage(Constants.MSG_USE_LEAVE);
            }
        }

    }
}
