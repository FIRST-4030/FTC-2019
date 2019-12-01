package org.firstinspires.ftc.teamcode.config;

import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.actuators.Actuator;
import org.firstinspires.ftc.teamcode.actuators.Actuators;
import org.firstinspires.ftc.teamcode.debug.Debug;
import org.firstinspires.ftc.teamcode.debug.DeviceProvider;
import org.firstinspires.ftc.teamcode.debug.ModeProvider;
import org.firstinspires.ftc.teamcode.robot.config.HardwareConfig;

import java.util.LinkedList;
import java.util.ListIterator;

public class Hardware implements ModeProvider {
    private final RobotNG robot;
    private final Actuators actuators;

    private Debug D_device = null;
    private DeviceProvider D_provider = null;
    private final LinkedList<DeviceProvider> D_providers;
    private ListIterator<DeviceProvider> D_providerIt = null;

    public Hardware(RobotNG robot) {
        if (robot == null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + ": Robot not available");
        }
        this.robot = robot;

        // Finals
        actuators = new Actuators(robot);
        D_providers = new LinkedList<>();

        // Load config
        HardwareConfig.actuators(robot, actuators);
        // TODO: Sensors

        // Debugger
        debug_init();
    }

    public void stop() {
        for (String name : actuators.get()) {
            Actuator a = actuators.get(name);
            if (a != null) {
                a.stop();
            }
        }
    }

    private void debug_init() {
        D_providers.add(actuators);
        // TODO: Sensors
        D_providerIt = D_providers.listIterator();
        D_provider = D_providerIt.next();
        D_device = D_provider.debug_next();
    }

    @Override
    public void debug_start(RobotNG robot) {
        if (D_provider == null) {
            D_provider = D_providers.getFirst();
        }
        if (D_device == null) {
            D_device = D_provider.debug_next();
            D_device.debug_init(robot);
        }
    }

    @Override
    public void debug_next() {
        D_device.debug_destroy();
        D_device = D_provider.debug_next();
        D_device.debug_init(robot);
    }

    @Override
    public void debug_prev() {
        D_device.debug_destroy();
        D_device = D_provider.debug_prev();
        D_device.debug_init(robot);
    }

    @Override
    public void debug_mode_next() {
        D_device.debug_destroy();
        if (!D_providerIt.hasNext()) {
            D_providerIt = D_providers.listIterator();
        }
        D_provider = D_providerIt.next();
        D_device = D_provider.debug_next();
        D_device.debug_init(robot);
    }

    @Override
    public void debug_mode_prev() {
        D_device.debug_destroy();
        if (!D_providerIt.hasPrevious()) {
            D_providerIt = D_providers.listIterator(D_providers.size() - 1);
        }
        D_provider = D_providerIt.previous();
        D_device = D_provider.debug_next();
        D_device.debug_init(robot);
    }

    @Override
    public void debug_loop() {
        if (D_device != null) {
            D_device.debug_loop();
        }
    }

    @Override
    public void debug_end() {
        D_device.debug_destroy();
        D_device = null;
    }
}
