package org.firstinspires.ftc.teamcode.storage;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.utils.anytype.AnyType;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class Config {
    HashMap<String, HashMap> config = new HashMap<>();

    public HashMap<String, HashMap> parseConfig(String s) throws JSONException {
        HashMap<String, HashMap> c = new HashMap<>();
        JSONObject json;
        json = new JSONObject(s);

        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String cls = keysItr.next();
            Object value = json.get(cls);
            if (!(value instanceof JSONObject)) {
                Robot.R.log("Invalid class in JSON: " + cls);
                return c;
            }
            config.put(cls, parseClass((JSONObject) value));
        }
        return c;
    }

    public HashMap<String, HashMap> parseClass(JSONObject json) throws JSONException {
        HashMap<String, HashMap> cls = new HashMap<>();
        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String element = keysItr.next();
            Object value = json.get(element);
            if (!(value instanceof JSONObject)) {
                Robot.R.log("Invalid element in JSON: " + element);
                return cls;
            }
            config.put(element, parseClass((JSONObject) value));
        }
        return cls;
    }

    public HashMap<String, AnyType> parseElement(JSONObject json) {
        HashMap<String, AnyType> data = new HashMap<>();
        Iterator<String> keysItr = json.keys();
        while (keysItr.hasNext()) {
            String name = keysItr.next();

            // Parse weak JSON types into AnyType
            AnyType item = new AnyType();
            // Check for null objects and map them to unset
            if (json.isNull(name)) {
                item.unset();
                data.put(name, item);
                continue;
            }
            // Check for boolean objects
            try {
                item.set(json.getBoolean(name));
                data.put(name, item);
                continue;
            } catch (JSONException e) {
            }
            // Default to strings, which should always work
            try {
                item.set(json.getString(name));
            } catch (JSONException e) {
                Robot.R.log("Invalid item in JSON: " + name);
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
