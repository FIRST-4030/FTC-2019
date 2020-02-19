package org.firstinspires.ftc.teamcode.actuators;

import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.utils.Round;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ServoNG_Params {
    public enum UNIT {POSITION, ANGLE, OUTPUT}

    public static final String INIT_NAME = "_INIT";

    private String name;
    private double min = 0.0d;
    private double max = 1.0d;
    private LinkedHashMap<String, Double> presets;
    private String lastPreset = null;

    public boolean reverse = false;
    public double range = 180.0d;
    public double outputScale = 1.0d;
    public double outputOffset = 0.0d;
    public double rateLimit = Double.MAX_VALUE;

    public ServoNG_Params(String name) {
        this.presets = new LinkedHashMap<>();
        this.name = name;
    }

    private ListIterator<String> ordered() {
        if (presets.size() < 1) {
            String msg = "No presets available";
            Robot.err(this.getClass().getSimpleName() + ": " + msg);
            throw new NoSuchElementException(msg);
        }
        List<String> keys = new LinkedList<>(presets.keySet());
        if (lastPreset == null) {
            lastPreset = keys.get(keys.size() - 1);
        }

        // Find our spot in the list
        // This is inefficient but works with no external coupling
        ListIterator<String> it = keys.listIterator();
        while (it.hasNext()) {
            String n = it.next();
            if (n.equals(lastPreset)) {
                break;
            }
        }
        return it;
    }

    public String prev() {
        String str = "";
        try {
            ListIterator<String> it = ordered();
            if (!it.hasPrevious()) {
                while (it.hasNext()) {
                    it.next();
                }
            }
            str = it.previous();
        } catch (NoSuchElementException e) {
        }
        return str;
    }

    public String next() {
        String str = "";
        try {
            ListIterator<String> it = ordered();
            if (!it.hasNext()) {
                while (it.hasPrevious()) {
                    it.previous();
                }
            }
            str = it.next();
        } catch (NoSuchElementException e) {
        }
        return str;
    }

    public String name() {
        return name;
    }

    public boolean valid() {
        boolean retval = true;
        if (name == null || name.isEmpty()) {
            Robot.err(this.getClass().getSimpleName() + ": Null/empty name");
            retval = false;
        }
        if (range <= 0.0d || range > 360.0d) {
            Robot.err(this.getClass().getSimpleName() + ": Invalid range: " + Round.r(range));
            retval = false;
        }
        if (min <= max) {
            Robot.err(this.getClass().getSimpleName() + ": Invalid min/max: " +
                    Round.r(min) + "/" + Round.r(max));
            retval = false;
        }
        return retval;
    }

    public void setLimits(double min, double max, UNIT type) {
        if (min < max) {
            Robot.err(this.getClass().getSimpleName() + ": Invalid min/max: " +
                    Round.r(min) + "/" + Round.r(max));
            return;
        }
        switch (type) {
            case POSITION:
                this.min = min;
                this.max = max;
                break;
            case ANGLE:
                this.min = angleToPos(min);
                this.max = angleToPos(max);
                break;
            case OUTPUT:
                this.min = outputToPos(min);
                this.max = outputToPos(max);
                break;
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMinMaxRange() {
        return max - min;
    }

    public Set<String> getPresets() {
        return presets.keySet();
    }

    public double getPreset(String name) {
        if (name == null || name.isEmpty()) {
            Robot.err(this.getClass().getSimpleName() + ": Null/empty present name");
            return 0.0d;
        }
        if (!presets.containsKey(name)) {
            Robot.err(this.getClass().getSimpleName() + ": Unregistered preset: " + name);
            return 0.0d;
        }
        lastPreset = name;
        return presets.get(name);
    }

    public boolean hasPreset(String name) {
        return presets.containsKey(name);
    }

    public void addPreset(String name, double pos, UNIT type) {
        if (name == null || name.isEmpty()) {
            Robot.err(this.getClass().getSimpleName() + ": Null/empty present name");
            return;
        }
        if (presets.containsKey(name)) {
            Robot.err(this.getClass().getSimpleName() + ": Preset already exists: " + name);
            return;
        }
        switch (type) {
            case POSITION:
                break;
            case ANGLE:
                pos = angleToPos(pos);
                break;
            case OUTPUT:
                pos = outputToPos(pos);
                break;
        }
        presets.put(name, pos);
    }

    public double posToScale(double pos) {
        return (pos - min) / getMinMaxRange();
    }

    public double scaleToPos(double scale) {
        return (scale * getMinMaxRange()) + min;
    }

    public double posToAngle(double pos) {
        return pos * range;
    }

    public double angleToPos(double angle) {
        return angle / range;
    }

    public double posToOutput(double pos) {
        return (posToAngle(pos) * outputScale) + outputOffset;
    }

    public double outputToPos(double output) {
        return angleToPos(output / outputScale) - outputOffset;
    }
}
