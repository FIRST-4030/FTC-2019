package org.firstinspires.ftc.teamcode.robot.common;

import org.firstinspires.ftc.teamcode.actuators.ServoFTC;
import org.firstinspires.ftc.teamcode.robot.Robot;

public class Arm implements CommonTask {
    private static boolean DEBUG = false;
    // when true, moveToPos will skip calculating and moving servos to the same position it was just at
    private static boolean SKIP_SAME_VALUES = false;

    private final Robot robot;
    private final ServoFTC lower;
    private final ServoFTC upper;

    // Servo positions when the servo is at 90 degrees
    private final float LOWER_MIDPOINT = 0.7f;
    private final float UPPER_MIDPOINT = 0.75f;

    // previous arm position
    private float prevX = 0.0f;
    private float prevY = 0.0f;

     // The arm has two segments, measured from joint to joint
     // The lower segment is from the base to the middle joint
     // The upper segment is from the base to the claw joint
     // lengths are measured in inches
    private final double LOWER_LENGTH = 5.5;
    private final double UPPER_LENGTH = 5.75;

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
        double B = lawOfCosines(LOWER_LENGTH, hypot, UPPER_LENGTH);
        double A = lawOfCosines(UPPER_LENGTH, LOWER_LENGTH, hypot);

        // calculate the angles the servos need to be at
        // the lower servo is the angle between the hypotenuse and the x-axis plus B
        float S1 = (float) (Math.atan(y/x) + B);
        // the upper servo is the difference between A and the third angle of the right
        // triangle made by the x-axis and the lower segment
        double M = Math.PI - (S1 + Math.PI/2);
        float S2 = (float) (A - M);

        // Make the angles usable servo positions
        S1 = (float) (S1 / Math.PI) + (LOWER_MIDPOINT - 0.5f);
        S2 = (float) (S2 / Math.PI) + (UPPER_MIDPOINT - 0.5f);

        if (DEBUG) {
            robot.telemetry.addData("aaaaaa", B);
            robot.telemetry.addData("lower raw: ", ((Math.asin(y / hypot) + B)));
            robot.telemetry.addData("upper raw: ", (A - (Math.PI - (Math.PI/2 + S1))));
            robot.telemetry.addData("lower pos: ", S1);
            robot.telemetry.addData("upper pos: ", S2);
        }

        // set servos to those positions
        lower.setPosition(S1);
        upper.setPosition(S2);
    }

    private double lawOfCosines(double a, double b, double c) {
        return Math.acos((a*a + b*b - c*c) / (2*a*b));
    }

    // In case the arm can't reach the desired position
    public class OutOfRangeException extends Exception {
        public OutOfRangeException(String message) {
            super(message);
        }
    }

}
