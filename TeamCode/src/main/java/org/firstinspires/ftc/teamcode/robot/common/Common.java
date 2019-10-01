package org.firstinspires.ftc.teamcode.robot.common;

import org.firstinspires.ftc.teamcode.robot.Robot;

/*
 * These are robot-specific helper methods
 * They exist to encourage code re-use across classes
 *
 * They are a reasonable template for future robots, but are unlikely to work as-is
 */
public class Common {

    // Runtime
    private final Robot robot;
    public final Arm arm;
    public final Drive drive;

    public Common(Robot r) {
        if (r == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null robot");
        }
        this.robot = r;

        this.arm = new Arm(robot);
        this.drive = new Drive(robot);
    }
}
