package de.dragonrex.syscalls.impl;

import de.dragonrex.CopperVM;
import de.dragonrex.syscalls.SysCall;

public class PrintAxSysCall implements SysCall {
    @Override
    public int address() {
        return 0x01;
    }

    @Override
    public void execute(CopperVM vm) {
        int value = vm.registers[CopperVM.AX];
        System.out.println(value);
    }
}
