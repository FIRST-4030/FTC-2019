package org.firstinspires.ftc.teamcode.debug;

import org.firstinspires.ftc.teamcode.RobotNG;

public interface ModeProvider {
    void debug_start(RobotNG robot);
    void debug_next();
    void debug_prev();
    void debug_mode_next();
    void debug_mode_prev();
    void debug_loop();
    void debug_end();
}
