package org.firstinspires.ftc.teamcode.actuators;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;
import org.firstinspires.ftc.teamcode.storage.globals.GlobalsPoll;
import org.firstinspires.ftc.teamcode.utils.Round;

public class ServoN2S implements Actuators, GlobalsPoll {
    private Servo servo;
    private final String name;
    private boolean stopped = false;
    private boolean teleop = false;
    private boolean enforce = true;

    // TODO: Replace this with JSON config
    private ServoNG_Params p = new ServoNG_Params("foo");

    public ServoN2S(String name) {
        if (name == null || name.isEmpty()) {
            Robot.err(this.getClass().getSimpleName() + ": No name provided");
            name = this.toString();
        }
        this.name = name;
        // TODO: Decode config
        //init();
    }

    // TODO: Constructor with all the bits exposed
    public void ServoN2S(String name, boolean reverse, double range, double min, double max,
                         double offset, String presets) {
    }

    private void init(boolean reverse, double range, double min, double max, double offset,
                      String presets) {
        try {
            servo = Robot.O.hardwareMap.servo.get(name);
            // FTC FORWARD/REVERSE mode
            if (reverse) {
                servo.setDirection(Servo.Direction.REVERSE);
            }
            // Init position, if set
            if (p.hasPreset(ServoNG_Params.INIT_NAME)) {
                position(p.getPreset(ServoNG_Params.INIT_NAME));
            }
        } catch (Exception e) {
            servo = null;
            Robot.err(this.getClass().getSimpleName() +
                    ": Servo not available: " + name);
        }
    }

    /**
     * Get the current teleop mode
     *
     * @return True if teleop is enabled
     */
    public boolean teleop() {
        return teleop;
    }

    /**
     * Set the teleop mode
     *
     * @param enable True to enable teleop, false to disable
     */
    public void teleop(boolean enable) {
        teleop = enable;
    }

    /**
     * Teleop control of the servo
     *
     * @param delta Relative change of servo output position
     */
    public void teleop(double delta) {
        if (!teleop) {
            return;
        }
        position(position() + delta, true);
    }

    public void enforceLimits(boolean enforce) {
        this.enforce = enforce;
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
        return servo.getPosition();
    }

    /**
     * Actually move the servo
     *
     * @param pos            The commanded position
     * @param teleopOverride True to over the teleop flag, false to enforce it
     */
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
        if (stopped) {
            servo.getController().pwmEnable();
            stopped = false;
        }

        // Move
        servo.setPosition(pos);
    }

    private void limitError(double pos) {
        Robot.warn(this.getClass().getSimpleName() + ": " +
                name + ": Cannot reach position: " +
                Round.r(pos) + "\t" +
                Round.r(p.posToAngle(pos)) + "°\t");
    }

    /**
     * Set the servo position directly
     *
     * @param pos The commanded position
     */
    public void position(double pos) {
        position(pos, false);
    }

    /**
     * Set the servo position by angle
     *
     * @param angle The commanded angle
     */
    public void angle(double angle) {
        position(p.angleToPos(angle));
    }

    /**
     * Set the servo position by relative change
     *
     * @param delta The change in position
     */
    public void relative(double delta) {
        position(position() + delta);
    }

    /**
     * Set the servo position by relative change in output angle
     *
     * @param delta The change in position
     */
    public void relativeAngle(double delta) {
        relative(delta / p.range);
    }

    public void preset(String name) {
        position(p.getPreset(name));
    }

    /**
     * Is the servo intialized and ready to move?
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
        g.set("SERVO_" + name, position());
    }
}
