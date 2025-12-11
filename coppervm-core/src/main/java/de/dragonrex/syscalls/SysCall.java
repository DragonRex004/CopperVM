package de.dragonrex.syscalls;

import de.dragonrex.CopperVM;

public interface SysCall {
    int address();
    void execute(CopperVM vm);
}
