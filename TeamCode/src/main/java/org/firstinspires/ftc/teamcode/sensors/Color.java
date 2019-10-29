package org.firstinspires.ftc.teamcode.sensors;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.Available;

public class Color implements Available {
    private NormalizedColorSensor sensor;

    public Color(HardwareMap map, Telemetry telemetry, String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null/empty name");
        }
        try {
            sensor = map.get(NormalizedColorSensor.class, name);
        } catch (Exception e) {
            sensor = null;
            telemetry.log().add(this.getClass().getSimpleName() + ": No such device: " + name);
            return;
        }
    }

    public void enableLight(boolean enable) {
        if (!isAvailable()) {
            return;
        }
        if (!(sensor instanceof SwitchableLight)) {
            return;
        }
        ((SwitchableLight) sensor).enableLight(enable);
    }

    public boolean isAvailable() {
        return (sensor != null);
    }

    public NormalizedRGBA get() {
        NormalizedRGBA colors = new NormalizedRGBA();
        if (isAvailable()) {
            colors = sensor.getNormalizedColors();
        }
        return colors;
    }
}
