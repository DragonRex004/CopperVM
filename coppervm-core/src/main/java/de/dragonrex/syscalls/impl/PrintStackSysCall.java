package de.dragonrex.syscalls.impl;

import de.dragonrex.CopperVM;
import de.dragonrex.syscalls.SysCall;

public class PrintStackSysCall implements SysCall {
    @Override
    public int address() {
        return 0x02;
    }

    @Override
    public void execute(CopperVM vm) {
        int value = vm.stack.peek();
        System.out.println(value);
    }
}
