package org.firstinspires.ftc.teamcode.wheels;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumDrive extends TankDrive {

    public MecanumDrive(HardwareMap map, Telemetry telemetry, WheelsConfig config) {
        super(map, telemetry, config);
    }

    public void setSpeed(float speed, MOTOR_SIDE side, MOTOR_END end) {
        if (side == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null SIDE");
        }
        if (end == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null END");
        }
        if (!isAvailable()) {
            return;
        }
        for (WheelMotor motor : config.motors) {
            if (motor.side == side && motor.end == end) {
                motor.motor.setPower(speed * speedScale);
            }
        }
    }

    @Override
    public void setSpeed(float speed, MOTOR_SIDE side) {
        for (MOTOR_END end : MOTOR_END.values()) {
            setSpeed(speed, side, end);
        }
    }

    @Override
    public void setSpeed(float x, float y, float rotation) {

        // modified code from https://ftcforum.usfirst.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
        // from dmssargent
        double r = Math.hypot(x, y);
        double theta = Math.atan2(y, x);
        final float v1 = (float) (r * Math.cos(theta - Math.PI / 4)) + rotation;
        final float v2 = (float) (r * Math.sin(theta - Math.PI / 4)) - rotation;
        final float v3 = (float) (r * Math.sin(theta - Math.PI / 4)) + rotation;
        final float v4 = (float) (r * Math.cos(theta - Math.PI / 4)) - rotation;

        // except for this, this is mine
        setSpeed(v1, MOTOR_SIDE.LEFT, MOTOR_END.FRONT);
        setSpeed(v2, MOTOR_SIDE.RIGHT, MOTOR_END.FRONT);
        setSpeed(v3, MOTOR_SIDE.LEFT, MOTOR_END.BACK);
        setSpeed(v4, MOTOR_SIDE.RIGHT, MOTOR_END.BACK);
    }

    @Override
    public void loop(Gamepad pad) {
        // an unconventional implementation, because Lars wanted it so
        float lStickX = cleanJoystick(pad.left_stick_x); // rotation
        float lStickY = cleanJoystick(pad.left_stick_y); // y
        float rStickX = cleanJoystick(-pad.right_stick_x); // x

        setSpeed(rStickX, lStickY, lStickX);
    }
}