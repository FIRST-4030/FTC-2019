package org.firstinspires.ftc.teamcode.defaults;

import android.os.Environment;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.robot.config.BOTS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Defaults {
    private boolean locked;
    private final RobotNG robot;
    private final HashMap<String, Default> defaults;

    // TODO: Put this in the FTC folder?
    public static final String SAVE_DIR_DEFAULT = Environment.DIRECTORY_DOWNLOADS;
    public static final String filename = "defaults.txt";

    private class DefaultsData {
        BOTS b = null;
        String name = null;
        String d = null;

        boolean valid() {
            return (b != null || name != null && d != null);
        }
    }

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

    private File file() {
        File f = null;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(SAVE_DIR_DEFAULT);
            AppUtil.getInstance().ensureDirectoryExists(dir);
            f = new File(dir, filename);
        } catch (Exception e) {
            robot.log(this, "Unable to find file: " + filename);
        }
        return f;
    }

    private void load() {
        try {
            FileReader fr = new FileReader(file());
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                DefaultsData d = deserialize(line);
                if (d.valid()) {
                    Default b = defaults.get(d.b.serial());
                    if (b == null) {
                        robot.log(this, "Invalid bot: " + d.b);
                        continue;
                    }
                    b.load(d.name, d.d);
                }
            }
            fr.close();
        } catch (Exception e) {
            robot.log(this, "Unable to read input file");
        }
    }

    private void save() {
        // Get all keys from all bots into a single set
        Set<String> keys = new HashSet<>();
        for (String b : defaults.keySet()) {
            Default d = defaults.get(b);
            if (d == null) {
                throw new IllegalStateException("No Defaults for bot: " + b);
            }
            keys.addAll(d.get());
        }

        // Sort by key name
        LinkedList<String> sorted = new LinkedList<>(keys);
        Collections.sort(sorted);

        try {
            FileWriter fw = new FileWriter(file());
            PrintWriter pw = new PrintWriter(fw);

            // Write each key out, for each bot where it exists
            for (String key : sorted) {
                for (BOTS b : BOTS.values()) {
                    Default d = defaults.get(b.serial());
                    if (d == null) {
                        robot.log(this, "Invalid bot: " + b);
                        continue;
                    }
                    if (d.exists(key)) {
                        pw.println(serialize(key, b));
                    }
                }
            }

            fw.close();
        } catch (Exception e) {
            robot.log(this, "Unable to write input file");
        }
    }

    public void lock() {
        locked = true;
        load();
        save();
    }

    private DefaultsData deserialize(String str) {
        DefaultsData d = new DefaultsData();

        // Skip commented lines
        if (str.indexOf('#') == 0) {
            return d;
        }

        // Sanity check for delimiters
        int bDelim = str.indexOf('@');
        int dDelim = str.indexOf('\t');
        if (bDelim < 1) {
            robot.log(this, "Invalid bot delimiter: " + str);
            return d;
        }
        if (dDelim < 1 || dDelim < bDelim) {
            robot.log(this, "Invalid data delimiter: " + str);
            return d;
        }

        // Parse name
        d.name = str.substring(0, bDelim - 1);

        // Parse bot
        String serial = str.substring(bDelim + 1, dDelim - 1);
        for (BOTS b : BOTS.values()) {
            if (b.serial().equals(serial)) {
                d.b = b;
            }
        }
        if (d.b == null) {
            robot.log(this, "Invalid bot: " + serial);
            return d;
        }

        // Save serialized data
        d.d = str.substring(dDelim + 1);

        // Validate and return
        if (!d.valid()) {
            robot.log(this, "Parsing failure: " + str);
        }
        return d;
    }

    private String serialize(String key, BOTS bot) {
        if (key == null) {
            robot.log(this, "Null/empty name");
            return "";
        }
        if (bot == null) {
            robot.log(this, "Null/empty bot");
            return "";
        }
        Default d = defaults.get(bot.serial());
        if (d == null) {
            robot.log(this, "Invalid bot: " + bot);
            return "";
        }

        // Comment out lines that haven't been changed
        String str = (d.isUpdated(key) ? "" : "#");

        // URI encode to ensure we don't have output newlines or other odd bits
        try {
            String val = URLEncoder.encode(key, StandardCharsets.UTF_8.toString());
            str += val;

            val = URLEncoder.encode(bot.serial(), StandardCharsets.UTF_8.toString());
            str += "@" + val;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unable to encode with charset: " + StandardCharsets.UTF_8.toString());
        }

        // Append the serialized Data value
        return str + "\t" + d.safeGet(key).serialize();
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
        if (d == null || !d.exists(name)) {
            d = defaults.get(BOTS.NONE.serial());
        }
        if (d == null) {
            throw new IllegalStateException("Invalid Defaults state");
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
