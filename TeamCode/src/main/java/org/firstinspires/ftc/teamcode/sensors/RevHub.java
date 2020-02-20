package org.firstinspires.ftc.teamcode.sensors;

import com.qualcomm.hardware.lynx.LynxModule;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.TempUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.storage.globals.Globals;
import org.firstinspires.ftc.teamcode.storage.globals.GlobalsPoll;

import java.util.ArrayList;
import java.util.List;

public class RevHub implements Sensors, GlobalsPoll {
    private List<LynxModule> hubs;
    private static final LynxModule.BulkCachingMode MODE_DEFAULT = LynxModule.BulkCachingMode.AUTO;

    /**
     * Find all connected RevHubs
     * Automatically set them to the default comms mode
     */
    public RevHub() {
        try {
            hubs = Robot.O.hardwareMap.getAll(LynxModule.class);
        } catch (Exception e) {
            hubs = new ArrayList<>();
            Robot.err(this, "Unable to find RevHubs");
        }
        mode(null);
        Robot.R.G.register(this);
    }

    /**
     * Get all connected RevHubs
     *
     * @return List<LynxModule> of all connected hubs
     */
    public List<LynxModule> hubs() {
        return new ArrayList<>(hubs);
    }

    /**
     * Set the caching mode for connected hubs
     *
     * @param mode The desired bulk caching mode, can be null
     */
    public void mode(LynxModule.BulkCachingMode mode) {
        if (mode == null) {
            mode = MODE_DEFAULT;
        }
        for (LynxModule module : hubs) {
            module.setBulkCachingMode(mode);
        }
    }

    /**
     * Invalidate the comms cache, to ensure new fetches
     */
    public void clear() {
        for (LynxModule module : hubs) {
            module.clearBulkCache();
        }
    }

    /**
     * Publish voltages and currents and temperatures from the attached RevHubs
     *
     * @param g The Globals object calling this method
     */
    public void gPoll(Globals g) {
        double volts = 0.0d;
        double amps = 0.0d;
        for (LynxModule m : hubs) {
            String name = m.getDeviceName();

            double v = m.getInputVoltage(VoltageUnit.VOLTS);
            g.set("HUB_VOLTAGE_" + name, v);
            volts += v;

            double a = m.getCurrent(CurrentUnit.AMPS);
            g.set("HUB_CURRENT_" + name, a);
            amps += a;

            g.set("HUB_GPIO_CURRENT" + name,
                    m.getGpioBusCurrent(CurrentUnit.AMPS));
            g.set("HUB_I2C_CURRENT_" + name,
                    m.getI2cBusCurrent(CurrentUnit.AMPS));
            g.set("HUB_AUX_VOLTAGE_" + name,
                    m.getAuxiliaryVoltage(VoltageUnit.VOLTS));
            g.set("HUB_TEMP" + name,
                    m.getTemperature(TempUnit.CELSIUS));
        }
        g.set("BATTERY_VOLTAGE", volts);
        g.set("BATTERY_CURRENT", amps);
    }

    /**
     * Is this sensor ready to provide data
     *
     * @return True if ready
     */
    public boolean ready() {
        return (hubs != null);
    }
}
