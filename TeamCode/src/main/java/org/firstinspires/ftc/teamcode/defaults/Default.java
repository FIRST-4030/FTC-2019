package org.firstinspires.ftc.teamcode.defaults;

import org.firstinspires.ftc.teamcode.RobotNG;

import java.util.HashMap;
import java.util.Set;

public class Default {
    private final RobotNG robot;
    private final HashMap<String, Data> orig;
    private final HashMap<String, Data> current;

    public Default(RobotNG robot) {
        if (robot == null) {
            throw new IllegalStateException("Robot not available");
        }
        this.robot = robot;
        orig = new HashMap<>();
        current = new HashMap<>();
    }

    public Default() {
        this(RobotNG.R);
    }

    public boolean exists(String name) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty name");
            return false;
        }
        return current.containsKey(name);
    }

    private boolean register(String name, Data d) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty name");
            return false;
        }
        if (d == null) {
            robot.log(this, "Null data");
            return false;
        }
        if (current.containsKey(name)) {
            robot.log(this, "Already registered: " + name);
            return false;
        }
        orig.put(name, d);
        current.put(name, d.dup());
        return true;
    }

    public boolean register(String name, boolean val) {
        Data d = new Data(val);
        return register(name, d);
    }

    public boolean register(String name, int val) {
        Data d = new Data(val);
        return register(name, d);
    }

    public boolean register(String name, double val) {
        Data d = new Data(val);
        return register(name, d);
    }

    public boolean register(String name, String val) {
        Data d = new Data(val);
        return register(name, d);
    }

    public void update(String name, boolean val) {
        Data d = safeGet(name);
        d.set(val);
    }

    public void update(String name, int val) {
        Data d = safeGet(name);
        d.set(val);
    }

    public void update(String name, double val) {
        Data d = safeGet(name);
        d.set(val);
    }

    public void update(String name, String val) {
        Data d = safeGet(name);
        d.set(val);
    }

    public Set<String> get() {
        return current.keySet();
    }

    protected void load(String name, String serialized) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty name");
            return;
        }
        Data d = current.get(name);
        if (d == null) {
            robot.log(this, "Invalid name: " + name);
            return;
        }
        d.deserialize(serialized);
    }

    protected Data safeGet(String name) {
        Data d = current.get(name);
        if (d == null) {
            d = new Data();
            if (name == null || name.isEmpty()) {
                name = "<NULL>";
            }
            robot.log(this, "Unregistered: " + name);
        }
        return d;
    }

    protected boolean isUpdated(String name) {
        Data c = current.get(name);
        Data o = orig.get(name);
        if (c == null || o == null) {
            robot.log(this, "Unregistered default: " + name);
            return false;
        }
        return o.equals(c);
    }
}
