package org.firstinspires.ftc.teamcode.opmodes.debug;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.actuators.ServoN2S;
import org.firstinspires.ftc.teamcode.buttons.BUTTON_TYPE;
import org.firstinspires.ftc.teamcode.buttons.PAD_BUTTON;
import org.firstinspires.ftc.teamcode.core.RobotUtils;
import org.firstinspires.ftc.teamcode.utils.Round;

import java.util.Iterator;
import java.util.Map;

public class ServoN2S_Debug extends OpMode_Debug {
    private final ServoN2S servo;
    private boolean limits;
    private Gamepad pad;

    private Map<String, Double> presets;
    private Iterator<String> it;

    public ServoN2S_Debug(ServoN2S servo) {
        if (servo == null) {
            throw new IllegalArgumentException("No servo provided");
        }
        this.servo = servo;
    }

    public void init() {
        // Save the incoming state so we can restore it
        limits = servo.limits();

        // Freedom
        servo.limits(false);

        // Buttons
        pad = RobotUtils.O.gamepad1;
        B.register("SLOW", pad, PAD_BUTTON.right_stick_button, BUTTON_TYPE.TOGGLE);
        B.register("PRESET", pad, PAD_BUTTON.right_trigger);
        B.register("LIMITS", pad, PAD_BUTTON.left_trigger);
        B.register("MIN", pad, PAD_BUTTON.left_bumper);
        B.register("MAX", pad, PAD_BUTTON.right_bumper);
        B.register("CENTER", pad, PAD_BUTTON.a);
        B.register("DOWN", pad, PAD_BUTTON.x);
        B.register("UP", pad, PAD_BUTTON.b);
        //B.register("UNUSED", pad, PAD_BUTTON.y);

        // Grab the list of presets
        presets = servo.presets();
    }

    public void init_loop() {
    }

    public void start() {
    }

    public void loop() {
        B.update();

        servo.teleop(pad.right_stick_x * (B.get("SLOW") ? 0.5d : 1.0d));
        if (B.get("PRESET")) {
            if (it == null || !it.hasNext()) {
                it = presets.keySet().iterator();
            }
            servo.preset(it.next());
        }
        if (B.get("LIMITS")) {
            servo.limits(!servo.limits());
        }
        if (B.get("CENTER")) {
            servo.scale(0.5d);
        }
        if (B.get("MIN")) {
            servo.scale(0.0d);
        }
        if (B.get("MAX")) {
            servo.scale(1.0d);
        }

        // Name/Mode
        String s = servo.name + "\t" +
                (servo.ready() ? "Enabled" : "Disabled");
        R.O.telemetry.addData("Servo", s);
        s = (servo.reverse() ? "Reverse" : "Forward") + "\t" +
                (servo.limits() ? "Limited" : "Unlimited") + "\t"
                + Round.r(servo.rate()) + " s^-1";
        R.O.telemetry.addData("Mode", s);

        // Position
        double pos = servo.raw();
        s = Round.r(pos) + "\t" +
                Round.r(servo.rawToOffset(pos)) + "\t" +
                Round.r(servo.rawToScale(pos) * 100.0d) + "%";
        R.O.telemetry.addData("Raw Offset Scale", s);

        // Min[0]/Max[1]/Offset
        s = Round.r(servo.minmax()[0]) + "\t" +
                Round.r(servo.minmax()[1]) + "\t" +
                Round.r(servo.offset());
        R.O.telemetry.addData("Min Max Offset", s);

        // Presets
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : presets.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(e.getKey()).append(":");
            sb.append(Round.r(e.getValue()));
        }
        R.O.telemetry.addData("Presets", sb);

        // Display
        R.O.telemetry.update();
    }

    public void stop() {
        // Restore the original state when leaving
        servo.limits(limits);

        // No need to tear down buttons -- we have our own ButtonHandler
    }
}