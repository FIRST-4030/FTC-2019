package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Motor Test", group = "Test")
public class MotorTest extends OpMode {
    private Robot R;

    @Override
    public void init() {
        R = Robot.start(this);
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        // Pad 1, right stick, vertical axis
        double p = gamepad1.right_stick_y;
        // Clip, because it's good practice (gamepads are range-safe but other inputs aren't)
        p = Range.clip(p, -1.0d, 1.0d);
        // Deadband to let us hold still
        if (Math.abs(p) < 0.10d) {
            p = 0.0d;
        }
        // Cube to better fit the linear input to the speed response curve
        p = Math.pow(p, 3);
        // Invert to make forward forward
        p *= -1.0d;
        // Set motor power
        R.m1.power(p);
        R.m2.power(-p);

        // Feedback
        telemetry.addData(R.m1.name + " Power", R.m1.power());
        telemetry.addData(R.m1.name + " Encoder", R.m1.encoder());
        telemetry.addData(R.m2.name + " Power", R.m2.power());
        telemetry.addData(R.m2.name + " Encoder", R.m2.encoder());
    }

    @Override
    public void stop() {
        if (R != null) {
            R.stop();
        }
    }
}
