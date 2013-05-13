package no.runsafe.headhunters;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.GlobalRegionManager;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;
import org.bukkit.GameMode;

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

    public SimpleArea(double x1, double y1, double z1, double x2, double y2, double z2, RunsafeWorld world){

        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z1 = z1;
        this.z2 = z2;
        this.world = world;

    }

    public SimpleArea(String region, RunsafeWorld world){
        this.worldGuard = RunsafeServer.Instance.getPlugin("WorldGuard");
        if(worldGuard == null){
            System.out.println("Please Enable/install WorldGuard");
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

        }

    }

    public SimpleArea(ProtectedRegion reg, RunsafeWorld world){

        this.x1 = reg.getMaximumPoint().getBlockX();
        this.y1 = reg.getMaximumPoint().getBlockY();
        this.z1 = reg.getMaximumPoint().getBlockZ();
        this.x2 = reg.getMinimumPoint().getBlockX();
        this.y2 = reg.getMinimumPoint().getBlockY();
        this.z2 = reg.getMinimumPoint().getBlockZ();
        this.world = world;



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

        double x1 = this.x1;
        double x2 = this.x2;
        double y1 = this.y1;
        double y2 = this.y2;
        double z1 = this.z1;
        double z2 = this.z2;


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




        return (x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2);

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

    public RunsafeLocation safeLocation(){
        int x,y,z;

        int ysmall = (int) Math.min(this.getY1(), this.getY2());
        int ylarge = (int) Math.max(this.getY1(), this.getY2());
        int tries = 350;
        while(tries > 0){


            x = Util.getRandom((int) this.getX1() + 1, (int) this.getX2() - 1);
            z = Util.getRandom((int) this.getZ1() + 1, (int) this.getZ2() - 1);

            for(y = ysmall; y < ylarge - 1; y++){

                if((new RunsafeLocation(world,(double) x, (double) y, (double) z).getBlock().getBlockState().getMaterialID() == 0)
                        &&
                        (new RunsafeLocation(world,(double) x, (double) y + 1, (double) z).getBlock().getBlockState().getMaterialID() == 0)){
                    return new RunsafeLocation(world, (double) x, (double) y, (double) z);
                }


            }
            tries--;
        }
        return null;

    }

}
