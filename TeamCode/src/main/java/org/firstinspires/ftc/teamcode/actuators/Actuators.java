package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.RobotNG;

import java.util.HashSet;
import java.util.Set;

public class Actuators {
    private final RobotNG robot;
    private final HashSet<Actuator> a;

    public Actuators(RobotNG robot) {
        this.robot = robot;
        a = new HashSet<Actuator>();
    }

    public Set<Actuator> get() {
        return (Set<Actuator>) a.clone();
    }

    public void add(Actuator actuator) {
        if (actuator == null) {
            robot.log("Null Actuator");
            return;
        }
        a.add(actuator);
    }

    public void remove(Actuator actuator) {
        if (actuator == null) {
            robot.log("Null actuator");
            return;
        }
        if (!a.contains(actuator)) {
            robot.log(this, "Unregistered actuator: " + actuator.p.name);
        }
        a.remove(actuator);
    }
}
