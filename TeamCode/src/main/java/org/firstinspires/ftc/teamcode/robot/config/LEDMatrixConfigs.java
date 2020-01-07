package org.firstinspires.ftc.teamcode.robot.config;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.config.Configs;
import org.firstinspires.ftc.teamcode.robot.LED_MATRICIES;
import org.firstinspires.ftc.teamcode.sensors.led_matrix.LEDMatrix;
import org.firstinspires.ftc.teamcode.sensors.led_matrix.LED_MATRIX_TYPES;
import org.firstinspires.ftc.teamcode.sensors.led_matrix.LEDMatrixConfig;
import org.firstinspires.ftc.teamcode.sensors.led_matrix.StupidAbstraction;
import org.firstinspires.ftc.teamcode.sensors.led_matrix.driver.HT16K33;

public class LEDMatrixConfigs extends Configs {
    public LEDMatrixConfigs(HardwareMap map, Telemetry telemetry, BOT bot) {
        super(map, telemetry, bot);
    }

    public LEDMatrix init(LED_MATRICIES name) {
        LEDMatrixConfig config = config(name);
        super.checkConfig(config, name);
        LEDMatrix matrix = null;
        switch (config.type) {
            case HT16K33:
                matrix = new StupidAbstraction(map, telemetry, config.name);

        }

        super.checkAvailable(matrix, name);
        return matrix;
    }

    public LEDMatrixConfig config(LED_MATRICIES name) {
        super.checkBOT();

        LEDMatrixConfig config = null;
        switch (bot) {
            case TEST:
                switch(name) {
                    case TEST:
                        config = new LEDMatrixConfig(LED_MATRIX_TYPES.HT16K33, "matrix");
                        break;
                }
                break;

        }
        return config;
    }
}
