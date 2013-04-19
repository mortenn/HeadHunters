package no.runsafe.headhunters;

import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeWorld;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:17
 */
public class SimpleArea {

    private double x1, x2, y1, y2, z1, z2;
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

    public boolean pointInArea(RunsafeLocation location){
        return pointInArea(location.getX(), location.getY(), location.getZ());
    }

    public boolean pointInArea(double x, double y, double z){

        return (x > x1 && x < x2 && y > y1 && y < y2 && z > z1 && z < z2);

    }

    public ArrayList<RunsafePlayer> getPlayers(){

        ArrayList<RunsafePlayer> players = new ArrayList<RunsafePlayer>();

        for(RunsafePlayer player :this.world.getPlayers())
           if(pointInArea(player.getLocation())) players.add(player);

        return players;

    }


}
