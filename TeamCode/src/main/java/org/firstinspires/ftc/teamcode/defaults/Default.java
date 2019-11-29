package org.firstinspires.ftc.teamcode.defaults;

import org.firstinspires.ftc.teamcode.RobotNG;

import java.util.HashMap;

public class Default {
    public class DefaultData {
        public boolean b;
        public int i;
        public double d;
        public String s;

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
            this.b = i == 0 ? false : true;
            this.i = i;
            this.d = (double) i;
            this.s = Integer.toString(i);
        }

        public void set(double d) {
            this.b = d == 0 ? false : true;
            this.i = (int) d;
            this.d = d;
            this.s = Double.toString(d);
        }

        public void set(String s) {
            if (s == null) {
                s = new String();
            }
            if (s.isEmpty() ||
                    s.equals("0") ||
                    s.equalsIgnoreCase("false") ||
                    s.equalsIgnoreCase("f")) {
                this.b = false;
            } else {
                this.b = true;
            }
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
    private final HashMap<String, DefaultData> m;

    public Default(RobotNG robot) {
        if (robot == null) {
            throw new IllegalStateException("Robot not available");
        }
        this.robot = robot;
        m = new HashMap<>();
    }

    public Default() {
        this(RobotNG.R);
    }

    public boolean register(String name, boolean val) {
        if (!putValidator(name)) {
            return false;
        }
        DefaultData d = new DefaultData();
        d.set(val);
        m.put(name, d);
        return true;
    }

    public boolean register(String name, int val) {
        if (!putValidator(name)) {
            return false;
        }
        DefaultData d = new DefaultData();
        d.set(val);
        m.put(name, d);
        return true;
    }

    public boolean register(String name, double val) {
        if (!putValidator(name)) {
            return false;
        }
        DefaultData d = new DefaultData();
        d.set(val);
        m.put(name, d);
        return true;
    }

    public boolean register(String name, String val) {
        if (!putValidator(name)) {
            return false;
        }
        DefaultData d = new DefaultData();
        d.set(val);
        m.put(name, d);
        return true;
    }

    private boolean putValidator(String name) {
        if (name == null || name.isEmpty()) {
            robot.log(this, "Null/empty name");
            return false;
        }
        if (m.containsKey(name)) {
            robot.log(this, "Already registered: " + name);
            return false;
        }
        return true;
    }

    private boolean getValidator(String name) {
        if (!m.containsKey(name)) {
            if (name == null || name.isEmpty()) {
                name = "<NULL>";
            }
            robot.log(this, "Unregistered: " + name);
            return false;
        }
        return true;
    }

    public boolean getB(String name) {
        if (!getValidator(name)) {
            return true;
        }
        return m.get(name).b;
    }

    public int getI(String name) {
        if (!getValidator(name)) {
            return 0;
        }
        return m.get(name).i;
    }

    public float getF(String name) {
        if (!getValidator(name)) {
            return 0.0f;
        }
        return (float) m.get(name).d;
    }

    public double getD(String name) {
        if (!getValidator(name)) {
            return 0.0d;
        }
        return m.get(name).d;
    }

    public String getS(String name) {
        if (!getValidator(name)) {
            return new String();
        }
        return m.get(name).s;
    }
}
