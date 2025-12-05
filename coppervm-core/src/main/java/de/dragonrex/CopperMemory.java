package de.dragonrex;

public class CopperMemory {
    private int[] memory;

    public CopperMemory(int size) {
        this.memory = new int[size];
    }

    public int read(int address) {
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }
        return memory[address];
    }

    public void write(int address, int value) {
        if (address < 0 || address >= memory.length) {
            throw new IllegalArgumentException("Invalid memory address: " + address);
        }
        memory[address] = value;
    }
}
