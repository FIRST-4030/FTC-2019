package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.core.OpModeEvents;
import org.firstinspires.ftc.teamcode.core.OpModeN2S;
import org.firstinspires.ftc.teamcode.utils.Round;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Motor Test", group = "Test")
public class MotorTest extends OpModeN2S {
    public MotorTest() {
        super();
        add(new Mode());
    }

    private class Mode implements OpModeEvents {

        public void init() {
            R.B.register("Reverse", gamepad1, PAD_BUTTON.a);
        }

        public void init_loop() {

        }

        public void start() {
            for (String s : R.G.list()) {
                Robot.verbose("Global: " + s + " => " + R.G.s(s));
            }
            Robot.log("Logged globals to Android");
        }

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

            // Reverse on button press
            if (R.B.get("Reverse")) {
                R.m1.reverse(!R.m1.reverse());
            }

            // Feedback
            telemetry.addData("Battery Voltage",
                    R.G.d("BATTERY_VOLTAGE"));
            telemetry.addData("Battery Current",
                    R.G.d("BATTERY_CURRENT"));
            telemetry.addData(R.m1.name + " Power",
                    R.m1.power());
            telemetry.addData(R.m1.name + " Reverse",
                    R.m1.reverse());
            telemetry.addData(R.m1.name + " Encoder",
                    R.G.i("ENCODER_" + R.m1.name));
            telemetry.addData(R.m1.name + " Current",
                    Round.r(R.G.d("CURRENT_" + R.m1.name)));
            telemetry.addData(R.m1.name + " Velocity",
                    Round.r(R.G.d("VELOCITY_" + R.m1.name)));
        }

        public void stop() {
        }
    }
}
