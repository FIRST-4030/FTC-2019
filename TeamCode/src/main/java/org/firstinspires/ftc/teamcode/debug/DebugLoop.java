package org.firstinspires.ftc.teamcode.debug;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;

import java.util.List;
import java.util.ListIterator;

public class DebugLoop {
    private final RobotNG robot;
    private final ButtonHandler b;
    private final List<ModeProvider> providers;
    private ListIterator<ModeProvider> it;
    private ModeProvider p;

    public DebugLoop(RobotNG robot, List<ModeProvider> providers) {
        if (robot == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + ": " +
                    "Robot not available");
        }
        if (providers == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + ": " +
                    "DebugProviders not available");
        }
        this.robot = robot;
        this.providers = providers;
        it = providers.listIterator();

        b = new ButtonHandler(robot);
        registerButtons();
    }

    private void registerButtons() {
        Gamepad pad = robot.opMode.gamepad1;
        b.register("DEBUG", pad, PAD_BUTTON.guide, BUTTON_TYPE.TOGGLE);
        b.register("STOP", pad, PAD_BUTTON.start);
        b.register("NEXT", pad, PAD_BUTTON.dpad_right);
        b.register("PREV", pad, PAD_BUTTON.dpad_left);
        b.register("MODE_NEXT", pad, PAD_BUTTON.dpad_up);
        b.register("MODE_PREV", pad, PAD_BUTTON.dpad_down);
    }

    public void loop() {
        b.update();

        // End debug cleanly
        if (RobotNG.DEBUG && !b.get("DEBUG")) {
            if (p != null) {
                p.debug_end();
            }
        }

        // Update the global debug state
        RobotNG.DEBUG = b.get("DEBUG");

        // Bail if we're not debugging
        if (!RobotNG.DEBUG) {
            return;
        }

        // Ensure we have an active debug provider at all times
        if (p == null) {
            p = providers.get(0);
            p.debug_start(robot);
        }
        p.debug_loop();

        // E-stop
        if (b.get("STOP")) {
            robot.stop();
        }

        // Mode (switch among device types)
        if (b.get("MODE_NEXT") || b.get("MODE_PREV")) {
            if (b.get("MODE_NEXT")) {
                if (!it.hasNext()) {
                    it = providers.listIterator();
                }
            } else {
                if (!it.hasPrevious()) {
                    it = providers.listIterator(providers.size() - 1);
                }
            }
            if (p != null) {
                p.debug_end();
            }
            p = it.next();
            p.debug_start(robot);
        }

        // Device (switch among devices of a given type)
        if (b.get("NEXT")) {
            p.debug_next();
        } else if (b.get("PREV")) {
            p.debug_prev();
        }
    }
}
