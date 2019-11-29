package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.debug.Debug;
import org.firstinspires.ftc.teamcode.utils.Disable;

public interface Actuator extends Disable, Debug {
    Actuator_Params p = null;
    void stop();
}
