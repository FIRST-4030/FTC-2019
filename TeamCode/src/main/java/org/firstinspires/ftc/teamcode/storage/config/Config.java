package org.firstinspires.ftc.teamcode.storage.config;

import android.os.Environment;
import android.util.Log;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.utils.anytype.AnyType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

public class Config {
    public static final String FTC_PATH = "FIRST";
    public static final String DEFAULTS_NAME = "defaults.json";
    public static final String OVERRIDE_NAME = "override.json";
    public static final int resource = R.raw.defaults;

    private final HashMap<String, HashMap> config;
    private final File dir;

    public Config() {
        // Make sure our paths are sensible and the default files exist
        dir = Environment.getExternalStoragePublicDirectory(FTC_PATH);
        AppUtil.getInstance().ensureDirectoryExists(dir);
        installDefaults();

        // Load an empty config, add the defaults, add the overrides
        config = new HashMap<>();
        parseConfig(readFile(DEFAULTS_NAME), config);
        parseConfig(readFile(OVERRIDE_NAME), config);
    }

    /**
     * Return the contents of a file as a String
     *
     * @param name The file path, relative to dir
     * @return The contents of the file, if any. Returns an empty string on failure.
     */
    private String readFile(String name) {
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(dir, name);
            if (!file.canRead()) {
                throw new FileNotFoundException(file.getPath());
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            log(this.getClass().getSimpleName() +
                    ": Unable to read file: " + name);
            return sb.toString();
        }
        return sb.toString();
    }

    /**
     * Copy the default JSON config from the App internals to the FTC dir
     * This will overwrite any previous content in the DEFAULTS_NAME file
     * Overrides should be stored in the OVERRIDE_NAME file to avoid loss
     */
    public void installDefaults() {
        try {
            // Find and drop the old defaults file on the phone
            File outFile = new File(dir, DEFAULTS_NAME);
            if (outFile.exists()) {
                outFile.delete();
            }

            // Copy the baked-in defaults to the FTC directory
            InputStream in = AppUtil.getDefContext().getResources().openRawResource(resource);
            OutputStream out = new FileOutputStream(outFile);
            int len;
            byte[] buffer = new byte[4 * 1024];

            while (true) {
                len = in.read(buffer);
                if (len <= 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

            // Ensure the overrides file exists (create an empty one if it doesn't)
            outFile = new File(dir, OVERRIDE_NAME);
            if (!outFile.exists()) {
                out = new FileOutputStream(outFile);
                out.write("{}".getBytes());
                out.close();
            }
        } catch (Exception e) {
            log("Unable to copy defaults: " +
                    e.getClass().getSimpleName() + "::"
                    + e.getLocalizedMessage());
            return;
        }
    }

    /**
     * Parse a JSON config hierarchy into a HashMap by class and device
     *
     * @param s String containing a complete configuration object, encoded as JSON
     * @param c The config map to be updated
     */
    public void parseConfig(String s, HashMap<String, HashMap> c) {
        JSONObject json;
        try {
            json = new JSONObject(s);
        } catch (JSONException e) {
            log("Invalid JSON: " + e.getLocalizedMessage());
            return;
        }

        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String cls = keysItr.next();
            JSONObject value;
            try {
                value = json.getJSONObject(cls);
            } catch (JSONException e) {
                log("Invalid class in JSON: " +
                        cls + ": " + e.getLocalizedMessage());
                continue;
            }
            config.put(cls, parseClass(value));
        }
    }

    /**
     * Parse a set of JSON elements from a JSON class configuration
     *
     * @param json JSON object a class-level configuration
     * @return The set of classes parsed from the input object, if any
     */
    public HashMap<String, HashMap> parseClass(JSONObject json) {
        HashMap<String, HashMap> cls = new HashMap<>();
        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String device = keysItr.next();
            JSONObject value;
            try {
                value = json.getJSONObject(device);
            } catch (JSONException e) {
                log("Invalid device in JSON: " + device +
                        ": " + e.getLocalizedMessage());
                continue;
            }
            cls.put(device, parseDevice(value));
        }
        return cls;
    }

