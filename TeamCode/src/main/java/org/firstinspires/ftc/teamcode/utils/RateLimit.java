package org.firstinspires.ftc.teamcode.utils;

import org.firstinspires.ftc.teamcode.Robot;

public class RateLimit {
    private long lastTime;
    private double rate = 0.25d;

    /**
     * Get the current maximum rate
     *
     * @return Maximum rate, in s^-1
     */
    public double rate() {
        return rate;
    }

    /**
     * Set the maximum allowed rate
     *
     * @param maxRate Maximum rate, in s^-1
     */
    public void rate(double maxRate) {
        if (maxRate == 0) {
            Robot.err(this, "Rate must not be zero");
        }
        rate = maxRate;
    }

    /**
     * Limit the given input to the maximum rate
     * This calculates the rate since the last call to limit()
     * If you don't call it every loop be sure to handle the buildup
     *
     * @param delta Unlimited input value
     * @return Rate-limited output
     */
    public double limit(double delta) {
        long now = System.currentTimeMillis();
        // Change in time in decimal seconds
        double deltaT = (now - lastTime) / 1000.0d;
        double last;
        if (Math.abs(delta) > rate / deltaT) {
            last = Math.copySign(rate * deltaT, delta);
        } else {
            last = delta;
        }
        lastTime = now;
        return last;
    }
}
