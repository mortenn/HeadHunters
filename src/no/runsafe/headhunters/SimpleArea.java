package no.runsafe.headhunters;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.framework.minecraft.block.RunsafeSign;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:17
 */
public class SimpleArea {

    private WorldGuardPlugin worldGuard;
    private double x1;
    private double x2;
    private double y1;
    private double y2;
    private double z1;
    private double z2;

    private RunsafeWorld world;

    private String regionName;

    public SimpleArea(double x1, double y1, double z1, double x2, double y2, double z2, RunsafeWorld world){

        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
        this.world = world;

        sortCoords();

        this.regionName = "Waitroom";

    }

    public SimpleArea(String region, RunsafeWorld world){
        this.regionName = region;
        this.worldGuard = RunsafeServer.Instance.getPlugin("WorldGuard");
        if(worldGuard == null){
            System.out.println("Please CommandEnable/install WorldGuard");
            this.x1 = 0;
            this.x2 = 0;
            this.y1 = 0;
            this.y2 = 0;
            this.z1 = 0;
            this.z2 = 0;
            this.world = world;
        }else{

            GlobalRegionManager manager = worldGuard.getGlobalRegionManager();

            ProtectedRegion reg = manager.get(world.getRaw()).getRegion(region);
            this.x1 = reg.getMaximumPoint().getBlockX();
            this.y1 = reg.getMaximumPoint().getBlockY();
            this.z1 = reg.getMaximumPoint().getBlockZ();
            this.x2 = reg.getMinimumPoint().getBlockX();
            this.y2 = reg.getMinimumPoint().getBlockY();
            this.z2 = reg.getMinimumPoint().getBlockZ();
            this.world = world;

            sortCoords();
        }

    }

    public SimpleArea(ProtectedRegion reg, RunsafeWorld world){

        this.regionName = reg.getId();
        this.x1 = reg.getMaximumPoint().getBlockX();
        this.y1 = reg.getMaximumPoint().getBlockY();
        this.z1 = reg.getMaximumPoint().getBlockZ();
        this.x2 = reg.getMinimumPoint().getBlockX();
        this.y2 = reg.getMinimumPoint().getBlockY();
        this.z2 = reg.getMinimumPoint().getBlockZ();
        this.world = world;

        sortCoords();


    }

    public void setFirstPos(double x, double y, double z){
        this.x1 = x;
        this.y1 = y;
        this.z1 = z;

    }

    public void setSecondPos(double x, double y, double z){
        this.x2 = x;
        this.y2 = y;
        this.z2 = z;

    }

    public boolean pointInArea(RunsafeLocation location){
        return pointInArea(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean pointInArea(double x, double y, double z){

        return (x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2);

    }

    public void sortCoords(){

        double temp;
        if(x2 < x1){
            temp = x2;
            x2 = x1;
            x1 = temp;
        }
        if(y2 < y1){
            temp = y2;
            y2 = y1;
            y1 = temp;
        }
        if(z2 < z1){
            temp = z2;
            z2 = z1;
            z1 = temp;
        }

    }

    public ArrayList<RunsafePlayer> getPlayers(){

        ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();

        for(RunsafePlayer player :this.world.getPlayers())
           if(pointInArea(player.getLocation())) players.add(player);

        return players;

    }

    public ArrayList<RunsafePlayer> getPlayers(GameMode mode){

        ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();

        for(RunsafePlayer player :this.world.getPlayers())
            if(pointInArea(player.getLocation()) && player.getGameMode() == mode) players.add(player);

        return players;

    }

    public ArrayList<String> getPlayersNames() {

        ArrayList<String> playerNames = new ArrayList<String>();
        for(RunsafePlayer player :this.world.getPlayers())
            if(pointInArea(player.getLocation())) playerNames.add(player.getName());

        return playerNames;
    }

    public ArrayList<String> getPlayersNames(GameMode mode) {

        ArrayList<String> playerNames = new ArrayList<String>();
        for(RunsafePlayer player :this.world.getPlayers())
            if(pointInArea(player.getLocation()) && player.getGameMode() == mode) playerNames.add(player.getName());

        return playerNames;
    }

    public double getX1(){
        return this.x1;
    }

    public double getX2(){
        return this.x2;
    }

    public double getY1(){
        return this.y1;
    }

    public double getY2(){
        return this.y2;
    }

    public double getZ1(){
        return this.z1;
    }

    public double getZ2(){
        return this.z2;
    }

    @Override
    public String toString(){
        return "x1=" + x1 + "x2=" + x2 + "y1=" + y1 + "y2=" + y2 + "z1=" + z1 + "z2=" + z2 + "world="+world.getName();
    }

    public void teleportToArea(RunsafePlayer player){
        player.teleport(new RunsafeLocation(world, this.getX1(), this.getY2() + 15, this.getZ1()));
    }

    public RunsafeLocation safeLocation(){
        int x,y,z;

        int ysmall = (int) Math.min(this.getY1(), this.getY2());
        int ylarge = (int) Math.max(this.getY1(), this.getY2());
        int tries = 20000;
        boolean foundGround;
        while(tries > 0){
            foundGround = false;

            x = Util.getRandom((int) this.getX1() + 3, (int) this.getX2() - 3);
            z = Util.getRandom((int) this.getZ1() + 3, (int) this.getZ2() - 3);

            for(y = ysmall; y < ylarge - 1; y++){

                RunsafeLocation thisLoc = new RunsafeLocation(world,(double) x, (double) y, (double) z);
                if(thisLoc.getBlock().getBlockState() instanceof RunsafeSign){
                    //System.out.println("Found a sign!" + Util.prettyLocation(thisLoc));
                    RunsafeSign sign = (RunsafeSign) thisLoc.getBlock().getBlockState();
                    if(sign.getLine(0).equalsIgnoreCase("skip")) continue;
                }
                if(thisLoc.getBlock().getBlockState().getMaterialID() != Material.AIR.getId()){
                    foundGround = true;
                    //System.out.println("~found ground!~");
                }else if(foundGround && (thisLoc.getBlock().getBlockState().getMaterialID() == Material.AIR.getId())
                        &&
                        (new RunsafeLocation(world,(double) x, (double) y + 1, (double) z).getBlock().getBlockState().getMaterialID() == Material.AIR.getId())){
                    return new RunsafeLocation(world, (double) x, (double) y, (double) z);
                }


            }
            tries--;
        }
        return null;

    }

    public String getRegionName() {
        return this.regionName;
    }
}