package org.firstinspires.ftc.teamcode.actuators;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.storage.config.ConfigDevice;

public class Motor implements Actuators {
    public static final DcMotor.RunMode MODE_OPEN = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    public static final DcMotor.RunMode MODE_ENCODER = DcMotor.RunMode.RUN_USING_ENCODER;
    public static final DcMotor.RunMode MODE_PID = DcMotor.RunMode.RUN_TO_POSITION;
    public static final DcMotor.RunMode MODE_DEFAULT = MODE_ENCODER;

    public final String name; // Name for the JSON config and hardware map
    private DcMotor motor = null; // The underlying FTC device
    private double power = 0.0d; // Track power locally -- the FTC device doesn't

    /**
     * Motors. They spin and stop and have encoders.
     *
     * @param name Motor name from the hardware map
     */
    public Motor(String name) {
        this.name = name;

        // Load the config for this device based on the class ane device names
        ConfigDevice d = Robot.R.config.device(this.getClass().getSimpleName(), name);

        // If you want a parameter to be optional in the JSON, apply a default and
        // check to see if it exists before using it
        DcMotor.RunMode mode = MODE_DEFAULT;
        if (d.exists("mode")) {
            String m = d.s("mode");
            if (m.equalsIgnoreCase("ENCODER")) {
                mode = MODE_ENCODER;
            } else if (m.equalsIgnoreCase("PID")) {
                mode = MODE_PID;
            } else if (m.equalsIgnoreCase("OPEN")) {
                mode = MODE_OPEN;
            } else {
                Robot.R.err(this.getClass().getSimpleName() +
                        ": Invalid mode: " + m);
            }
        }

        // It's always safe to use config parameters directly, even if they are missing
        init(name, d.b("reverse"), d.b("brake"), mode);
    }

    /**
     * Motors. They spin and stop and have encoders.
     *
     * @param name    Motor name from the hardware map
     * @param reverse True if the motor direction should be reversed
     * @param brake   True if the motor should brake instead of float when commanded to 0 power
     * @param mode    One of the native DcMotor.RunModes for RevHub configuration, can be null
     */
    public Motor(String name, boolean reverse, boolean brake, DcMotor.RunMode mode) {
        this.name = name;
        init(name, reverse, brake, mode);
    }

    /**
     * Internal constructor. This needs to be a separate method so the actual constructors can do
     * other work before running this, but as you can see it takes all the same args.
     *
     * @param name    Motor name from the hardware map
     * @param reverse True if the motor direction should be reversed
     * @param brake   True if the motor should brake instead of float when commanded to 0 power
     * @param mode    One of the native DcMotor.RunModes for RevHub configuration, can be null
     */
    private void init(String name, boolean reverse, boolean brake, DcMotor.RunMode mode) {
        if (name == null || name.isEmpty()) {
            Robot.R.err(this.getClass().getSimpleName() + ": No name provided");
            name = this.toString();
        }
        try {
            motor = Robot.R.opmode.hardwareMap.dcMotor.get(name);
            brake(brake);
            reverse(reverse);
            mode(mode);
            resetEncoder();
        } catch (Exception e) {
            motor = null;
            Robot.R.err(this.getClass().getSimpleName() +
                    ": Unable to initialize: " + name);
        }

        // Register with the global actuators list
        Robot.R.register(this);
    }

    /*
     * Setter/getter methods in this class are overloaded to use the same name
     *
     * Calling a method with no arguments will return the current value
     * Calling a method with arguments will update the value
     */


    /**
     * Get the motor initialization state
     *
     * @return True if the motor exists and has been initialized
     */
    public boolean ready() {
        return (motor != null);
    }

    /**
     * Get the current reverse flag
     *
     * @return True if the motor direction is inverted
     */
    public boolean reverse() {
        if (!ready()) {
            return false;
        }
        return (motor.getDirection() == DcMotor.Direction.REVERSE);
    }

    /**
     * Set the reverse flag
     *
     * @param reverse True to invert the motor direction
     */
    public void reverse(boolean reverse) {
        if (!ready()) {
            return;
        }

        DcMotor.Direction dir;
        if (reverse) {
            dir = DcMotor.Direction.REVERSE;
        } else {
            dir = DcMotor.Direction.FORWARD;
        }
        motor.setDirection(dir);
    }

