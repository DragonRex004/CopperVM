package de.dragonrex;

import java.util.HashMap;
import java.util.Map;

public class CopperVM {
    private CopperStack stack;
    private CopperAlu alu;
    private CopperMemory memory;
    private CopperProgCounter pc;

    private Map<String, CopperRegister> registers;

    public CopperVM() {
        this.stack = new CopperStack();
        this.alu = new CopperAlu(stack);
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
                case 0 -> running = false;
                // PUSH
                case 1 -> {
                    int val = memory.read(pc.get());
                    pc.inc();
                    stack.push(val);
                }
                // ADD
                case 2 -> {
                    alu.add();
                    getRegister("AX").store(stack.pop());
                }
                // MOV AX
                case 3 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("AX").store(regVal);
                }
                // PUSH_REG AX
                case 4 -> {
                    int regVal = getRegister("AX").load();
                    stack.push(regVal);
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
                1, 5,  // PUSH 5
                1, 3,  // PUSH 3
                2,     // ADD
                4,     // PUSH_REG AX
                1, 2,  // PUSH 2
                2,     // ADD
                0      // HALT
        };

        vm.loadProgram(program);
        vm.run();

        System.out.println("Result: " + vm.getRegister("AX").load());
    }
}
