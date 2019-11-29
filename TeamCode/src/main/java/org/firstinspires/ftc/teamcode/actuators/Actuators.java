package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.RobotNG;

import java.util.HashMap;
import java.util.Set;

public class Actuators {
    private final RobotNG robot;
    private final HashMap<String, Actuator> a;

    public Actuators(RobotNG robot) {
        this.robot = robot;
        a = new HashMap<>();
    }

    public Set<String> getNames() {
        return a.keySet();
    }

    // Note that this can return null
    public Actuator get(String name) {
        return a.get(name);
    }

    public void add(String name, Actuator actuator) {
        if (name == null || name.isEmpty()) {
            robot.log("add(): Null/empty name");
            return;
        }
        if (actuator == null) {
            robot.log("Null Actuator: " + name);
            return;
        }
        if (a.containsKey(name)) {
            robot.log("Actuator exists: " + name);
            return;
        }
        a.put(name, actuator);
    }

    public void remove(String name) {
        if (name == null || name.isEmpty()) {
            robot.log("remove(): Null/empty name");
            return;
        }
        if (!a.containsKey(name)) {
            robot.log("Actuator does not exist: " + name);
            return;
        }
        a.remove(name);
    }
}
