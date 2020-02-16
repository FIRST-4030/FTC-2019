package org.firstinspires.ftc.teamcode.utils.anytype;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class AnyType {
    public boolean b = false;
    public int i = 0;
    public double d = 0.0d;
    public String s = "";
    public Types type = Types.UNSET;

    public AnyType() {
        unset();
    }

    public AnyType(boolean val) {
        set(val);
    }

    public AnyType(int val) {
        set(val);
    }

    public AnyType(double val) {
        set(val);
    }

    public AnyType(String val) {
        set(val);
    }

    public AnyType dup() {
        AnyType d = new AnyType();
        d.b = this.b;
        d.i = this.i;
        d.d = this.d;
        d.s = this.s;
        d.type = this.type;
        return d;
    }

    public boolean equals(AnyType c) {
        if (c == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null Data");
        }
        if (type != c.type) {
            return false;
        }
        switch (type) {
            case UNSET:
                return true;
            case BOOLEAN:
                if (b == c.b) {
                    return true;
                }
            case INT:
                if (i == c.i) {
                    return true;
                }
            case DOUBLE:
                if (d == c.d) {
                    return true;
                }
            case STRING:
                if (s.equals(c.s)) {
                    return true;
                }
        }
        return false;
    }

    public boolean isUnset() {
        return (type == Types.UNSET);
    }

    public void unset() {
        set("");
        type = Types.UNSET;
    }

    protected void set(AnyType data) {
        if (data == null) {
            throw new IllegalArgumentException("Null Data");
        }
        type = data.type;
        b = data.b;
        i = data.i;
        d = data.d;
        s = data.s;
    }

    public void set(boolean b) {
        type = Types.BOOLEAN;
        this.b = b;
        if (b) {
            i = 1;
            d = 1.0d;
            s = "true";
        } else {
            i = 0;
            d = 0.0d;
            s = "false";
        }
    }

    public void set(int i) {
        type = Types.INT;
        b = i != 0;
        this.i = i;
        d = (double) i;
        s = Integer.toString(i);
    }

    public void set(double d) {
        type = Types.DOUBLE;
        b = d != 0;
        i = (int) d;
        this.d = d;
        s = Double.toString(d);
    }

    public void set(String s) {
        type = Types.STRING;
        if (s == null) {
            s = "";
        }
        b = !s.isEmpty() &&
                !s.equals("0") &&
                !s.equalsIgnoreCase("false") &&
                !s.equalsIgnoreCase("f");
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            i = 0;
        }
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            d = 0.0d;
        }
        this.s = s;
    }

    public void parseJSON(JSONObject json, String name) {
        // Check for null objects and map them to unset
        if (json.isNull(name)) {
            unset();
            return;
        }
        // Check for boolean objects
        try {
            set(json.getBoolean(name));
        } catch (JSONException ignored) {
        }
        // Default to strings, which should always work
        try {
            set(json.getString(name));
        } catch (JSONException e) {
            Log.w("AnyType", "Invalid item in JSON for element: " + name);
        }
        // Try parsing the string to an int
        try {
            int i = Integer.parseInt(s);
            set(i);
        } catch (NumberFormatException ignored) {
        }
        // Try parsing the string to a double
        try {
            double d = Double.parseDouble(s);
            set(d);
        } catch (NumberFormatException ignored) {
        }
    }
}