    /**
     * Parse a set of AnyType objects from JSON configuration elements
     *
     * @param json JSON object containing a device-level configuration
     * @return The set of elements parsed from the input object, if any
     */
    public HashMap<String, AnyType> parseDevice(JSONObject json) {
        HashMap<String, AnyType> data = new HashMap<>();
        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String name = keysItr.next();

            // Parse weak JSON types into AnyType
            AnyType item = new AnyType();
            item.parseJSON(json, name);
            data.put(name, item);

            /*
             * Alternatively you could use Strings for all config items and do parsing elsewhere
             *
             * try {
             *     String str = json.getString(name);
             *     data.put(name, str);
             * } catch (JSONException e) {
             *     log("JSON parsing error: " + e.getLocalizedMessage());
             *     continue;
             * }
             */
        }
        return data;
    }

    /**
     * Use the Robot or RobotUtils log facilities when available
     * Log to Android when they are not
     *
     * @param msg Log message
     */
    private void log(String msg) {
        msg = this.getClass().getSimpleName() + ": " + msg;
        if (Robot.R != null) {
            Robot.R.err(msg);
        } else {
            Log.e("Robot", msg);
        }
    }

    /**
     * Find the requested class in the config hierarchy
     * Create it if it does not yet exist
     *
     * @param cls Name of the config class
     * @return HashMap of all devices in the specified class
     */
    protected HashMap<String, HashMap> getCls(String cls) {
        HashMap<String, HashMap> c = config.get(cls);
        if (c == null) {
            c = new HashMap<>();
            config.put(cls, c);
            log("Adding class: " + cls);
        }
        return c;
    }

    /**
     * Wrapper for getCls() that returns a ConfigCls
     *
     * @param cls Name of the config class
     * @return ConfigCls pointer for use in further queries
     */
    public ConfigCls cls(String cls) {
        return new ConfigCls(this, getCls(cls));
    }

    /**
     * Find the requested device in the config hierarchy
     * Create it if it does not yet exist
     *
     * @param c      Pointer to the class being searched
     * @param device Name of the device
     * @return HashMap of all items in the device
     */
    protected HashMap<String, AnyType> device(HashMap<String, HashMap> c, String device) {
        HashMap<String, AnyType> d = c.get(device);
        if (d == null) {
            d = new HashMap<>();
            c.put(device, d);
            log("Adding device: " + device);
        }
        return d;
    }

    /**
     * Wrapper for device() that returns a ConfigDevice
     *
     * @param cls    Name of the config class
     * @param device Name of the config device
     * @return ConfigDevice for use in further queries
     */
    public ConfigDevice device(String cls, String device) {
        return new ConfigDevice(this, device(getCls(cls), device));
    }

    /**
     * Find the requested item in the config hierarchy
     * Create it if it does not yet exist
     *
     * @param d    Pointer to the device being searched
     * @param item Name of the item
     * @return AnyType containing the requested item
     */
    protected AnyType item(HashMap<String, AnyType> d, String item) {
        AnyType i;
        if (d.containsKey(item)) {
            i = d.get(item);
        } else {
            i = new AnyType();
            d.put(item, i);
            log("Adding item: " + item);
        }
        return i;
    }

    /**
     * Wrapper for any() that does all name lookups
     *
     * @param cls    Config class name
     * @param device Config device name
     * @param item   Config item name
     * @return AnyType containing the requested item
     */
    public AnyType item(String cls, String device, String item) {
        return item(device(getCls(cls), device), item);
    }

    /**
     * Determine if the requested item exists in the config hierarchy
     *
     * @param d    Pointer to the device being searched
     * @param item Name of the item
     * @return True if the named item exists
     */
    protected boolean exists(HashMap<String, AnyType> d, String item) {
        return d.containsKey(item);
    }
}
