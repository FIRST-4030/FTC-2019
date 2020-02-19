package org.firstinspires.ftc.teamcode.utils;

public class Heading {
    private static final int FULL_CIRCLE = 360;

    /**
     * Normalize the provided heading into the space 0 <= heading < 360
     *
     * @param heading Any heading
     * @return Normalized heading
     */
    public static int normalize(int heading) {
        return ((heading % FULL_CIRCLE) + FULL_CIRCLE) % FULL_CIRCLE;
    }

    /**
     * Float implementation of normalize(int)
     *
     * @param heading Any heading
     * @return Normalized heading
     */
    public static float normalize(float heading) {
        while (heading >= (float) FULL_CIRCLE) {
            heading -= (float) FULL_CIRCLE;
        }
        while (heading < 0.0f) {
            heading += (float) FULL_CIRCLE;
        }
        return heading;
    }
}
