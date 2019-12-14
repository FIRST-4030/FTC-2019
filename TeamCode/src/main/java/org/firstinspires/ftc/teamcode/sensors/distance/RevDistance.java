package org.firstinspires.ftc.teamcode.sensors.distance;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class RevDistance implements Distance {
    private DistanceSensor distanceSensor;

    public RevDistance(HardwareMap map, Telemetry telemetry, String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null/empty name");
        }
        try {
            distanceSensor = map.get(DistanceSensor.class, name);

        } catch (Exception e) {
            telemetry.log().add(this.getClass().getSimpleName() + ": No such device: " + name);
            return;
        }
    }

    @Override
    public boolean isAvailable() {
        return (distanceSensor != null);
    }

    public double distance() {
        return distanceMM();
    }

    @Override
    public double distanceMM() {
        double mm = DistanceUnit.infinity;
        if (isAvailable()) {
            mm = distanceSensor.getDistance(DistanceUnit.MM);
        }
        return mm;
    }

    @Override
    public double distanceIn() {
        double in = DistanceUnit.infinity;
        if (isAvailable()) {
            in = distanceSensor.getDistance(DistanceUnit.INCH);
        }
        return in;
    }

    public int get() {
        return (int) distance();
    }

    @Override
    public double minDistance() {
        return 0.0;
    }

    @Override
    public double maxDistance() {
        return 200.0;
    }
}
