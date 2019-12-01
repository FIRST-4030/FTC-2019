package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.debug.DebugLoop;
import org.firstinspires.ftc.teamcode.debug.ModeProvider;
import org.firstinspires.ftc.teamcode.defaults.Defaults;
import org.firstinspires.ftc.teamcode.robot.config.BOTS;
import org.firstinspires.ftc.teamcode.config.Hardware;

import java.util.List;

public class RobotNG {
    // Static self reference
    public static RobotNG R = null;
    public static boolean DEBUG = false;

    // Global public bits
    public final BOTS bot;
    public final ButtonHandler buttons;
    public final OpMode opMode;
    public final Hardware hardware;
    public final Defaults defaults;

    // Internals
    private final DebugLoop debug;
    private List<ModeProvider> debugProviders;

    public RobotNG(OpMode opMode) {
        // Init the mandatory global elements
        this.opMode = opMode;
        this.bot = detectBot();
        this.defaults = new Defaults(this);
        this.buttons = new ButtonHandler(this);

        // Config lives in Hardware
        this.hardware = new Hardware(this);

        // Lock the defaults to stop registrations and trigger a load/save cycle
        defaults.lock();

        // Init our gooey debug center
        debugProviders.add(hardware);
        this.debug = new DebugLoop(this, debugProviders);

        // Provide a static reference once we're up and running
        R = this;
    }

    public void init() {
        opModeCommon();
    }

    public void init_loop() {
        opModeCommon();
    }

    public void start() {
        opModeCommon();
    }

    public void loop() {
        opModeCommon();
    }

    public void stop() {
        opModeCommon();
        hardware.stop();
    }

    private void opModeCommon() {
        buttons.update();
        debug.loop();
    }

    // Logging shortcut
    public void log(String msg) {
        opMode.telemetry.log().add(msg);
    }

    // Logging shortcut
    public void log(Object obj, String msg) {
        log(obj.getClass().getSimpleName() + ": " + msg);
    }

    public BOTS detectBot() {
        BOTS bot = BOTS.NONE;
        /*
        //TODO: Bot detection based on RevHub serial or name
        for (BOTS b : BOTS.values()) {
            try {
                opMode.hardwareMap.get(b.serial());
                bot = b;
                break;
            } catch (Exception e) {
                // Do nothing
            }
        }
         */
        return bot;
    }
}
