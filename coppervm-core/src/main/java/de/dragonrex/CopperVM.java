package de.dragonrex;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
                //############################ System Operations (0x00-0x0F) ############################
                // HALT
                case 0x00 -> running = false;
                // NOP
                case 0x01 -> {
                    // Empty Case
                }
                //############################ System Operations (0x00-0x0F) ############################

                //############################ Stack Operations (0x10-0x1F) ############################
                // PUSH
                case 0x10 -> {
                    int val = memory.read(pc.get());
                    pc.inc();
                    stack.push(val);
                }
                // POP
                case 0x11 -> {
                    stack.pop();
                }
                // DUP
                case 0x12 -> {
                    int val = stack.peek();
                    stack.push(val);
                }
                // SWAP
                case 0x13 -> {
                    int a = stack.pop();
                    int b = stack.pop();
                    stack.push(a);
                    stack.push(b);
                }
                //############################ Stack Operations (0x10-0x1F) ############################

                //############################ Register Operations (0x20-0x3F) ############################
                // MOV_AX
                case 0x20 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("AX").store(regVal);
                }
                // MOV_BX
                case 0x21 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("BX").store(regVal);
                }
                // MOV_CX
                case 0x22 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("CX").store(regVal);
                }
                // MOV_DX
                case 0x23 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    getRegister("DX").store(regVal);
                }

                // PUSH_AX
                case 0x24 -> {
                    int regVal = getRegister("AX").load();
                    stack.push(regVal);
                }
                // PUSH_BX
                case 0x25 -> {
                    int regVal = getRegister("BX").load();
                    stack.push(regVal);
                }
                // PUSH_CX
                case 0x26 -> {
                    int regVal = getRegister("CX").load();
                    stack.push(regVal);
                }
                // PUSH_DX
                case 0x27 -> {
                    int regVal = getRegister("DX").load();
                    stack.push(regVal);
                }

                // POP_AX
                case 0x28 -> {
                    int regVal = stack.pop();
                    getRegister("AX").store(regVal);
                }
                // POP_BX
                case 0x29 -> {
                    int regVal = stack.pop();
                    getRegister("BX").store(regVal);
                }
                // POP_CX
                case 0x2A -> {
                    int regVal = stack.pop();
                    getRegister("CX").store(regVal);
                }
                // POP_DX
                case 0x2B -> {
                    int regVal = stack.pop();
                    getRegister("DX").store(regVal);
                }
                //############################ Register Operations (0x20-0x3F) ############################

                //############################ Arithmetic Operations (0x40-0x4F) ############################
                // ADD
                case 0x40 -> alu.add();
                // SUB
                case 0x41 -> alu.sub();
                // MUL
                case 0x42 -> alu.mul();
                // DIV
                case 0x43 -> alu.div();
                // MOD
                case 0x44 -> alu.mod();
                // INC_AX
                case 0x45 -> getRegister("AX").store(getRegister("AX").load() + 1);
                // DEC_AX
                case 0x46 -> getRegister("AX").store(getRegister("AX").load() - 1);
                //############################ Arithmetic Operations (0x40-0x4F) ############################

                //############################ Logic & Compare Operations (0x50-0x5F) ############################
                // AND
                case 0x50 -> alu.and();
                // OR
                case 0x51 -> alu.or();
                // XOR
                case 0x52 -> alu.xor();
                // NOT
                case 0x53 -> alu.not();
                // CMP
                case 0x54 -> alu.cmp();
                //############################ Logic & Compare Operations (0x50-0x5F) ############################

                //############################ Jump Operations (0x60-0x6F) ############################
                // JMP
                case 0x60 -> {
                    int addr = memory.read(pc.get());
                    pc.jump(addr);
                }
                // JZ
                case 0x61 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    if(flags.isZero()) {
                        pc.jump(addr);
                    }
                }
                // JNZ
                case 0x62 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    if(!flags.isZero()) {
                        pc.jump(addr);
                    }
                }
                // JG
                case 0x63 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    if(!flags.isZero() && !flags.isNegative()) {
                        pc.jump(addr);
                    }
                }
                //JL
                case 0x64 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    if(flags.isNegative()) {
                        pc.jump(addr);
                    }
                }
                // CALL
                case 0x65 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    stack.push(pc.get());
                    pc.jump(addr);
                }
                // RET
                case 0x66 -> {
                    int addr = stack.pop();
                    pc.jump(addr);
                }
                //############################ Jump Operations (0x60-0x6F) ############################

                //############################ RAM Operations (0x70-0x7F) ############################
                // LOAD
                case 0x70 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    int val = memory.read(addr);
                    stack.push(val);
                }
                // STORE
                case 0x71 -> {
                    int addr = memory.read(pc.get());
                    pc.inc();
                    int val = stack.pop();
                    memory.write(addr, val);
                }
                // LOAD_IND
                case 0x72 -> {
                    int addr = getRegister("AX").load();
                    int val = memory.read(addr);
                    stack.push(val);
                }
                // STORE_IND
                case 0x73 -> {
                    int addr = getRegister("AX").load();
                    int val = stack.pop();
                    memory.write(addr, val);
                }
                //############################ RAM Operations (0x70-0x7F) ############################

                //############################ I/O Operations (0x80-0x8F) ############################
                // PRINT_AX
                case 0x80 -> {
                    int value = getRegister("AX").load();
                    System.out.println(value);
                }
                // PRINT_STACK
                case 0x81 -> {
                    int value = stack.peek();
                    System.out.println(value);
                }
                // READ
                case 0x82 -> {
                    Scanner scanner = new Scanner(System.in);
                    int value = scanner.nextInt();
                    getRegister("AX").store(value);
                    scanner.close();
                }
                //############################ I/O Operations (0x80-0x8F) ############################
                default -> {
                    System.err.println("Unknown instruction: " + instruction);
                    running = false;
                }
            }
        }
    }

    public static void main(String[] args) {
        CopperVM vm = new CopperVM();

        /*
        PUSH 10        ; 0x10, 0x0A
        PUSH 20        ; 0x10, 0x14
        ADD            ; 0x40
        POP_AX         ; 0x28
        PRINT_AX       ; 0x80
        HALT           ; 0x00
         */

        int[] addition_test_program = {
                0x10, 0x0A,
                0x10, 0x14,
                0x40,
                0x28,
                0x80,
                0x00
        };

        /*
        MOV_AX 5       ; 0x20, 0x05 - Counter auf 5 setzen
        PUSH_AX        ; 0x24       - AX auf Stack
        PRINT_STACK    ; 0x81       - Aktuellen Wert ausgeben
        DEC_AX         ; 0x46       - AX dekrementieren
        PUSH_AX        ; 0x24       - AX auf Stack
        PUSH 0         ; 0x10, 0x00 - 0 auf Stack pushen
        CMP            ; 0x54       - Vergleiche CX mit 0
        JNZ 0          ; 0x62, 0x03 - Springe zurück wenn AX != 0
        HALT           ; 0x00
         */

        int[] loop_test_program = {
                0x20, 0x05,
                0x24,
                0x81,
                0x46,
                0x24,
                0x10, 0x00,
                0x54,
                0x62, 0x02,
                0x00

        };

        vm.loadProgram(loop_test_program);
        vm.run();
    }
}
