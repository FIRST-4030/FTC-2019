package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.core.OpModeEvents;
import org.firstinspires.ftc.teamcode.core.OpModeN2S;

@Disabled
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Empty", group = "Test")
public class Empty extends OpModeN2S {
    public Empty() {
        super();
        add(new Mode());
    }

    private class Mode implements OpModeEvents {

        public void init() {
        }

        public void init_loop() {
            telemetry.addData("Battery Voltage", R.G.d("BATTERY_VOLTAGE"));
        }

        public void start() {
        }

        public void loop() {
            telemetry.addData("Battery Current", R.G.d("BATTERY_CURRENT"));
        }

        public void stop() {
        }
    }
}
