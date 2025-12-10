package de.dragonrex;

import java.util.HashMap;
import java.util.Map;

public class CopperVM {
    private CopperStack stack;
    private CopperAlu alu;
    private CopperMemory memory;
    private CopperFlagRegister flags;
    private CopperProgCounter pc;

    private Map<String, CopperRegister> registers;

    public CopperVM() {
        this.flags = new CopperFlagRegister();
        this.stack = new CopperStack();
        this.alu = new CopperAlu(stack, flags);
        this.memory = new CopperMemory(1024);
        this.pc = new CopperProgCounter();
        this.registers = new HashMap<>();

        addRegister("AX");
        addRegister("BX");
        addRegister("CX");
        addRegister("DX");
    }

    public void addRegister(String name) {
        registers.put(name, new CopperRegister(name));
    }

    public CopperRegister getRegister(String name) {
        return registers.get(name);
    }

    public void loadProgram(int[] program) {
        for (int i = 0; i < program.length; i++) {
            memory.write(i, program[i]);
        }
        pc.jump(0);
    }

    public void run() {
        boolean running = true;
        while (running) {
            int instruction = memory.read(pc.get());
            pc.inc();

            switch (instruction) {
                // HALT
                case 0x00 -> running = false;
                // PUSH
                case 0x01 -> {
                    int val = memory.read(pc.get());
                    pc.inc();
                    stack.push(val);
                }
                // ADD
                case 0x02 -> {
                    alu.add();
                    getRegister("AX").store(stack.pop());
                }
                // MOV AX
                case 0x03 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("AX").store(regVal);
                }
                // MOV BX
                case 0x04 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("BX").store(regVal);
                }
                // MOV CX
                case 0x05 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("CX").store(regVal);
                }
                // MOV DX
                case 0x06 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("DX").store(regVal);
                }
                // PUSH_REG AX
                case 0x07 -> {
                    int regVal = getRegister("AX").load();
                    stack.push(regVal);
                }
                // PRINT AX
                case 0x08 -> {
                    System.out.println("Result: " + getRegister("AX").load());
                }
                // JUMP to address in BX
                case 0x09 -> {
                    this.stack.push(this.pc.get());
                    this.pc.jump(getRegister("BX").load());
                }
                default -> {
                    System.err.println("Unknown instruction: " + instruction);
                    running = false;
                }
            }
        }
    }

    public static void main(String[] args) {
        CopperVM vm = new CopperVM();

        int[] program = {
                0x01, 0x05,
                0x01, 0x05,
                0x02, 0x08,
                0x04, 0x00,
                0x09
        };

        vm.loadProgram(program);
        vm.run();
    }
}
