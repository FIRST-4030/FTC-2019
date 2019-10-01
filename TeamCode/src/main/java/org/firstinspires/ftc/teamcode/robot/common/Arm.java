package org.firstinspires.ftc.teamcode.robot.common;

import org.firstinspires.ftc.teamcode.actuators.ServoFTC;
import org.firstinspires.ftc.teamcode.robot.Robot;

public class Arm implements CommonTask {
    private static boolean DEBUG = false;

    private final Robot robot;
    private final ServoFTC lower;
    private final float LOWER_MIDPOINT = 0.3f;
    private final ServoFTC upper;
    private final float UPPER_MIDPOINT = 0.75f;

    /*
     * The arm has two segments, measured from joint to joint
     * The lower segment is from the base to the middle joint
     * The upper segment is from the base to the claw joint
     * lengths are measured in inches
     */
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
        /* finds the angles that will make a valid triangle out of the arms, the
         * desired position, and the base. if they don't exist, throw the OutOfRangeException.
         */

        // first, find the hypotenuse
        double hypot = Math.sqrt(x * x + y * y);

        // check if the triangle is valid (this only checks if the position is possible in theory)
        if (LOWER_LENGTH + UPPER_LENGTH <= hypot || LOWER_LENGTH + hypot <= UPPER_LENGTH || UPPER_LENGTH + hypot <= LOWER_LENGTH)
            throw new OutOfRangeException("Arm position is impossible");

        // use the law of cosines to find angles
        double hypot_lower = Math.acos((LOWER_LENGTH * LOWER_LENGTH + hypot * hypot - UPPER_LENGTH * UPPER_LENGTH) / (2 * LOWER_LENGTH * hypot));
        double lower_upper = Math.acos((UPPER_LENGTH * UPPER_LENGTH + LOWER_LENGTH * LOWER_LENGTH - hypot * hypot) / (2 * UPPER_LENGTH * LOWER_LENGTH));

        // now that we have the angles we want, we can move the appropriate servos to the angles

        /* the lower position is the angle between the hypotenuse and 0 plus the angle between the
         * lower arm segment and the hypotenuse
         */
        float lowerPos = (float) (Math.acos(x / hypot) + hypot_lower);

        /* the upper position is the angle between the lower segment and the upper segment minus
         * the angle between the lower segment and 90
         */
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
        lower.setPosition(lowerPos);
        upper.setPosition(upperPos);
    }

    // In case the arm can't reach the desired position
    public class OutOfRangeException extends Exception {
        public OutOfRangeException(String message) {
            super(message);
        }
    }

    /**
     * Rotates the arm at a specified velocity
     * @param v velocity to turn at
     */
    public void rotate(float v) {

    }

}
