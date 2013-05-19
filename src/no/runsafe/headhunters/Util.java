package no.runsafe.headhunters;

import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.item.RunsafeItemStack;
import no.runsafe.framework.server.material.RunsafeMaterial;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 7-5-13
 * Time: 3:16
 */
public class Util {

    static Random random = new Random(System.currentTimeMillis());


    public static int getRandom(int min, int max){

        if(min > max){
            int t = min;
            min = max;
            max = t;
        }

        if(min == max){
            return min;
        }

        return random.nextInt(max - min) + min;
    }

    public static int getRandom(int min, int max, int not){

        if(min > max){
            int t = min;
            min = max;
            max = t;
        }

        if(min == max){
            return min;
        }

        int r = random.nextInt(max - min) + min;
        if(r == not) r = getRandom(min, max, not);

        return r;
    }

    public static boolean actPercentage(int percentage){
        return (getRandom(0, 100) < percentage);
    }

    public static double flatDistance(double x, double y){
        if(x < y){
            double t = x;
            x = y;
            y = t;
        }
        return (x-y);

    }

    public static double distance(RunsafeLocation loc1, RunsafeLocation loc2){

        return Math.sqrt(
                Math.pow(flatDistance(loc1.getX(), loc2.getX()), 2) +
                        Math.pow(flatDistance(loc1.getY(), loc2.getY()), 2) +
                        Math.pow(flatDistance(loc1.getZ(), loc2.getZ()), 2)
        );
    }



    public static String prettyLocation(RunsafeLocation location){
        return String.format("&a%d %d %d", location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static int amountMaterial(RunsafePlayer player, RunsafeMaterial search){
        int amount = 0;
        for(RunsafeItemStack content : (ArrayList<RunsafeItemStack>) player.getInventory().getContents())
            if(content.getItemId() == search.getMaterialId()) amount += content.getAmount();
        return amount;
    }

    public static String fillZeros(int i){
        String s =  i + "";
        if(i < 10) s = "0" + s;
        return s;

    }

}
