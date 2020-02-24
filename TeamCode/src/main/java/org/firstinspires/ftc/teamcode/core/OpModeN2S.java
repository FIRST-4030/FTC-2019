package org.firstinspires.ftc.teamcode.core;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Robot;

import java.util.ArrayList;

public class OpModeN2S extends OpMode implements OpModeEvents {
    protected Robot R;

    private OpModeDebug debug = null;
    private final ArrayList<OpModeEvents> children;
    private boolean initDone = false;

    protected OpModeN2S() {
        children = new ArrayList<>();
    }

    /**
     * Add a child that will receive OpModeEvents callbacks
     *
     * @param child The OpModeEvents handler to call
     */
    public void add(OpModeEvents child) {
        children.add(child);

        // Init is only called once by upstream
        // If this child missed that event, fake it to let the child initialize
        if (initDone) {
            invoke(child, "init");
        }
    }

    /**
     * Set the debug handler, which runs first and can disable other loops
     *
     * @param child The OpModeDebug handler to call
     */
    public void debug(OpModeDebug child) {
        debug = child;
    }

    /**
     * Called from the FTC OpMode manager
     */
    @Override
    public void init() {
        initDone = true;

        // Ensure we have a valid Robot
        R = Robot.start(this);

        // Put us into bulk-update mode for faster RevHub comms
        R.revhub.mode(LynxModule.BulkCachingMode.AUTO);

        early();
        late("init");
    }

    /**
     * Called from the FTC OpMode manager
     */
    @Override
    public void init_loop() {
        early();
        late("init_loop");
    }

    /**
     * Called from the FTC OpMode manager
     */
    @Override
    public void start() {
        early();
        late("start");

        // Clear telemetry at match start
        telemetry.clear();
    }

    /**
     * Called from the FTC OpMode manager
     */
    @Override
    public void loop() {
        early();
        late("loop");
    }

    /**
     * Called from the FTC OpMode manager
     */
    @Override
    public void stop() {
        early();
        late("stop");
        R.stop();
    }

    /**
     * Operations that should occur early in every OpModeEvent
     */
    private void early() {
        // Invalidate the RevHub cache
        R.revhub.clear();
        // Poll for Globals updates
        R.G.poll();
        // Read buttons
        R.B.update();
    }

    /**
     * Operations that should occur late in every OpModeEvent
     *
     * @param method The method to be called in children and debug handlers
     */
    private void late(String method) {
        // Debug, if configured
        boolean run = true;
        if (debug != null) {
            invoke(debug, method);
            // Allow debug to disable loop and init_loop for children
            if (method.equalsIgnoreCase("loop")
                    || method.equalsIgnoreCase("init_loop")) {
                run = debug.runChildren();

            }
        }

        // Run children, if allowed
        if (run) {
            for (OpModeEvents e : children) {
                invoke(e, method);
            }
        }

        // Push telemetry
        telemetry.update();
    }

    /**
     * Call the specified method of the provided class with no parameters
     *
     * @param cls    Class containing the method
     * @param method Name of the method
     */
    private void invoke(OpModeEvents cls, String method) {
        switch (method) {
            case "init":
                cls.init();
                break;
            case "init_loop":
                cls.init_loop();
                break;
            case "start":
                cls.start();
                break;
            case "loop":
                cls.loop();
                break;
            case "stop":
                cls.stop();
                break;
            default:
                Robot.err(this, "Invalid method " +
                        cls.getClass().getSimpleName() + "::" + method);
        }

        /*
        This is the Java.reflection way to call a method by name.
        It works, but it folds all exceptions into InvocationTargetException,
        which makes debugging more complicated. So we use a case statement.

        try {
            Method m = cls.getClass().getMethod(method);
            m.invoke(cls);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            Robot.err(this, "Invalid method " +
                    cls.getClass().getSimpleName() + "::" + method);
        } catch (InvocationTargetException e) {
            Robot.err(this, "Runtime exception in: " +
                    cls.getClass().getSimpleName() + "::" + method +
                    ": " + e.getLocalizedMessage());
        }
         */
    }
}
