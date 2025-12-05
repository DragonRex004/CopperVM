package de.dragonrex;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CopperAsm {
    HALT(0),
    PUSH(1),
    ADD(2),
    SUB(3),
    MUL(4),
    DIV(5),
    MOV(6),
    PUSH_REG(7),
    POP(8),
    PRINT(9);

    private final int opcode;

    private static final Map<Integer, CopperAsm> BY_OPCODE = Arrays.stream(values())
            .collect(Collectors.toMap(CopperAsm::getOpcode, Function.identity()));

    CopperAsm(int opcode) {
        this.opcode = opcode;
    }

    public int getOpcode() {
        return opcode;
    }

    public static CopperAsm fromOpcode(int opcode) {
        return BY_OPCODE.get(opcode);
    }
}
