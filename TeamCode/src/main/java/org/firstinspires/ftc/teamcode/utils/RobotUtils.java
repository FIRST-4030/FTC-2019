package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.actuators.Actuators;

import java.util.ArrayList;

public class RobotUtils implements Actuators {
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

    // Keep a list of Actuators
    public void register(Actuators s) {
        actuators.add(s);
    }

    // This could also log to-disk or other useful places
    public void log(String s) {
        opmode.telemetry.log().add(s);
    }
}
