package org.firstinspires.ftc.teamcode.robot.config;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.actuators.ServoFTC;
import org.firstinspires.ftc.teamcode.actuators.ServoConfig;
import org.firstinspires.ftc.teamcode.robot.SERVOS;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.config.Configs;

public class ServoConfigs extends Configs {
    public ServoConfigs(HardwareMap map, Telemetry telemetry, BOT bot) {
        super(map, telemetry, bot);
    }

    public ServoFTC init(SERVOS name) {
        ServoConfig config = config(name);
        super.checkConfig(config, name);
        ServoFTC servo = new ServoFTC(map, telemetry, config);
        super.checkAvailable(servo, name);
        return servo;
    }

    public ServoConfig config(SERVOS servo) {
        super.checkBOT();
        checkNull(servo, SERVOS.class.getName());

        ServoConfig config = null;
        switch (bot) {
            case SCISSOR:
                switch(servo) {
                    case CLAW:
                        config = new ServoConfig("Claw");
                        break;
                    case FLIPPER:
                        config = new ServoConfig("Flipper");
                        break;
                    case CAPSTONE:
                        config = new ServoConfig("Capstone");
                        break;
                    case LEFT_HOOK:
                        config = new ServoConfig("Left Hook", true, 0.2f, 0.8f);
                        break;
                    case RIGHT_HOOK:
                        config = new ServoConfig("Right Hook", false, 0.5f, 1.0f);
                }
                break;

            case ARM:
                switch (servo) {
                    case LOWER:
                        config = new ServoConfig("Lower", true, 0.0f, 1.0f);
                        break;
                    case UPPER:
                        config = new ServoConfig("Upper", false, 0.0f, 1.0f);
                        break;
                    case ROTATION:
                        config = new ServoConfig("Rotation", false, 0.0f, 1.0f);
                        break;
                    case CLAW:
                        config = new ServoConfig("Claw", false, 0.5f, 0.8f);
                        break;
                    case WRIST:
                        config = new ServoConfig("Swivel", false, 0.0f, 1.0f);
                }
        }

        return config;
    }
}
