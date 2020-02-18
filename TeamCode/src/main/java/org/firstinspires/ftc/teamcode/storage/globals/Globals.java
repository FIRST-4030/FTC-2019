package org.firstinspires.ftc.teamcode.storage.globals;

import org.firstinspires.ftc.teamcode.storage.anytype.AnyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Globals {
    private final HashMap<String, AnyType> globals;
    private final ArrayList<GlobalsPoll> pollers;

    /**
     * HashMap to store named global variables
     * <p>
     * This class is intended to allow sensors, actuators, and other classes and devices to store
     * variables in a globally-accessible namespace.
     * <p>
     * New variables are automatically created on set/get, initialized as AnyType.unset
     */
    public Globals() {
        globals = new HashMap();
        pollers = new ArrayList();
    }

    /**
     * Poll all registered classes
     */
    public void poll() {
        for (GlobalsPoll p : pollers) {
            p.gPoll(this);
        }
    }

    /**
     * Get a list of all items in Globals
     *
     * @return Set<String> of all currently known Globals
     */
    public Set<String> list() {
        return globals.keySet();
    }

    /**
     * Allow classes to register themselves for Globals callbacks
     * <p>
     * Registered classes will be polled to register data into the Globals namespace.
     * This is intended to support once-per-loop updates to polled data (e.g. updates from RevHub)
     *
     * @param p Typically "this" is sufficient
     */
    public void register(GlobalsPoll p) {
        pollers.add(p);
    }

    /**
     * Does a variable exist in the Globals namespace?
     *
     * @param name Variable name
     * @return True if the named variable exists
     */
    public boolean exists(String name) {
        return globals.containsKey(name);
    }

    /**
     * Get the requested variable
     *
     * @param name Variable name
     * @return AnyType containing the variable
     */
    public AnyType any(String name) {
        AnyType a = globals.get(name);
        if (a == null) {
            a = new AnyType();
            globals.put(name, a);
        }
        return a;
    }

    /**
     * Get the requested variable
     *
     * @param name Variable name
     * @return boolean containing the variable
     */
    public boolean b(String name) {
        return any(name).b;
    }

    /**
     * Get the requested variable
     *
     * @param name Variable name
     * @return int containing the variable
     */
    public int i(String name) {
        return any(name).i;
    }

    /**
     * Get the requested variable
     *
     * @param name Variable name
     * @return double containing the variable
     */
    public double d(String name) {
        return any(name).d;
    }

    /**
     * Get the requested variable
     *
     * @param name Variable name
     * @return String containing the variable
     */
    public String s(String name) {
        return any(name).s;
    }

    /**
     * Set the named variable according to an existing AnyType
     *
     * @param name Variable name
     * @param any  Data to copy
     */
    public void set(String name, AnyType any) {
        AnyType a = any(name);
        a.set(any);
    }

    /**
     * Set the named variable according to an existing boolean
     *
     * @param name Variable name
     * @param b    Data to copy
     */
    public void set(String name, boolean b) {
        AnyType a = any(name);
        a.set(b);
    }

    /**
     * Set the named variable according to an existing int
     *
     * @param name Variable name
     * @param i    Data to copy
     */
    public void set(String name, int i) {
        AnyType a = any(name);
        a.set(i);
    }

    /**
     * Set the named variable according to an existing double
     *
     * @param name Variable name
     * @param d    Data to copy
     */
    public void set(String name, double d) {
        AnyType a = any(name);
        a.set(d);
    }

    /**
     * Set the named variable according to an existing String
     *
     * @param name Variable name
     * @param s    Data to copy
     */
    public void set(String name, String s) {
        AnyType a = any(name);
        a.set(s);
    }
}
