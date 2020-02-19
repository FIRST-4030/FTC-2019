package org.firstinspires.ftc.teamcode.sensors.gyro;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.sensors.Sensors;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;
import org.firstinspires.ftc.teamcode.storage.globals.GlobalsPoll;
import org.firstinspires.ftc.teamcode.utils.Heading;

public class MRGyro implements Gyro, Sensors, GlobalsPoll {
    private ModernRoboticsI2cGyro gyro;
    private float offset = 0.0f;

    public MRGyro(String name) {
        if (name == null || name.isEmpty()) {
            Robot.err(this.getClass().getSimpleName() + ": No name provided");
            name = this.toString();
        }
        try {
            gyro = (ModernRoboticsI2cGyro) Robot.O.hardwareMap.gyroSensor.get(name);
        } catch (Exception e) {
            gyro = null;
            Robot.err(this.getClass().getSimpleName() +
                    ": No such device: " + name);
            return;
        }

        // Start and calibrate
        gyro.resetDeviceConfigurationForOpMode();
        gyro.calibrate();

        // Register to publish data
        Robot.R.G.register(this);
    }

    /**
     * Is the gyro running and producing valid readings?
     *
     * @return True if the gyro is ready
     */
    public boolean ready() {
        return (gyro != null && !gyro.isCalibrating());
    }

    /**
     * Get the current gyro offset
     *
     * @return Offset in degrees
     */
    public float offset() {
        return offset;
    }

    /**
     * Set the gyro offset.
     * This is typically used to adjust the raw heading to a field-relative measurement
     *
     * @param offset Offset in degrees
     */
    public void offset(float offset) {
        this.offset = offset;
    }

    /**
     * Current normalized heading, relative to orientation at init
     * Consider using the polled value with:
     * Robot.R.G.d("GYRO_RAW");
     *
     * @return Normalized heading
     */
    public float raw() {
        if (!ready()) {
            return 0.0f;
        }

        // Invert to make CW rotation increase with the heading
        return -gyro.getIntegratedZValue();
    }

    /**
     * Current normalized heading, adjusted by the offset.
     * Typically the offset is configured to make 0° field-north
     * Consider using the polled value with:
     * Robot.R.G.d("GYRO_HEADING");
     *
     * @return Normalized heading
     */
    public float heading() {
        return heading(raw());
    }

    /**
     * Current normalized heading, adjusted by the offset.
     * Provided for internal use to allow raw and offset
     * headings to be calculated in a single poll
     *
     * @param raw Current raw heading
     * @return Normalized heading
     */
    private float heading(float raw) {
        return Heading.normalize(raw() + offset);
    }

    /**
     * Publish the raw and offset headings
     *
     * @param g The Globals object calling this method
     */
    public void gPoll(Globals g) {
        float raw = raw();
        g.set("GYRO_RAW", raw);
        g.set("GYRO_HEADING", heading(raw));
    }
}