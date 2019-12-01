package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.RobotNG;

import java.util.HashMap;
import java.util.Set;

public class Actuators {
    private final RobotNG robot;
    private final HashMap<String, Actuator> a;

    public Actuators(RobotNG robot) {
        this.robot = robot;
        a = new HashMap<String, Actuator>();
    }

    public Set<String> get() {
        return a.keySet();
    }

    public Actuator get(String name) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty actuator");
            return null;
        }
        if (!a.containsKey(name)) {
            robot.log(this, "Unregistered actuator: " + name);
            return null;
        }
        return a.get(name);
    }

    public void add(Actuator actuator) {
        if (actuator == null) {
            robot.log("Null Actuator");
            return;
        }
        if (actuator.params() == null) {
            robot.log("Null Actuator_Params");
            return;
        }
        if (actuator.params().name() == null) {
            robot.log("Null Name");
            return;
        }
        a.put(actuator.params().name(), actuator);
    }

    public void remove(String name) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty actuator");
            return;
        }
        if (!a.containsKey(name)) {
            robot.log(this, "Unregistered actuator: " + name);
        }
        a.remove(name);
    }
}
