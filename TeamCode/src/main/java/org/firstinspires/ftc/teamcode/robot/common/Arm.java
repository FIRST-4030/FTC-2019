package org.firstinspires.ftc.teamcode.robot.common;

import org.firstinspires.ftc.teamcode.actuators.ServoFTC;
import org.firstinspires.ftc.teamcode.robot.Robot;

public class Arm implements CommonTask {
    private static boolean DEBUG = false;
    // when true, moveToPos will skip calculating and moving servos to the same position it was just at
    private static boolean SKIP_SAME_VALUES = true;

    private final Robot robot;
    private final ServoFTC lower;
    private final ServoFTC upper;

    // Servo positions when the servo is at 90 degrees
    private final float LOWER_MIDPOINT = 0.3f;
    private final float UPPER_MIDPOINT = 0.75f;

    // previous arm position
    private float prevX = 0.0f;
    private float prevY = 0.0f;

     // The arm has two segments, measured from joint to joint
     // The lower segment is from the base to the middle joint
     // The upper segment is from the base to the claw joint
     // lengths are measured in inches
    private static final double LOWER_LENGTH = 5.5;
    private static final double UPPER_LENGTH = 6.75;

    public Arm(Robot robot) {
        this.robot = robot;

        // Set which servos control the arm
        this.lower = robot.orange;
        this.upper = robot.black;
    }

    /**
     * Moves arm to specified position
     * x and y are when looking at the arm from the side on a 2D plane
     * @param x x position in inches (extension)
     * @param y y position in inches (elevation)
     * @throws OutOfRangeException if the position is theoretically impossible
     */
    public void moveToPos(float x, float y) throws OutOfRangeException {
        // trig OwO

        // first, find the hypotenuse (from the base to the desired pos)
        double hypot = Math.sqrt(x * x + y * y);

        // check if the triangle is valid (this only checks if the position is possible in theory,
        // the arm might still not be able to physically reach the position)
        if (LOWER_LENGTH + UPPER_LENGTH <= hypot ||
            LOWER_LENGTH + hypot <= UPPER_LENGTH ||
            UPPER_LENGTH + hypot <= LOWER_LENGTH)
            throw new OutOfRangeException("Arm position is impossible");

        // check previous arm position
        if (x == prevX && y == prevY && SKIP_SAME_VALUES) {
            // i don't know if doing all the trig every loop cycle slows things down,
            // so if the last position is the same as the desired position, we don't do
            // the math or move the arm
            return;
        } else {
            prevX = x;
            prevY = y;
        }

        // use the law of cosines to find the internal angles of the triangle made by the arm
        // segments and the hypotenuse
        double hypot_lower = Math.acos((LOWER_LENGTH * LOWER_LENGTH + hypot * hypot - UPPER_LENGTH * UPPER_LENGTH) / (2 * LOWER_LENGTH * hypot));
        double lower_upper = Math.acos((UPPER_LENGTH * UPPER_LENGTH + LOWER_LENGTH * LOWER_LENGTH - hypot * hypot) / (2 * UPPER_LENGTH * LOWER_LENGTH));

        // calculate the angles the servos need to be at
        // the lower servo is the angle between the hypotenuse and the x-axis plus hypot_lower
        float lowerPos = (float) (Math.acos(x / hypot) + hypot_lower);
        // the upper servo is the difference between lower_upper and the third angle of the right
        // triangle made by the x-axis and the lower segment
        float upperPos = (float) (lower_upper - (Math.PI - (Math.PI/2 + lowerPos)));

        // Make the angles usable servo positions
        lowerPos = (float) (lowerPos / Math.PI) + (LOWER_MIDPOINT - 0.5f);
        upperPos = (float) (upperPos / Math.PI) + (UPPER_MIDPOINT - 0.5f);

        if (DEBUG) {
            robot.telemetry.addData("lower raw: ", ((Math.asin(y / hypot) + hypot_lower)));
            robot.telemetry.addData("upper raw: ", (lower_upper - (Math.PI - (Math.PI/2 + lowerPos))));
            robot.telemetry.addData("lower pos: ", lowerPos);
            robot.telemetry.addData("upper pos: ", upperPos);
        }

        // set servos to those positions
        lower.setPosition(lowerPos);
        upper.setPosition(upperPos);
    }

    // In case the arm can't reach the desired position
    public class OutOfRangeException extends Exception {
        public OutOfRangeException(String message) {
            super(message);
        }
    }

}
