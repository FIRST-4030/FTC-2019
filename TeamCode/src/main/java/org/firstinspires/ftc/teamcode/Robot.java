package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.actuators.Motor;
import org.firstinspires.ftc.teamcode.actuators.ServoN2S;
import org.firstinspires.ftc.teamcode.core.RobotUtils;
import org.firstinspires.ftc.teamcode.sensors.RevHub;
import org.firstinspires.ftc.teamcode.sensors.gyro.Gyro;
import org.firstinspires.ftc.teamcode.sensors.gyro.RevIMU;

@SuppressWarnings("WeakerAccess")
public class Robot extends RobotUtils {
    //tim is a massive dummy -the robot
    public static Robot R = null;

    // Core hardware
    public final RevHub revhub;
    public final Gyro gyro;

    // All our hardware should have members declared here
    public final Motor m1;
    public final Motor m2;
    public final ServoN2S s1;

    public Robot(OpMode opmode) {
        super(opmode);
        R = this;

        // Core hardware config
        revhub = new RevHub();
        gyro = new RevIMU("imu");

        // Hardware config
        // If you provide config in the JSON file you can just do this
        // The Motor class reads config parameters out of the JSON by class and device name
        m1 = new Motor("FL");
        // But you can also initialize things directly, if you need to calculate something
        m2 = new Motor("BL", false, true, null);

        s1 = new ServoN2S("S1");
    }
}
