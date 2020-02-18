package org.firstinspires.ftc.teamcode.utils;

public class Round {
    /**
     * Round to the specified number of digits after the decimal
     *
     * @param val    Value to be rounded
     * @param digits Number of post-decimal digits to retain
     * @return val rounded to digits places
     */
    public static double truncate(double val, int digits) {
        double factor = Math.pow(10, digits);
        return (double) ((int) (val * factor)) / factor;
    }

    /**
     * Float wrapper for truncate(double)
     *
     * @param val    Value to be rounded
     * @param digits Number of post-decimal digits to retain
     * @return val rounded to digits places
     */
    public static float truncate(float val, int digits) {
        return (float) truncate((double) val, digits);
    }

    /**
     * Alias for truncate(val, 2)
     *
     * @param val Value to be rounded
     * @return val rounded to digits places
     */
    public static double r(double val) {
        return truncate(val, 2);
    }

    /**
     * Float wrapper for r(double)
     *
     * @param val Value to be rounded
     * @return val rounded to digits places
     */
    public static float r(float val) {
        return (float) r((double) val);
    }
}
