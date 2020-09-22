package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.core.OpModeEvents;
import org.firstinspires.ftc.teamcode.core.OpModeN2S;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Joy", group = "Test")
public class JoyTest extends OpModeN2S {
    public JoyTest() {
        super();
        add(new Mode());
    }

    private class Mode implements OpModeEvents {

        public void init() {
        }

        public void init_loop() {
        }

        public void start() {
        }

        public void loop() {
            telemetry.addData("L-X", R.O.gamepad1.left_stick_x);
            telemetry.addData("L-Y", R.O.gamepad1.left_stick_y);
            telemetry.addData("R-X", R.O.gamepad1.right_stick_x);
            telemetry.addData("L-Y", R.O.gamepad1.left_stick_y);
            telemetry.addData("L-Z", R.O.gamepad1.left_trigger);
            telemetry.addData("R-Z", R.O.gamepad1.right_trigger);
        }

        public void stop() {
        }
    }
}
