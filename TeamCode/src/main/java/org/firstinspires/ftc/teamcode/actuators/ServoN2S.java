package org.firstinspires.ftc.teamcode.actuators;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.storage.config.ConfigDevice;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;
import org.firstinspires.ftc.teamcode.storage.globals.GlobalsPoll;
import org.firstinspires.ftc.teamcode.utils.RateLimit;
import org.firstinspires.ftc.teamcode.utils.Round;

import java.util.HashMap;
import java.util.Map;

public class ServoN2S implements Actuators, GlobalsPoll {
    private static final String PRESET_INIT = "_INIT";
    private static final double DEFAULT_MIN = 0.0d;
    private static final double DEFAULT_MAX = 1.0d;
    private static final double DEFAULT_OFFSET = 0.0d;
    private static final boolean DEFAULT_REVERSE = false;
    private static final double DEFAULT_RATE = 0.25d;

    private Servo servo;
    private boolean stopped = false;
    private boolean limits = true;

    public String name;
    private Map<String, Double> presets;
    private double offset = DEFAULT_OFFSET;
    private double min = DEFAULT_MIN;
    private double max = DEFAULT_MAX;
    private RateLimit rate;

    public ServoN2S(String name) {
        // Load the config for this device based on the class and device names
        ConfigDevice d = Robot.R.C.device(this.getClass().getSimpleName(), name);

        // TODO: Decode config for presets -- right now it's always empty
        init(name,
                d.bOptional("reverse", DEFAULT_REVERSE),
                d.dOptional("offset", DEFAULT_OFFSET),
                d.dOptional("min", DEFAULT_MIN),
                d.dOptional("max", DEFAULT_MAX),
                d.dOptional("rate", DEFAULT_RATE),
                new HashMap<String, Double>());
    }

    public ServoN2S(String name, boolean reverse, double offset,
                    double min, double max, double rate,
                    Map<String, Double> presets) {
        init(name, reverse, offset, min, max, rate, presets);
    }

    /**
     * Internal constructor
     *
     * @param name    Servo name from the hardware map
     * @param reverse True if the servo direction should be reversed
     * @param offset  Offset for the raw servo position
     * @param min     Minimum allowed raw servo position (if limits are enforced)
     * @param max     Maximum allowed raw servo position (if limits are enforced)
     * @param rate    Maximum rate of change for the teleop mode
     * @param presets Map of preset names and position
     */
    private void init(String name, boolean reverse, double offset,
                      double min, double max, double rate,
                      Map<String, Double> presets) {
        if (name == null || name.isEmpty()) {
            Robot.err(this, "No name provided");
            name = this.toString();
        }
        this.name = name;

        // Find the device
        try {
            servo = Robot.O.hardwareMap.servo.get(name);
        } catch (Exception e) {
            servo = null;
            Robot.err(this, "Unable to initalize: " + name);
        }

        // Apply config
        reverse(reverse);
        offset(offset);
        minmax(min, max);
        rate(rate);
        presets(presets);

        // Move to the init position, if set
        if (this.presets.containsKey(PRESET_INIT)) {
            this.preset(PRESET_INIT);
        }

        // Register with the Actuators list and Globals
        Robot.R.register(this);
        Robot.R.G.register(this);
    }

    /**
     * Is the servo currently reversed?
     *
     * @return True if reversed
     */
    public boolean reverse() {
        if (!ready()) {
            return false;
        }
        return (servo.getDirection() == Servo.Direction.REVERSE);
    }

    /**
     * Set or clear the reverse mode for this servo
     *
     * @param reverse True to reverse
     */
    public void reverse(boolean reverse) {
        if (!ready()) {
            return;
        }
        Servo.Direction mode = Servo.Direction.FORWARD;
        if (reverse) {
            mode = Servo.Direction.REVERSE;
        }
        servo.setDirection(mode);
    }

    /**
     * Are limits currently enforced?
     *
     * @return True if limits are enabled
     */
    public boolean limits() {
        return limits;
    }

    /**
     * Enable or disable enforcement of min-max limits
     * This is intended for special modes that allow tuning or debugging
     *
     * @param enforce True to limits limits, false to ignore them
     */
    public void limits(boolean enforce) {
        this.limits = enforce;
        // Force an update when limits are enabled, to be sure we're inside them
        if (enforce) {
            delta(0.0d);
        }
    }

    /**
     * Get the current min/max limits for the servo
     *
     * @return double[0] = min, double[1] = max
     */
    public double[] minmax() {
        return new double[]{min, max};
    }

    /**
     * Set the min/max limits for the servo
     *
     * @param min Minimum allowed raw servo position (if limits are enforced)
     * @param max Maximum allowed raw servo position (if limits are enforced)
     */
    public void minmax(double min, double max) {
        if (min < 0.0d || min >= max || max > 1.0d) {
            Robot.warn(this, "Ignoring invalid min/max: "
                    + Round.r(min) + "/" + Round.r(max));
            return;
        }
        this.min = min;
        this.max = max;
        // Force an update to ensure the new limits are enforced (if active)
        delta(0.0d);
    }

    /**
     * Get the current teleop rate limit (in raw servo output units per second)
     *
     * @return Current teleop rate limit
     */
    public double rate() {
        return rate.rate();
    }

