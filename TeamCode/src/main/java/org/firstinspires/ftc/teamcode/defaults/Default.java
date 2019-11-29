package org.firstinspires.ftc.teamcode.defaults;

import org.firstinspires.ftc.teamcode.RobotNG;

import java.util.HashMap;

public class Default {
    private class DefaultData {
        public boolean b = false;
        public int i = 0;
        public double d = 0.0d;
        public String s = "";

        public DefaultData() {
        }

        public DefaultData(boolean val) {
            set(val);
        }

        public DefaultData(int val) {
            set(val);
        }

        public DefaultData(double val) {
            set(val);
        }

        public DefaultData(String val) {
            set(val);
        }

        public DefaultData dup() {
            DefaultData d = new DefaultData();
            d.b = this.b;
            d.i = this.i;
            d.d = this.d;
            d.s = this.s;
            return d;
        }

        public void set(boolean b) {
            this.b = b;
            if (b) {
                this.i = 1;
                this.d = 1.0d;
                this.s = "true";
            } else {
                this.i = 0;
                this.d = 0.0d;
                this.s = "false";
            }
        }

        public void set(int i) {
            this.b = i != 0;
            this.i = i;
            this.d = (double) i;
            this.s = Integer.toString(i);
        }

        public void set(double d) {
            this.b = d != 0;
            this.i = (int) d;
            this.d = d;
            this.s = Double.toString(d);
        }

        public void set(String s) {
            if (s == null) {
                s = "";
            }
            this.b = !s.isEmpty() &&
                    !s.equals("0") &&
                    !s.equalsIgnoreCase("false") &&
                    !s.equalsIgnoreCase("f");
            try {
                this.i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                this.i = 0;
            }
            try {
                this.d = Double.parseDouble(s);
            } catch (NumberFormatException e) {
                this.d = 0.0d;
            }
        }
    }

    private final RobotNG robot;
    private final HashMap<String, DefaultData> orig;
    private final HashMap<String, DefaultData> current;

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

    private boolean register(String name, DefaultData d) {
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
        DefaultData d = new DefaultData(val);
        return register(name, d);
    }

    public boolean register(String name, int val) {
        DefaultData d = new DefaultData(val);
        return register(name, d);
    }

    public boolean register(String name, double val) {
        DefaultData d = new DefaultData(val);
        return register(name, d);
    }

    public boolean register(String name, String val) {
        DefaultData d = new DefaultData(val);
        return register(name, d);
    }

    public void update(String name, boolean val) {
        DefaultData d = safeGet(name);
        d.set(val);
    }

    public void update(String name, int val) {
        DefaultData d = safeGet(name);
        d.set(val);
    }

    public void update(String name, double val) {
        DefaultData d = safeGet(name);
        d.set(val);
    }

    public void update(String name, String val) {
        DefaultData d = safeGet(name);
        d.set(val);
    }

    private DefaultData safeGet(String name) {
        DefaultData d = current.get(name);
        if (d == null) {
            d = new DefaultData();
            if (name == null || name.isEmpty()) {
                name = "<NULL>";
            }
            robot.log(this, "Unregistered: " + name);
        }
        return d;
    }

    public boolean getB(String name) {
        return safeGet(name).b;
    }

    public int getI(String name) {
        return safeGet(name).i;
    }

    public float getF(String name) {
        return (float) safeGet(name).d;
    }

    public double getD(String name) {
        return safeGet(name).d;
    }

    public String getS(String name) {
        return safeGet(name).s;
    }
}
