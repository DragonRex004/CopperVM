package de.dragonrex;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class CopperVM {
    private CopperStack stack;
    private CopperAlu alu;
    private CopperMemory memory;
    private CopperFlagRegister flags;
    private CopperProgCounter pc;
    private Scanner scanner;

    private int[] registers;
    private static final int AX = 0;
    private static final int BX = 1;
    private static final int CX = 2;
    private static final int DX = 3;

    public CopperVM() {
        this.flags = new CopperFlagRegister();
        this.stack = new CopperStack();
        this.alu = new CopperAlu(stack, flags);
        this.memory = new CopperMemory(1024);
        this.pc = new CopperProgCounter();
        this.scanner = new Scanner(System.in);

        this.registers = new int[4];
        Arrays.fill(this.registers, 0);
    }

    public void loadProgram(int[] program) {
        for (int i = 0; i < program.length; i++) {
            memory.write(i, program[i]);
        }
        pc.jump(0);
    }

    public void loadProgramFromFile(String filePath) throws IOException {
        if (!filePath.endsWith(".cux")) {
            throw new IllegalArgumentException("File must have .cux extension");
        }

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            int fileSize = (int) Files.size(path);
            int programLength = fileSize / 4;

            int[] program = new int[programLength];
            for (int i = 0; i < programLength; i++) {
                program[i] = dis.readInt();
            }

            loadProgram(program);
        }
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
                    this.registers[AX] = regVal;
                }
                // MOV_BX
                case 0x21 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    this.registers[BX] = regVal;
                }
                // MOV_CX
                case 0x22 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    this.registers[CX] = regVal;
                }
                // MOV_DX
                case 0x23 -> {
                    int regVal = memory.read(pc.get());
                    pc.inc();
                    this.registers[DX] = regVal;
                }

                // PUSH_AX
                case 0x24 -> {
                    int regVal = this.registers[AX];
                    stack.push(regVal);
                }
                // PUSH_BX
                case 0x25 -> {
                    int regVal = this.registers[BX];
                    stack.push(regVal);
                }
                // PUSH_CX
                case 0x26 -> {
                    int regVal = this.registers[CX];
                    stack.push(regVal);
                }
                // PUSH_DX
                case 0x27 -> {
                    int regVal = this.registers[DX];
                    stack.push(regVal);
                }

                // POP_AX
                case 0x28 -> {
                    int regVal = stack.pop();
                    this.registers[AX] = regVal;
                }
                // POP_BX
                case 0x29 -> {
                    int regVal = stack.pop();
                    this.registers[BX] = regVal;
                }
                // POP_CX
                case 0x2A -> {
                    int regVal = stack.pop();
                    this.registers[CX] = regVal;
                }
                // POP_DX
                case 0x2B -> {
                    int regVal = stack.pop();
                    this.registers[DX] = regVal;
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
                case 0x45 -> this.registers[AX] = this.registers[AX]++;
                // DEC_AX
                case 0x46 -> this.registers[AX] = this.registers[AX]--;
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
                    int addr = this.registers[AX];
                    int val = memory.read(addr);
                    stack.push(val);
                }
                // STORE_IND
                case 0x73 -> {
                    int addr = this.registers[AX];
                    int val = stack.pop();
                    memory.write(addr, val);
                }
                //############################ RAM Operations (0x70-0x7F) ############################

                //############################ I/O Operations (0x80-0x8F) ############################
                // PRINT_AX
                case 0x80 -> {
                    int value = this.registers[AX];
                    System.out.println(value);
                }
                // PRINT_STACK
                case 0x81 -> {
                    int value = stack.peek();
                    System.out.println(value);
                }
                // READ
                case 0x82 -> {
                    int value = scanner.nextInt();
                    this.registers[AX] = value;
                }
                //############################ I/O Operations (0x80-0x8F) ############################
                default -> {
                    System.err.println("Unknown instruction: " + instruction);
                    scanner.close();
                    running = false;
                }
            }
        }
    }

    public static void main(String[] args) {
        CopperVM vm = new CopperVM();

        if (args.length > 0) {
            try {
                vm.loadProgramFromFile(args[0]);
                vm.run();
            } catch (IOException e) {
                System.err.println("Error loading program: " + e.getMessage());
                System.exit(1);
            }
        } else {
            System.err.println("No program specified");
        }
    }
}
