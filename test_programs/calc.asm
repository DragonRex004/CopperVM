; Easy Calculator
        MOV AX, 1000
        SYSCALL 1       ; Print Stack
start:
        SYSCALL 3       ; Read Input
        PUSH AX
        SYSCALL 3       ; Read Input
        PUSH AX
        ADD
        POP AX
        SYSCALL 1       ; Print Stack
        JMP start       ; Loop