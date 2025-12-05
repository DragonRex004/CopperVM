package de.dragonrex;

public class CopperProgCounter {
    private int counter;

    public CopperProgCounter() {
        counter = 0;
    }

    public void jump(int value) {
        counter = value;
    }

    public void inc() {
        counter++;
    }

    public void dec() {
        counter--;
    }

    public int get() {
        return counter;
    }
}
