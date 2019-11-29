package org.firstinspires.ftc.teamcode.debug;

import org.firstinspires.ftc.teamcode.RobotNG;

public interface Debug {
    void debug_init(RobotNG robot);
    void debug_loop();
    void debug_destroy();
}