    /**
     * Set the teleop rate limit
     *
     * @param newRate New rate limit, in raw servo output units per second)
     */
    public void rate(double newRate) {
        if (rate == null) {
            rate = new RateLimit();
        }
        rate.rate(newRate);
    }

    /**
     * Get the current offset used in position() calculations
     *
     * @return Current offset
     */
    public double offset() {
        return offset;
    }

    /**
     * Update the offset used in position() calculations
     *
     * @param offset New offset
     */
    public void offset(double offset) {
        if (offset <= -1.0d || offset >= 1.0d) {
            Robot.warn(this, "Ignoring invalid offset: " + Round.r(offset));
            offset = this.offset;
        }
        this.offset = offset;
        // Force an update to recalculate with the new offset
        delta(0.0d);
    }

    /**
     * Return the current list of presets
     *
     * @return All named presets
     */
    public Map<String, Double> presets() {
        return new HashMap<>(presets);
    }

    /**
     * Set the list of presets
     *
     * @param presets All named presets
     */
    public void presets(Map<String, Double> presets) {
        if (presets == null) {
            Robot.warn(this, "Ignoring invalid presets");
            return;
        }
        this.presets = new HashMap<>(presets);
    }

    /**
     * Get the current servo output position, not adjusted for the offset
     * Consider using the polled value with:
     * Robot.R.G.d("SERVO_RAW_" + name);
     *
     * @return Output position
     */
    public double raw() {
        if (!ready()) {
            return 0.0d;
        }
        return servo.getPosition();
    }

    /**
     * Actually move the servo
     *
     * @param pos The commanded position
     */
    private void raw(double pos) {
        if (!ready()) {
            return;
        }

        // Enforce device limits, optionally enforce user limits
        double p = pos;
        if (limits) {
            p = Math.min(p, max);
            p = Math.max(p, min);
        }
        p = Math.min(p, DEFAULT_MAX);
        p = Math.max(p, DEFAULT_MIN);
        if (p != pos) {
            Robot.warn(this, name + ": Cannot reach position "
                    + Round.r(pos) + "/" + Round.r(p));
        }
        pos = p;

        // Automatically re-enable PWM if needed
        if (stopped) {
            servo.getController().pwmEnable();
            stopped = false;
        }

        // Move
        servo.setPosition(pos);
    }

    /**
     * Helper to convert raw values to position values
     *
     * @param raw Raw position
     * @return Offset position
     */
    public double rawToOffset(double raw) {
        return raw - offset;
    }

    /**
     * Get the current servo output position
     * Consider using the polled value with:
     * Robot.R.G.d("SERVO_" + name);
     *
     * @return Output position
     */
    public double position() {
        if (!ready()) {
            return 0.0d;
        }
        return rawToOffset(servo.getPosition());
    }

    /**
     * Set the servo position directly
     *
     * @param pos The commanded position
     */
    public void position(double pos) {
        raw(pos + offset);
    }

    /**
     * Helper to convert raw values to scale values
     *
     * @param raw Raw position
     * @return Scale position
     */
    public double rawToScale(double raw) {
        return (raw - min) / (max - min);
    }

    /**
     * Get the current scaled servo output position
     * Consider using the polled value with:
     * Robot.R.G.d("SERVO_SCALE_" + name);
     *
     * @return Scaled output position
     */
    public double scale() {
        return rawToScale(raw());
    }

    /**
     * Set the servo position by percent, relative to the min-max interval
     *
     * @param scale Scaled output position
     */
    public void scale(double scale) {
        position((scale * (max - min)) + min);
    }

    /**
     * Set the servo position by relative change
     *
     * @param delta Change in position
     */
    public void delta(double delta) {
        position(raw() + delta);
    }

    /**
     * Set the servo position by scaled relative change
     *
     * @param scaleDelta Change in scaled position
     */
    public void deltaScale(double scaleDelta) {
        delta((scaleDelta * (max - min)));
    }

    /**
     * Set the position to a preset by name
     *
     * @param name Preset name
     */
    public void preset(String name) {
        Double d = presets.get(name);
        if (d == null) {
            Robot.warn(this, "Invalid preset: " + name);
            return;
        }
        scale(d);
    }

    /**
     * Teleop control of the servo
     * This applies the rate limit and should be called every loop
     * (when teleop is in use) to ensure accurate limits
     *
     * @param delta Relative change of servo position
     */
    public void teleop(double delta) {
        raw(raw() + rate.limit(delta));
    }

    /**
     * Is the servo initialized and ready to move?
     *
     * @return True if ready, otherwise false
     */
    public boolean ready() {
        return (servo != null);
    }

    /**
     * Disable all motion for this servo (i.e. disable PWM output)
     */
    public void stop() {
        if (!ready()) {
            return;
        }

        // I think this disables more than one channel, but it's what we can access
        // This is automatically reset when a new position is commanded
        servo.getController().pwmDisable();
        stopped = true;
    }

    /**
     * Callback for the Globals polling mechanism
     *
     * @param g The Globals calling for this poll
     */
    public void gPoll(Globals g) {
        double raw = raw();
        g.set("SERVO_RAW_" + name, raw);
        g.set("SERVO_" + name, rawToOffset(raw));
        g.set("SERVO_SCALE_" + name, rawToScale(raw));
    }
}
