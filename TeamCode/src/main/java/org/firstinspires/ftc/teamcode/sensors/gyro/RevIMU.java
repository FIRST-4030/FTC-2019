package org.firstinspires.ftc.teamcode.sensors.gyro;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;
import org.firstinspires.ftc.teamcode.storage.globals.GlobalsPoll;
import org.firstinspires.ftc.teamcode.utils.Heading;

class IMUWaiter implements Runnable {
    private static final int TIMEOUT = 2500;

    private static final String LOG_NAME = null;
    private static final String CALIBRATION_FILE = null;

    private static final int INTEGRATION_INTERVAL = 1000;
    private static final BNO055IMU.AccelerationIntegrator INTEGRATOR = new JustLoggingAccelerationIntegrator();

    private BNO055IMU imu;
    private final RevIMU parent;

    public IMUWaiter(RevIMU parent, BNO055IMU imu) {
        this.parent = parent;
        this.imu = imu;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void run() {
        // Record the start time so we can detect timeouts
        long start = System.currentTimeMillis();

        // Basic parameters for the IMU, to get units we like
        BNO055IMU.Parameters params = new BNO055IMU.Parameters();
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;

        // Optionally select an integration algorithm
        if (INTEGRATOR != null) {
            params.accelerationIntegrationAlgorithm = INTEGRATOR;
        }

        // Optionally log to the phone's catlog
        if (LOG_NAME != null && !LOG_NAME.isEmpty()) {
            params.loggingEnabled = true;
            params.loggingTag = LOG_NAME;
        }

        // Optionally load static calibration data
        if (CALIBRATION_FILE != null && !CALIBRATION_FILE.isEmpty()) {
            params.calibrationDataFile = CALIBRATION_FILE;
        }

        // Init -- this is where we hang
        try {
            imu.initialize(params);
        } catch (Exception e) {
            fail();
            return;
        }
        if (System.currentTimeMillis() - start > TIMEOUT) {
            fail();
            return;
        }

        // If things look good
        if (imu != null) {
            // Start integrations from 0, 0, 0, 0
            imu.startAccelerationIntegration(new Position(), new Velocity(),
                    INTEGRATION_INTERVAL);
        }

        // Make the gyro available (or unavailable, if we failed)
        parent.gyro(imu);
    }

    private void fail() {
        imu = null;
        Robot.warn(this, "Failed to initialize: " + parent.name);
    }
}

public class RevIMU implements Gyro, GlobalsPoll {
    private BNO055IMU gyro = null;
    private float offset = 0.0f;
    public final String name;

    public RevIMU(String name) {
        if (name == null || name.isEmpty()) {
            Robot.err(this, "No name provided");
            name = this.toString();
        }
        this.name = name;

        // Attempt to init
        BNO055IMU imu;
        try {
            imu = Robot.O.hardwareMap.get(BNO055IMU.class, name);
        } catch (Exception e) {
            imu = null;
            Robot.err(this, "No such device: " + name);
        }

        // Start the IMU in a background thread -- it behaves poorly when not available
        if (imu != null) {
            Thread thread = new Thread(new IMUWaiter(this, imu));
            thread.start();
        }

        // Register to publish data
        Robot.R.G.register(this);
    }

    /**
     * Allow the background thread to feed us the underlying gyro, when available
     *
     * @param gyro The active gyro
     */
    void gyro(BNO055IMU gyro) {
        this.gyro = gyro;
    }

    /**
     * Is the gyro running and producing valid readings?
     *
     * @return True if the gyro is ready
     */
    public boolean ready() {
        return (gyro != null && gyro.isGyroCalibrated());
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
        return -gyro.getAngularOrientation(AxesReference.INTRINSIC,
                AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
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
