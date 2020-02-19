package org.firstinspires.ftc.teamcode.sensors.gyro;

import org.firstinspires.ftc.teamcode.sensors.Sensors;

public interface Gyro extends Sensors {
    boolean ready();

    float heading();

    float raw();

    void offset(float offset);

    float offset();
}
