package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.core.OpModeEvents;
import org.firstinspires.ftc.teamcode.core.OpModeN2S;
import org.firstinspires.ftc.teamcode.opmodes.debug.ServoN2S_Debug;
import org.firstinspires.ftc.teamcode.utils.Round;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Zach Test", group = "Test")
public class ZTest extends OpModeN2S {
    public ZTest() {
        super();
        add(new Mode());
    }

    private class Mode implements OpModeEvents {

        public void init() {
            R.B.register("Reverse", gamepad1, PAD_BUTTON.a);
            R.B.register("Servo", gamepad1, PAD_BUTTON.y);

            // Force this debug mode to be active for testing
            // We don't yet have a debug mode handler to do this properly
            add(new ServoN2S_Debug(R.s1));
        }

        public void init_loop() {

        }

        public void start() {
            for (String s : R.G.list()) {
                Robot.verbose(this, "Global: " + s + " => " + R.G.s(s));
            }
            Robot.log(this, "Logged globals to Android");
        }

        public void loop() {
            // Pad 1, right stick, vertical axis
            double p = gamepad1.right_stick_y;
            // Clip, because it's good practice (gamepads are range-safe but other inputs aren't)
            p = Range.clip(p, -1.0d, 1.0d);
            // Dead-band to let us hold still
            if (Math.abs(p) < 0.10d) {
                p = 0.0d;
            }
            // Cube to better fit the linear input to the speed response curve
            p = Math.pow(p, 3);
            // Invert to make forward forward
            p *= -1.0d;
            // Set motor power
            R.m1.power(p);

            // Reverse on button press
            if (R.B.get("Reverse")) {
                R.m1.reverse(!R.m1.reverse());
            }

            // Servo click
            if (R.B.get("Servo")) {
                R.s1.delta(0.05);
            }

            // Feedback
            telemetry.addData("Battery Voltage",
                    R.G.d("BATTERY_VOLTAGE"));
            telemetry.addData(R.m1.name + " Reverse",
                    R.m1.reverse());
            telemetry.addData(R.m1.name + " Encoder",
                    R.G.i("ENCODER_" + R.m1.name));
            telemetry.addData(R.m1.name + " Current",
                    Round.r(R.G.d("CURRENT_" + R.m1.name)));
            telemetry.addData("Heading",
                    Round.r(R.G.d("GYRO_HEADING")));
            telemetry.addData(R.s1.name + " Position",
                    Round.r(R.G.d("SERVO_" + R.s1.name)));
        }

        public void stop() {
        }
    }
}
