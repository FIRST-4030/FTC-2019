package org.firstinspires.ftc.teamcode.opmodes.debug;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.core.OpModeEvents;
import org.firstinspires.ftc.teamcode.core.RobotUtils;

abstract public class OpMode_Debug implements OpModeEvents {
    protected final Robot R;
    protected final Telemetry T;
    protected final ButtonHandler B;

    public OpMode_Debug() {
        R = Robot.R;
        T = RobotUtils.O.telemetry;
        B = new ButtonHandler();
    }

    // I imagine this will be a more complete handler, perhaps like OpeModeN2S
    // But to start with let's just provide some common objects in a local namespace
}