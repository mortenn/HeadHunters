package no.runsafe.headhunters;

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

    public static boolean actPercentage(int percentage){
        return (getRandom(0, 100) < percentage);
    }

}
