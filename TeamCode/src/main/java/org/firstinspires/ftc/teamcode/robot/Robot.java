package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.actuators.Motor;
import org.firstinspires.ftc.teamcode.actuators.ServoFTC;
import org.firstinspires.ftc.teamcode.config.BOT;
import org.firstinspires.ftc.teamcode.robot.common.Common;
import org.firstinspires.ftc.teamcode.robot.config.ColorRangeConfigs;
import org.firstinspires.ftc.teamcode.robot.config.GyroConfigs;
import org.firstinspires.ftc.teamcode.robot.config.MotorConfigs;
import org.firstinspires.ftc.teamcode.robot.config.ServoConfigs;
import org.firstinspires.ftc.teamcode.robot.config.WheelsConfigs;
import org.firstinspires.ftc.teamcode.sensors.color_range.ColorRange;
import org.firstinspires.ftc.teamcode.sensors.color_range.RevColorRange;
import org.firstinspires.ftc.teamcode.sensors.gyro.Gyro;
import org.firstinspires.ftc.teamcode.vuforia.VuforiaFTC;
import org.firstinspires.ftc.teamcode.wheels.Wheels;

public class Robot {
    public static Robot robot = null;
    public final Common common;
    public final BOT bot;
    public final HardwareMap map;
    public final Telemetry telemetry;

    // Base
    public final Gyro gyro;
    public final Wheels wheels;
    public final VuforiaFTC vuforia;

    // Collector
    public final Motor collectorLeft;
    public final Motor collectorRight;
    public final ColorRange loadSensor;

    // Lift
    public final Motor lift;
    public final ServoFTC claw;
    public final ServoFTC flipper;

    public final ServoFTC hookLeft;
    public final ServoFTC hookRight;
    public final ServoFTC capstone;

    public Robot(HardwareMap map, Telemetry telemetry) {
        this(map, telemetry, null);
    }

    public Robot(HardwareMap map, Telemetry telemetry, BOT bot) {
        robot = this;
        this.map = map;
        this.telemetry = telemetry;
        if (bot == null) {
            bot = detectBot();
        }
        this.bot = bot;

        GyroConfigs gyros = new GyroConfigs(map, telemetry, bot);
        WheelsConfigs wheels = new WheelsConfigs(map, telemetry, bot);
        MotorConfigs motors = new MotorConfigs(map, telemetry, bot);
        //PIDMotorConfigs pids = new PIDMotorConfigs(map, telemetry, bot);
        ServoConfigs servos = new ServoConfigs(map, telemetry, bot);
        //SwitchConfigs switches = new SwitchConfigs(map, telemetry, bot);
        // TODO: Disabled until we can test
        //ColorRangeConfigs colors = new ColorRangeConfigs(map, telemetry, bot);

        // Base
        this.wheels = wheels.init();
        this.wheels.stop();
        gyro = gyros.init();
        vuforia = new VuforiaFTC(map, telemetry, bot);

        // Lift
        if (bot == BOT.PRODUCTION) {
            lift = motors.init(MOTORS.LIFT);
            claw = servos.init(SERVOS.CLAW);
            flipper = servos.init(SERVOS.FLIPPER);
        } else {
            lift = null;
            claw = null;
            flipper = null;
        }

        // Collector
        if (bot == BOT.PRODUCTION) {
            collectorLeft = motors.init(MOTORS.COLLECTOR_LEFT);
            collectorRight = motors.init(MOTORS.COLLECTOR_RIGHT);
            // TODO: Disabled until we can test
            loadSensor = null;
            //loadSensor = colors.init();
        } else {
            collectorLeft = null;
            collectorRight = null;
            loadSensor = null;
        }

        // Foundation hooks
        if (bot == BOT.PRODUCTION) {
            hookLeft = servos.init(SERVOS.LEFT_HOOK);
            hookRight = servos.init(SERVOS.RIGHT_HOOK);
        } else {
            hookLeft = null;
            hookRight = null;
        }

        // Capstone
        if (bot == BOT.PRODUCTION) {
            capstone = servos.init(SERVOS.CAPSTONE);
        } else {
            capstone = null;
        }

        this.common = new Common(this);
    }

    public BOT detectBot() {
        // Try WheelsConfigs from each bot until something succeeds
        BOT bot = null;
        for (BOT b : BOT.values()) {
            WheelsConfigs wheels = new WheelsConfigs(map, telemetry, b);
            Wheels w = wheels.init();
            if (w != null && w.isAvailable()) {
                bot = b;
                break;
            }
        }
        if (bot == null) {
            bot = BOT.values()[0];
            telemetry.log().add("BOT detection failed. Default: " + bot);
        }
        if (bot.ordinal() != 0) {
            telemetry.log().add("Using BOT: " + bot);
        }
        return bot;
    }
}
