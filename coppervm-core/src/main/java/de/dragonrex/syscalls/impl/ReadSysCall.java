package de.dragonrex.syscalls.impl;

import de.dragonrex.CopperVM;
import de.dragonrex.syscalls.SysCall;

public class ReadSysCall implements SysCall {
    @Override
    public int address() {
        return 0x03;
    }

    @Override
    public void execute(CopperVM vm) {
        int value = vm.scanner.nextInt();
        vm.registers[CopperVM.AX] = value;
    }
}
