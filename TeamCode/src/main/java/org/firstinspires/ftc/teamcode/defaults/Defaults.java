package org.firstinspires.ftc.teamcode.defaults;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.robot.config.BOTS;

import java.util.HashMap;

public class Defaults {
    private boolean locked;
    private final RobotNG robot;
    private final HashMap<String, Default> defaults;

    public Defaults(RobotNG robot) {
        if (robot == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + ": " +
                    "Robot not available");
        }
        defaults = new HashMap<>();
        for (BOTS bot : BOTS.values()) {
            defaults.put(bot.serial(), new Default());
        }
        this.locked = false;
        this.robot = robot;
    }

    private void load() {
        // TODO: Load overrides from disk
    }

    private void save() {
        // TODO: Save to disk
    }

    public void lock() {
        locked = true;
        load();
        save();
    }

    private Default getDefault(BOTS bot) {
        Default d = null;
        if (locked) {
            robot.log(this, "Registration locked");
            d = new Default(robot);
        }
        if (d == null) {
            d = defaults.get(bot.serial());
        }
        if (d == null) {
            robot.log(this, "Unregistered bot: " + bot);
            d = new Default(robot);
        }
        return d;
    }

    private Data getData(String name, BOTS bot) {
        Default d = defaults.get(bot.serial());
        if (!d.exists(name)) {
            d = defaults.get(BOTS.NONE.serial());
        }
        return d.safeGet(name).dup();
    }

    public boolean register(String name, boolean val) {
        return register(name, val, BOTS.NONE);
    }

    public boolean register(String name, int val) {
        return register(name, val, BOTS.NONE);
    }

    public boolean register(String name, double val) {
        return register(name, val, BOTS.NONE);
    }

    public boolean register(String name, String val) {
        return register(name, val, BOTS.NONE);
    }

    public boolean register(String name, boolean val, BOTS bot) {
        return getDefault(bot).register(name, val);
    }

    public boolean register(String name, int val, BOTS bot) {
        return getDefault(bot).register(name, val);
    }

    public boolean register(String name, double val, BOTS bot) {
        return getDefault(bot).register(name, val);
    }

    public boolean register(String name, String val, BOTS bot) {
        return getDefault(bot).register(name, val);
    }

    public boolean getB(String name, BOTS bot) {
        return getData(name, bot).b;
    }

    public int getI(String name, BOTS bot) {
        return getData(name, bot).i;
    }

    public float getF(String name, BOTS bot) {
        return (float) getData(name, bot).d;
    }

    public double getD(String name, BOTS bot) {
        return getData(name, bot).d;
    }

    public String getS(String name, BOTS bot) {
        return getData(name, bot).s;
    }

    public boolean getB(String name) {
        return getB(name, robot.bot);
    }

    public int getI(String name) {
        return getI(name, robot.bot);
    }

    public float getF(String name) {
        return getF(name, robot.bot);
    }

    public double getD(String name) {
        return getD(name, robot.bot);
    }

    public String getS(String name) {
        return getS(name, robot.bot);
    }
}
