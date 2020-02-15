package org.firstinspires.ftc.teamcode.actuators;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.storage.Config;

public class Motor implements Actuators {
    public static final DcMotor.RunMode MODE_DEFAULT = DcMotor.RunMode.RUN_USING_ENCODER;
    public static final DcMotor.RunMode MODE_PID = DcMotor.RunMode.RUN_TO_POSITION;

    public final String name;
    private DcMotor motor = null;
    private double power = 0.0d;

    // TODO: Add debug output
    public static boolean DEBUG = false;

    public Motor(String name, Config config) {
        this.name = name;
        // TODO: Load our config from the structure
        boolean reverse = false;
        boolean brake = true;
        DcMotor.RunMode mode = MODE_DEFAULT;
        init(name, reverse, brake, mode);
    }

    public Motor(String name, boolean reverse, boolean brake, DcMotor.RunMode mode) {
        this.name = name;
        init(name, reverse, brake, mode);
    }

    // Can't be a constructor since we need to decode the config first
    private void init(String name, boolean reverse, boolean brake, DcMotor.RunMode mode) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() +
                    ": No name provided");
        }
        try {
            motor = Robot.R.opmode.hardwareMap.dcMotor.get(name);
            brake(brake);
            reverse(reverse);
            mode(mode);
            resetEncoder();
        } catch (Exception e) {
            motor = null;
            Robot.R.log(this.getClass().getSimpleName() +
                    ": Unable to initialize: " + name);
        }

        // Register with the global actuators list
        Robot.R.register(this);
    }

    public boolean ready() {
        return (motor != null);
    }

    public boolean reverse() {
        if (!ready()) {
            return false;
        }
        return (motor.getDirection() == DcMotor.Direction.REVERSE);
    }

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

    public boolean brake() {
        if (!ready()) {
            return false;
        }
        return (motor.getZeroPowerBehavior() == DcMotor.ZeroPowerBehavior.BRAKE);
    }

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

    private DcMotor.RunMode mode() {
        if (!ready()) {
            return MODE_DEFAULT;
        }
        return motor.getMode();
    }

    private void mode(DcMotor.RunMode mode) {
        if (!ready()) {
            return;
        }
        if (mode == null) {
            mode = MODE_DEFAULT;
        }
        motor.setMode(mode);
    }

    public boolean pid() {
        return (mode() == MODE_PID);
    }

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

        // Set the target to "here" if we're in PID mode
        target(encoder());
    }

    public int target() {
        if (!ready() || !pid()) {
            return 0;
        }
        return motor.getTargetPosition();
    }

    public void target(int target) {
        if (!ready() || !pid()) {
            return;
        }
        motor.setTargetPosition(target);
    }

    public boolean busy() {
        if (!ready()) {
            return false;
        }
        return motor.isBusy();
    }

    public double power() {
        return power;
    }

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

    public void stop() {
        power(0.0d);
    }

    public int encoder() {
        if (!ready()) {
            return 0;
        }
        return motor.getCurrentPosition();
    }

    public void resetEncoder() {
        stop();
        DcMotor.RunMode mode = mode();
        mode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mode(mode);
    }
}
