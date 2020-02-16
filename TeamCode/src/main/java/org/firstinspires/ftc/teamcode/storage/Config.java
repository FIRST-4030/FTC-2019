package org.firstinspires.ftc.teamcode.storage;

import android.os.Environment;

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
    public static final String FTC_PATH = "FTC";
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
    public String readFile(String name) {
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
            Robot.R.err(this.getClass().getSimpleName() +
                    ": Unable to read config file: " + name);
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
                if (len == 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception e) {
            Robot.R.warn(this.getClass().getSimpleName() + ": Unable to copy defaults");
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
            Robot.R.err("Invalid JSON: " + e.getLocalizedMessage());
            return;
        }

        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String cls = keysItr.next();
            JSONObject value;
            try {
                value = json.getJSONObject(cls);
            } catch (JSONException e) {
                Robot.R.warn("Invalid class in JSON: " +
                        cls + ": " + e.getLocalizedMessage());
                continue;
            }
            config.put(cls, parseClass(value, cls));
        }
    }

    /**
     * Parse a set of JSON elements from a JSON class configuration
     *
     * @param json JSON object a class-level configuration
     * @return The set of classes parsed from the input object, if any
     * @throws JSONException
     */
    public HashMap<String, HashMap> parseClass(JSONObject json, String jsonPath) {
        HashMap<String, HashMap> cls = new HashMap<>();
        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String element = keysItr.next();
            JSONObject value;
            try {
                value = json.getJSONObject(element);
            } catch (JSONException e) {
                Robot.R.warn("Invalid device in JSON: " +
                        element + ": " + e.getLocalizedMessage());
                continue;
            }
            config.put(element, parseDevice(value, jsonPath + "::" + element));
        }
        return cls;
    }

    /**
     * Parse a set of AnyType objects from JSON configuration elements
     *
     * @param json JSON object containing a device-level configuration
     * @return The set of elements parsed from the input object, if any
     */
    public HashMap<String, AnyType> parseDevice(JSONObject json, String jsonPath) {
        HashMap<String, AnyType> data = new HashMap<>();
        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String name = keysItr.next();

            // Parse weak JSON types into AnyType
            AnyType item = new AnyType();
            data.put(name, item);

            // Check for null objects and map them to unset
            if (json.isNull(name)) {
                item.unset();
                continue;
            }
            // Check for boolean objects
            try {
                item.set(json.getBoolean(name));
                continue;
            } catch (JSONException e) {
            }
            // Default to strings, which should always work
            try {
                item.set(json.getString(name));
            } catch (JSONException e) {
                Robot.R.warn("Invalid item in JSON: " + jsonPath + "::" + name);
                continue;
            }
            // Try parsing the string to an int
            try {
                int i = Integer.parseInt(item.s);
                item.set(i);
                continue;
            } catch (NumberFormatException e) {
            }
            // Try parsing the string to a double
            try {
                double d = Double.parseDouble(item.s);
                item.set(d);
                continue;
            } catch (NumberFormatException e) {
            }
        }
        return data;
    }
}
