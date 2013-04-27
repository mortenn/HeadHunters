package no.runsafe.headhunters;

import no.runsafe.framework.server.RunsafeLocation;
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
        return pointInArea(location.getX(), location.getY(), location.getZ());
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
}