    /**
     * Get the current brake setting
     *
     * @return True if braking is enabled
     */
    public boolean brake() {
        if (!ready()) {
            return false;
        }
        return (motor.getZeroPowerBehavior() == DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Set the braking mode for the motor, to control the motor behavior when power(0.0) is called
     * When enabled, braking will dump kinetic energy into the battery, to stop almost immediately
     * When disabled the motor will be allowed to glide when set to 0 power
     *
     * @param brake True to brake, false to glide
     */
    public void brake(boolean brake) {
        if (!ready()) {
            return;
        }

        DcMotor.ZeroPowerBehavior zero;
        if (brake) {
            zero = DcMotor.ZeroPowerBehavior.BRAKE;
        } else {
            zero = DcMotor.ZeroPowerBehavior.FLOAT;
        }
        motor.setZeroPowerBehavior(zero);
    }

    /**
     * Get the current mode of the underlying FTC motor
     *
     * @return One of the native DcMotor.RunModes
     */
    private DcMotor.RunMode mode() {
        if (!ready()) {
            return MODE_DEFAULT;
        }
        return motor.getMode();
    }

    /**
     * Set the mode of underlying FTC motor
     *
     * @param mode One of the native DcMotor.RunModes. Can be null
     */
    private void mode(DcMotor.RunMode mode) {
        if (!ready()) {
            return;
        }
        if (mode == null) {
            mode = MODE_DEFAULT;
        }
        motor.setMode(mode);
    }

    /**
     * Get the current PID setting
     *
     * @return True if position-PID is active
     */
    public boolean pid() {
        return (mode() == MODE_PID);
    }

    /**
     * Enable or disable position-PID mode (REV internal)
     *
     * @param pid True to enable PID mode, false to use to the default mode
     */
    public void pid(boolean pid) {
        if (!ready()) {
            return;
        }

        // Actuators if we're changing modes
        if (pid != pid()) {
            stop();
        }

        DcMotor.RunMode mode;
        if (pid) {
            mode = MODE_PID;
        } else {
            mode = MODE_DEFAULT;
        }
        mode(mode);

        // Set the target to "here" if we're entering PID mode
        target(encoder());
    }

    /**
     * Get the current target for position PID. Returns 0 if PID is not enabled.
     *
     * @return The current position-PID target, in ticks
     */
    public int target() {
        if (!ready() || !pid()) {
            return 0;
        }
        return motor.getTargetPosition();
    }

    /**
     * Set the target for position PID mode. Does nothing if PID is not enabled.
     *
     * @param target The desired position-PID target, in ticks
     */
    public void target(int target) {
        if (!ready() || !pid()) {
            return;
        }
        motor.setTargetPosition(target);
    }

    /**
     * Is the motor in PID mode and seeking a target?
     *
     * @return True if the motor is still seeking a target
     */
    public boolean busy() {
        if (!ready() || !pid()) {
            return false;
        }
        return motor.isBusy();
    }

    /**
     * Get the most recent power setting requested for this motor
     *
     * @return Power setting, -1.0 to 1.0
     */
    public double power() {
        return power;
    }

    /**
     * Set the motor power (or speed, depending on default mode)
     *
     * @param power The desired motor speed or power, -1.0 to 1.0
     */
    public void power(double power) {
        if (!ready()) {
            return;
        }

        // Limit power to the allowed range, just in case
        power = Range.clip(power, -1.0d, 1.0d);
        // Store the last power setting for future reference
        this.power = power;
        motor.setPower(power);
    }

    /**
     * Stop spinning
     */
    public void stop() {
        power(0.0d);
    }

    /**
     * Gets the current encoder tick count. Consider using the polled value from Globals
     *
     * @return Current encoder reading.
     */
    public int encoder() {
        if (!ready()) {
            return 0;
        }
        return motor.getCurrentPosition();
    }

    /**
     * Set the encoder count to 0
     * This method temporarily disrupts the mode and speed of the motor, but restores both
     */
    public void resetEncoder() {
        DcMotor.RunMode mode = mode();
        double power = power();
        stop();
        mode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mode(mode);
        power(power);
    }
}
