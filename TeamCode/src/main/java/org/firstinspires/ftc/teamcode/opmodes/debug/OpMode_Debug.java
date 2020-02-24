package org.firstinspires.ftc.teamcode.opmodes.debug;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.core.OpModeEvents;

abstract public class OpMode_Debug implements OpModeEvents {
    protected final Robot R;
    protected final ButtonHandler B;

    public OpMode_Debug() {
        R = Robot.R;
        B = new ButtonHandler();
    }

    // I imagine this will be a more complete handler, perhaps like OpeModeN2S
    // But to start with let's just provide some common objects in a local namespace
}