package org.firstinspires.ftc.teamcode.sensors.distance;

import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.teamcode.utils.Available;

public interface Distance extends Available {

    double minDistance();
    double maxDistance();
    double distance();
    double distanceMM();
    double distanceIn();
}
