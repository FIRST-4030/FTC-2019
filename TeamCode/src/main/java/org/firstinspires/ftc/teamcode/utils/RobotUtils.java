package org.firstinspires.ftc.teamcode.utils;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.actuators.Actuators;

import java.util.ArrayList;

public class RobotUtils {
    private final ArrayList<Actuators> actuators;
    public final OpMode opmode;

    public RobotUtils(OpMode opmode) {
        this.opmode = opmode;
        actuators = new ArrayList<>();
    }

    // Global stop
    public void stop() {
        for (Actuators a : actuators) {
            a.stop();
        }
    }

    // Keep a list of Actuators for global operations
    public void register(Actuators s) {
        actuators.add(s);
    }

    public void log(String s) {
        Log.i("Robot", s);
        opmode.telemetry.log().add(s);
    }

    public void warn(String s) {
        Log.w("Robot", s);
        opmode.telemetry.log().add(s);
    }

    public void err(String s) {
        Log.e("Robot", s);
        opmode.telemetry.log().add(s);
    }
}
