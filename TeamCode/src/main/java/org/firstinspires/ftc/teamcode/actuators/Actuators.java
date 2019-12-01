package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.debug.Debug;
import org.firstinspires.ftc.teamcode.debug.DeviceProvider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

public class Actuators implements DeviceProvider {
    private final RobotNG robot;
    private final HashMap<String, Actuator> a;
    private final ListIterator<String> it;

    public Actuators(RobotNG robot) {
        this.robot = robot;
        a = new HashMap<>();
        it = new LinkedList<>(a.keySet()).listIterator();
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

    public Debug debug_next() {
        if (!it.hasNext()) {
            while (it.hasPrevious()) {
                it.previous();
            }
        }
        return get(it.next());
    }

    public Debug debug_prev() {
        if (!it.hasPrevious()) {
            while (it.hasNext()) {
                it.next();
            }
        }
        return get(it.previous());
    }
}
