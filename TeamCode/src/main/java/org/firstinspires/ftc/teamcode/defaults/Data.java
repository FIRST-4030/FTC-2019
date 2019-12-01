package org.firstinspires.ftc.teamcode.defaults;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Data {
    public boolean b = false;
    public int i = 0;
    public double d = 0.0d;
    public String s = "";
    public DataTypes type = DataTypes.UNSET;

    public Data() {
    }

    public Data(boolean val) {
        set(val);
    }

    public Data(int val) {
        set(val);
    }

    public Data(double val) {
        set(val);
    }

    public Data(String val) {
        set(val);
    }

    public Data dup() {
        Data d = new Data();
        d.b = this.b;
        d.i = this.i;
        d.d = this.d;
        d.s = this.s;
        d.type = this.type;
        return d;
    }

    public boolean isUnset() {
        return type == DataTypes.UNSET;
    }

    public void set(boolean b) {
        this.type = DataTypes.BOOLEAN;
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
        this.type = DataTypes.INT;
        this.b = i != 0;
        this.i = i;
        this.d = (double) i;
        this.s = Integer.toString(i);
    }

    public void set(double d) {
        this.type = DataTypes.DOUBLE;
        this.b = d != 0;
        this.i = (int) d;
        this.d = d;
        this.s = Double.toString(d);
    }

    public void set(String s) {
        this.type = DataTypes.STRING;
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

    public void deserialize(String str) {
        // Strings expected in the form of <type>:<uri_encoded_value>
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ":" +
                    "Null/empty string");
        }
        if (str.length() < 2) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ":" +
                    "Invalid length (" + str.length() + ") in: " + str);
        }
        if (!str.substring(1, 1).equals(":")) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ":" +
                    "Invalid delimiter in: " + str);
        }
        DataTypes t;
        switch (str.substring(0, 1)) {
            case "b":
                t = DataTypes.BOOLEAN;
                break;
            case "i":
                t = DataTypes.INT;
                break;
            case "d":
                t = DataTypes.DOUBLE;
                break;
            case "s":
                t = DataTypes.STRING;
                break;
            default:
                throw new IllegalArgumentException(this.getClass().getSimpleName() + ":" +
                        "Invalid type in: " + str);
        }

        String val;
        // Decode from the URI encoding
        try {
            val = URLDecoder.decode(str.substring(3), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unable to encode with charset: " + StandardCharsets.UTF_8.toString());
        }
        // Parse using the set(String) method since that does parsing
        set(val);
        // Override the type to match the original
        type = t;
    }

    public String serialize() {
        char t = 'u';
        switch (type) {
            case UNSET:
                t = 'u';
                break;
            case BOOLEAN:
                t = 'b';
                break;
            case INT:
                t = 'i';
                break;
            case DOUBLE:
                t = 'd';
                break;
            case STRING:
                t = 'e';
                break;
        }

        // URI encode to ensure we don't have output newlines or other odd bits
        String val;
        try {
            val = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unable to encode with charset: " + StandardCharsets.UTF_8.toString());
        }
        return t + ":" + val;
    }
}
