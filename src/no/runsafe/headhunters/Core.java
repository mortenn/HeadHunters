package no.runsafe.headhunters;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.timer.IScheduler;

/**
 * Created with IntelliJ IDEA.
 * User: Naxanria
 * Date: 19-4-13
 * Time: 16:04
 */
public class Core {

    private String worldName;
    private SimpleArea waitingRoom;
    private SimpleArea combatArea;
    private boolean gamestarted;
    private boolean enabled = true;

    private IConfiguration config;
    private IOutput console;
    private IScheduler scheduler;


    public Core(IConfiguration config, IOutput console, IScheduler scheduler) {


        System.out.println("-----------------------------------------------------");

        this.config = config;
        this.console = console;
        this.scheduler = scheduler;


        this.gamestarted = false;

        this.enabled = config.getConfigValueAsBoolean("enabled");


        System.out.println("-----------------------------------------------------");

        console.write("loaded");

    }


}
