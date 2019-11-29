package org.firstinspires.ftc.teamcode.actuators;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.utils.Round;

public class ServoNG implements Actuator {
    public final ServoNG_Params p;
    private Servo servo;
    private boolean enabled = true;
    private boolean teleop = false;
    private boolean enforce = true;

    public ServoNG(ServoNG_Params p) {
        this.p = p;
        try {
            servo = p.robot.opMode.hardwareMap.servo.get(p.name);
            // FTC FORWARD/REVERSE mode
            if (p.reverse) {
                servo.setDirection(Servo.Direction.REVERSE);
            }
            // Init position, if set
            if (p.hasPreset(p.INIT_NAME)) {
                position(p.getPreset(p.INIT_NAME));
            }
        } catch (Exception e) {
            servo = null;
            p.robot.log(this, "Servo not available: " + p.name);
            this.enabled = false;
        }
    }

    public void loop(double input) {
        if (!teleop) {
            return;
        }
        // TODO: Rate-limited analog input
        relative(input);
    }

    public void setTeleop(boolean enable) {
        teleop = enable;
    }

    public void limits(boolean enforce) {
        this.enforce = enforce;
    }

    public double getPosition() {
        if (!ready()) {
            return 0.5d;
        }
        return servo.getPosition();
    }

    private void limitError(double pos) {
        p.robot.log(this, p.name + ": Cannot reach position: " +
                Round.truncate(pos) + "/" +
                Round.truncate(p.posToAngle(pos)) + "/" +
                Round.truncate(p.posToOutput(pos)));
    }

    public void position(double pos) {
        if (!ready()) {
            return;
        }

        // Optionally enforce limits
        if (enforce) {
            if (pos < p.min) {
                limitError(pos);
                pos = p.min;
            }
            if (pos > p.max) {
                limitError(pos);
                pos = p.max;
            }
        }

        // Automatically re-enable PWM if needed
        if (servo.getController().getPwmStatus() != ServoController.PwmStatus.ENABLED) {
            servo.getController().pwmEnable();
        }

        // Actuate
        servo.setPosition(pos);
    }

    public void angle(double angle) {
        position(p.angleToPos(angle));
    }

    public void scale(double scale) {
        position(p.scaleToPos(scale));
    }

    public void relative(double delta) {
        position(getPosition() + delta);
    }

    public void relativeScale(double delta) {
        relative(delta * (p.max - p.min));
    }

    public void relativeAngle(double delta) {
        relative(delta / p.range);
    }

    public void relativeOutput(double delta) {
        relativeAngle(delta * p.outputScale);
    }

    @Override
    public void debug_init(RobotNG robot) {
        // TODO: Install key bindings
    }

    @Override
    public void debug_loop() {
        Telemetry t = p.robot.opMode.telemetry;

        String mode = p.name + "\t";
        mode += enabled() ? "Enabled\t" : "Disabled\t";
        mode += ready() ? "Ready\t" : "Not Ready\t";
        t.addData("Mode", mode);

        double pos = servo.getPosition();
        String position = Round.truncate(pos) + "\t" +
                Round.truncate(p.posToAngle(pos)) + "\t" +
                Round.truncate(p.posToOutput(pos));
        t.addData("Position", position);

        // TODO: Other parameters
        t.update();
    }

    @Override
    public void debug_destroy() {
        // TODO: Remove key bindings
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void setEnable(boolean enable) {
        if (enable) {
            relative(0.0d);
        } else {
            stop();
        }
        this.enabled = enable;
    }

    @Override
    public boolean ready() {
        return (enabled() && servo != null);
    }

    @Override
    public void stop() {
        if (!ready()) {
            return;
        }

        // I think this disables more than one channel, but it's what we can access
        // I don't expect stop() will be called in normal use, but we may need to scrap this
        servo.getController().pwmDisable();
    }
}
