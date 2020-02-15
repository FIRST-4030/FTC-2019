package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Motor Test", group = "Test")
public class MotorTest extends OpMode {
    public final Robot R = Robot.R;

    @Override
    public void init() {

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
        // Cube to better fit the linear input to the speed reponse curve
        p = Math.pow(p, 3);
        // Invert to make forward forward
        p *= -1.0d;
        // Set motor power
        R.m.power(p);

        // Feedback
        telemetry.addData(R.m.name + " Power", p);
        telemetry.addData(R.m.name + " Encoder", R.m.encoder());
    }

    @Override
    public void stop() {
        R.stop();
    }
}
