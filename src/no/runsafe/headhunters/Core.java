package no.runsafe.headhunters;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.ConsoleColors;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.event.player.RunsafePlayerDeathEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.framework.timer.IScheduler;
import org.bukkit.GameMode;

import java.util.ArrayList;
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

    private int timer = -1;

    private ArrayList<RunsafePlayer> ingamePlayers = new ArrayList<RunsafePlayer>();
    private ArrayList<String> ingamePlayersNames = new ArrayList<String>();



    public Core(IConfiguration config, IOutput console, IScheduler scheduler, RunsafeServer server) {

        this.config = config;
        this.console = console;
        this.scheduler = scheduler;
        this.server = server;
        this.rand = new Random(System.currentTimeMillis());


        this.gamestarted = false;

        this.worldName = config.getConfigValueAsString("world");
        if(this.worldName == null){
            worldName = "world";
        }


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

            if(time == 0) time = 1;

            timer = time;

            return Constants.MSG_COLOR + "Game will start in " + time + " seconds";
        }

        return "Game already started";
    }

    public void start(){

        this.ingamePlayers = this.waitingRoom.getPlayers(GameMode.SURVIVAL);
        for(RunsafePlayer player : ingamePlayers) ingamePlayersNames.add(player.getName());

        teleportIntoGame(this.ingamePlayers);
        this.gamestarted = true;

    }

    public void end(String endMessage){
        for(RunsafePlayer player: this.combatArea.getPlayers()){
            teleportIntoWaitRoom(player);
            player.getInventory().clear();
            player.sendColouredMessage(Constants.MSG_COLOR + endMessage);
        }
        gamestarted = false;
        ingamePlayers.clear();
        ingamePlayersNames.clear();

    }

    public String showIngame(){

        String out = "";

        for(RunsafePlayer player: ingamePlayers){
            out = out + player.getName() + " ";
        }

        return out;

    }

    public void teleportIntoWaitRoom(ArrayList<RunsafePlayer> players){
        for(RunsafePlayer player : players){
            this.teleportIntoWaitRoom(player);
        }
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
        return null;

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

    public void debugTeleportAll(){
        ingamePlayers = (ArrayList<RunsafePlayer>) server.getOnlinePlayers();
        if(ingamePlayers == null) console.writeColoured(ConsoleColors.RED + "Couldnt get players!!" + ConsoleColors.RESET);
        else teleportIntoGame(ingamePlayers);

    }

    @Override
    public void OnConfigurationChanged(IConfiguration configuration) {

        this.config = configuration;

        this.enabled = config.getConfigValueAsBoolean("enabled");

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

        this.timer = config.getConfigValueAsInt("timer");

    }
    
    public void tick(){

        console.fine("tick. Timer:" + timer);

        if(enabled)
            if(!this.gamestarted){
                this.teleportIntoWaitRoom(this.combatArea.getPlayers(), GameMode.SURVIVAL);
                timer--;
                if(timer == 0){
                    start();
                    timer = -1;
                }
            }else{
                for(RunsafePlayer player : combatArea.getPlayers()){

                    if(player.getGameMode() == GameMode.CREATIVE) continue;

                    if(!ingamePlayersNames.contains(player.getName())){
                        teleportIntoWaitRoom(player);

                    }else{

                    }

                }
            }
        else this.teleportIntoWaitRoom(this.combatArea.getPlayers(), GameMode.SURVIVAL);



        
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



    }

    public RunsafeLocation playerRespawn(RunsafePlayer player) {
        return (ingamePlayersNames.contains(player.getName()) ? safeLocation() : null);
    }
}
