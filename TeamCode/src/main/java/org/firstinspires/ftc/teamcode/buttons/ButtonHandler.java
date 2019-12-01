package org.firstinspires.ftc.teamcode.buttons;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotNG;
import org.firstinspires.ftc.teamcode.robot.Robot;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ButtonHandler {
    private final static float AXIS_THRESHOLD = 0.4f;

    private final HashMap<String, PadButton> buttons;
    private final Telemetry telemetry;
    public final SpinnerHandler spinners;

    public ButtonHandler(Robot robot) {
        this(robot.telemetry);
    }

    public ButtonHandler(RobotNG robot) {
        this(robot.opMode.telemetry);
    }

    public ButtonHandler(Telemetry telemetry) {
        if (telemetry == null) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + ": Null Telemetry");
        }
        this.buttons = new HashMap<>();
        this.telemetry = telemetry;
        this.spinners = new SpinnerHandler(this, telemetry);
    }

    public void register(String name, Gamepad gamepad, PAD_BUTTON button) {
        this.register(name, gamepad, button, BUTTON_TYPE.SINGLE_PRESS);
    }

    // Register a button for consolidated updates
    public void register(String name, Gamepad gamepad, PAD_BUTTON button, BUTTON_TYPE type) {

        // Die hard if we're used poorly
        if (name == null || gamepad == null || button == null) {
            throw new NullPointerException("Null or empty name, gamepad or button");
        }

        // Ensure the named button exists, as the compiler can't check
        try {
            Field field = gamepad.getClass().getField(button.name());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Invalid button: " + button);
        }

        // Create a button of the appropriate type
        PadButton b = new PadButton(gamepad, button);
        switch (type) {
            case SINGLE_PRESS:
                b.listener = new SinglePress();
                break;
            case TOGGLE:
                b.listener = new Toggle();
                break;
        }

        // Register it in the map
        if (buttons.containsKey(name)) {
            telemetry.log().add("De-registering existing button: " + name);
        }
        buttons.put(name, b);
    }

    // Return the underlying PadButton for external tweaks
    public Button getListener(String name) {
        PadButton button = buttons.get(name);
        if (button == null) {
            telemetry.log().add("Unregistered listener: " + name);
            return null;
        }
        return button.listener;
    }

    // Remove named button from the handler
    public void deregister(String name) {
        buttons.remove(name);
    }

    // Update stored state for all buttons, typically called once per OpMode loop()
    // Call handle for all registered ButtonHandlerListeners
    public void update() {
        for (String name : buttons.keySet()) {
            PadButton b = buttons.get(name);
            if (b != null) {
                b.listener.update(read(b));
            }
        }
        spinners.handle();
    }

    // Directly read the underlying button state
    private boolean read(PadButton b) {
        boolean pressed = false;
        try {
            Field field = b.gamepad.getClass().getField(b.button.name());
            if (boolean.class.isAssignableFrom((field.getType()))) {
                pressed = field.getBoolean(b.gamepad);
            } else {
                pressed = (Math.abs(field.getFloat(b.gamepad)) >= AXIS_THRESHOLD);
            }
        } catch (Exception e) {
            // We checked this when registering so this shouldn't happen, but log if it does
            telemetry.log().add("Unable to read button: " + b.button);
        }
        return pressed;
    }

    // Live state of the button
    public boolean raw(String name) {
        PadButton padButton = buttons.get(name);
        if (padButton == null) {
            telemetry.log().add("Unregistered padButton name: " + name);
            return false;
        }
        return read(padButton);
    }

    // Stored state of the button
    public boolean get(String name) {
        return get(name, "active");
    }

    // Stored held state of the button
    public boolean held(String name) {
        return get(name, "held");
    }

    // Stored held state of the button, delayed
    public boolean heldLong(String name) {
        return get(name, "heldLong");
    }

    // Stored held state, repeated periodically
    public boolean autokey(String name) {
        return get(name, "autokey");
    }

    // Reflection-based implementation of the above stubs
    private boolean get(String name, String type) {
        PadButton button = buttons.get(name);
        if (button == null) {
            telemetry.log().add("Unregistered button name: " + name);
            return false;
        }

        Method method;
        try {
            method = button.listener.getClass().getMethod(type);
        } catch (NoSuchMethodException e) {
            telemetry.log().add("Invalid button request type: " + type);
            return false;
        }

        boolean value;
        try {
            value = (boolean) method.invoke(button.listener);
        } catch (IllegalAccessException | InvocationTargetException e) {
            telemetry.log().add("Unable to invoke button listener for type: " + type);
            return false;
        }
        return value;
    }

    private class PadButton {
        public final Gamepad gamepad;
        public final PAD_BUTTON button;
        public Button listener;

        public PadButton(Gamepad gamepad, PAD_BUTTON button) {
            this.gamepad = gamepad;
            this.button = button;
        }
    }
}
