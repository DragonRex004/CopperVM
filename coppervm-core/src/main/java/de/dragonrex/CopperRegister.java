package de.dragonrex;

public class CopperRegister {
    private String name;
    private int value;

    public CopperRegister(String name) {
        this.name = name;
        this.value = 0;
    }

    public void store(int value) {
        this.value = value;
    }

    public int load() {
        return this.value;
    }

    public String getName() {
        return name;
    }
}
