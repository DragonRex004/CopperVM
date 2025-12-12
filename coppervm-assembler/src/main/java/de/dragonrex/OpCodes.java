package de.dragonrex;

import java.util.Map;

public class OpCodes {
    public static final Map<String, Integer> OPCODES = Map.ofEntries(
            // System
            Map.entry("HALT", 0x00),
            Map.entry("NOP", 0x01),
            // Stack
            Map.entry("PUSH", 0x10),
            Map.entry("POP", 0x11),
            Map.entry("DUP", 0x12),
            Map.entry("SWAP", 0x13),
            // Register MOV
            Map.entry("MOV_AX", 0x20),
            Map.entry("MOV_BX", 0x21),
            Map.entry("MOV_CX", 0x22),
            Map.entry("MOV_DX", 0x23),
            // Register PUSH
            Map.entry("PUSH_AX", 0x24),
            Map.entry("PUSH_BX", 0x25),
            Map.entry("PUSH_CX", 0x26),
            Map.entry("PUSH_DX", 0x27),
            // Register POP
            Map.entry("POP_AX", 0x28),
            Map.entry("POP_BX", 0x29),
            Map.entry("POP_CX", 0x2A),
            Map.entry("POP_DX", 0x2B),
            // Arithmetic
            Map.entry("ADD", 0x40),
            Map.entry("SUB", 0x41),
            Map.entry("MUL", 0x42),
            Map.entry("DIV", 0x43),
            Map.entry("MOD", 0x44),
            Map.entry("INC_AX", 0x45),
            Map.entry("DEC_AX", 0x46),
            // Logic
            Map.entry("AND", 0x50),
            Map.entry("OR", 0x51),
            Map.entry("XOR", 0x52),
            Map.entry("NOT", 0x53),
            Map.entry("CMP", 0x54),
            // Jumps
            Map.entry("JMP", 0x60),
            Map.entry("JZ", 0x61),
            Map.entry("JNZ", 0x62),
            Map.entry("JG", 0x63),
            Map.entry("JL", 0x64),
            Map.entry("CALL", 0x65),
            Map.entry("RET", 0x66),
            // Memory
            Map.entry("LOAD", 0x70),
            Map.entry("STORE", 0x71),
            Map.entry("LOAD_IND", 0x72),
            Map.entry("STORE_IND", 0x73),
            // I/O
            Map.entry("SYSCALL", 0x80)
    );
}
