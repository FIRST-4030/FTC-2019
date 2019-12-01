package org.firstinspires.ftc.teamcode.actuators;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.ButtonHandler;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.utils.Round;

public class ServoNG implements Actuator {
    public final ServoNG_Params p;
    private Servo servo;
    private boolean enabled = true;
    private boolean teleop = false;
    private boolean enforce = true;

    // Debug support
    private ButtonHandler D_buttons = null;
    private boolean D_teleop = teleop;
    private boolean D_enforce = enforce;
    private Telemetry D_telemetry;
    private Gamepad D_gamepad;

    public ServoNG(ServoNG_Params p) {
        if (p == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": " +
                    "Null ServoNG_Params");
        }
        if (!p.valid()) {
            p.robot.log(this, "ServoNG_Params not valid");
        }
        this.p = p;
        try {
            servo = p.robot.opMode.hardwareMap.servo.get(p.name());
            // FTC FORWARD/REVERSE mode
            if (p.reverse) {
                servo.setDirection(Servo.Direction.REVERSE);
            }
            // Init position, if set
            if (p.hasPreset(ServoNG_Params.INIT_NAME)) {
                position(p.getPreset(ServoNG_Params.INIT_NAME));
            }
        } catch (Exception e) {
            servo = null;
            p.robot.log(this, "Servo not available: " + p.name());
            this.enabled = false;
        }
    }

    public Actuator_Params params() {
        return p;
    }

    public void setTeleop(boolean enable) {
        teleop = enable;
    }

    public void teleop(double delta) {
        if (!teleop) {
            return;
        }
        position(getPosition() + delta, true);
    }

    public void enforceLimits(boolean enforce) {
        this.enforce = enforce;
    }

    public double getPosition() {
        if (!ready()) {
            return 0.0d;
        }
        return servo.getPosition();
    }

    private void limitError(double pos) {
        p.robot.log(this, p.name() + ": Cannot reach position: " +
                Round.truncate(pos) + "\t" +
                Round.truncate(p.posToAngle(pos)) + "°\t" +
                Round.percent(p.posToScale(pos)) + "%\t" +
                Round.truncate(p.posToOutput(pos)) + "°");
    }

    public void position(double pos) {
        position(pos, false);
    }

    private void position(double pos, boolean teleopOverride) {
        if (!ready()) {
            return;
        }

        // Don't allow auto commands in teleop
        if (teleop && !teleopOverride) {
            return;
        }

        // Optionally enforce limits
        if (enforce) {
            if (pos < p.getMin()) {
                limitError(pos);
                pos = p.getMin();
            }
            if (pos > p.getMax()) {
                limitError(pos);
                pos = p.getMax();
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
        relative(delta * (p.getMinMaxRange()));
    }

    public void relativeAngle(double delta) {
        relative(delta / p.range);
    }

    public void relativeOutput(double delta) {
        relativeAngle(delta * p.outputScale);
    }

    public void preset(String name) {
        position(p.getPreset(name));
    }

    @Override
    public void debug_init(RobotNG robot) {
        D_teleop = teleop;
        D_enforce = enforce;
        D_telemetry = robot.opMode.telemetry;
        D_gamepad = robot.opMode.gamepad1;
        setTeleop(true);

        Gamepad pad = robot.opMode.gamepad1;
        D_buttons = new ButtonHandler(robot.opMode.telemetry);
        D_buttons.register("PRESET_UP", pad, PAD_BUTTON.y);
        D_buttons.register("PRESET_DOWN", pad, PAD_BUTTON.a);
        D_buttons.register("SLOW", pad, PAD_BUTTON.x, BUTTON_TYPE.TOGGLE);
        D_buttons.register("LIMITS", pad, PAD_BUTTON.b);
        D_buttons.register("MIN", pad, PAD_BUTTON.left_bumper);
        D_buttons.register("MAX", pad, PAD_BUTTON.right_bumper);
    }

    @Override
    public void debug_destroy() {
        D_buttons = null;
        setTeleop(D_teleop);
        enforceLimits(D_enforce);

        // Force a null move to get everything in sync
        relative(0.0d);
    }

    @Override
    public void debug_loop() {
        Telemetry t = D_telemetry;
        String s;

        D_buttons.update();
        teleop(D_gamepad.right_stick_x * (D_buttons.get("SLOW") ? 0.5d : 1.0d));
        if (D_buttons.get("PRESET_UP")) {
            preset(p.next());
        }
        if (D_buttons.get("PRESET_DOWN")) {
            preset(p.prev());
        }
        if (D_buttons.get("LIMITS")) {
            enforceLimits(!enforce);
        }
        if (D_buttons.get("MIN")) {
            position(p.getMin());
        }
        if (D_buttons.get("MAX")) {
            position(p.getMax());
        }

        // Name/Mode
        s = p.name() + "\t" +
                Round.truncate(p.range) + "°\t" +
                (p.reverse ? "Reverse" : "Forward");
        t.addData("Servo", s);
        s = (enabled() ? "Enabled" : "Disabled") + "\t" +
                (ready() ? "Ready" : "Not Ready") + "\t" +
                (enforce ? "Limited" : "Unlimited");
        t.addData("Mode", s);

        // Position
        double pos = servo.getPosition();
        s = Round.truncate(pos) + "\t" +
                Round.truncate(p.posToAngle(pos)) + "°";
        t.addData("Pos Raw", s);
        s = Round.percent(p.posToScale(pos)) + "%\t" +
                Round.truncate(p.posToOutput(pos)) + "°";
        t.addData("Pos Out", s);

        // Min/Max
        s = p.getMin() + " (" + p.posToAngle(p.getMin()) + "°)\t"
                + p.getMax() + " (" + p.posToAngle(p.getMax()) + "°)";
        t.addData("Min Max Raw", s);
        s = Round.percent(p.posToScale(p.getMin())) + "% " +
                "(" + Round.truncate(p.posToOutput(p.getMin())) + "°)\t"
                + Round.percent(p.posToScale(p.getMax())) + "% " +
                "(" + Round.truncate(p.posToAngle(p.getMax())) + "°)";
        t.addData("Min Max Out", s);

        // Output
        s = Round.truncate(p.outputOffset) + "\t" + Round.truncate(p.outputScale);
        t.addData("Output", s);

        // Teleop
        s = (teleop ? "Teleop" : "Auto") + "\t" +
                Round.truncate(p.rateLimit);
        t.addData("Teleop", s);

        // Presets
        s = "";
        for (String name : p.getPresets()) {
            if (!s.isEmpty()) {
                s += ", ";
            }
            s += name + ":" + p.getPreset(name);
        }
        t.addData("Presets", s);

        // Display
        t.update();
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public void setEnable(boolean enable) {
        if (enable) {
            // Force a null move to ensure everything is in sync
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
        // I don't expect stop() will be called in normal use, if it is this might need to go
        servo.getController().pwmDisable();
    }
}
