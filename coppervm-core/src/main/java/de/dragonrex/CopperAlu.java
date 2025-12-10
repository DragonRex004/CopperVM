package de.dragonrex;

public class CopperAlu {
    private CopperStack stack;
    private CopperFlagRegister flags;

    public CopperAlu(CopperStack stack, CopperFlagRegister flagRegister) {
        this.stack = stack;
        this.flags = flagRegister;
    }

    public void add() {
        int b = stack.pop();
        int a = stack.pop();
        int result = a + b;

        flags.updateFlagsAdd(a, b, result);
        stack.push(result);
    }

    public void sub() {
        int b = stack.pop();
        int a = stack.pop();
        int result = a - b;

        flags.updateFlags(result);
        flags.setCarry(a < b);
        stack.push(result);
    }

    public void cmp() {
        int b = stack.pop();
        int a = stack.pop();
        int result = a - b;

        flags.updateFlags(result);
        flags.setCarry(a < b);
    }

    public void mul() {
        int b = stack.pop();
        int a = stack.pop();
        long result = (long)a * (long)b;
        int truncated = (int)result;

        flags.updateFlags(truncated);
        flags.setOverflow(result != truncated);
        stack.push(truncated);
    }

    public void div() {
        int divisor = stack.pop();
        int dividend = stack.pop();

        if (divisor == 0) {
            flags.setZero(false);
            flags.setCarry(true);
            flags.setOverflow(true);
            flags.setNegative(false);
            stack.push(0);
            return;
        }

        int result = dividend / divisor;
        int remainder = dividend % divisor;

        flags.setZero(result == 0);
        flags.setNegative(result < 0);
        flags.setCarry(remainder != 0);
        flags.setOverflow(false);

        stack.push(result);
    }

    public void mod() {
        int divisor = stack.pop();
        int dividend = stack.pop();

        if (divisor == 0) {
            flags.setZero(false);
            flags.setCarry(true);
            flags.setOverflow(true);
            flags.setNegative(false);
            stack.push(0);
            return;
        }

        int result = dividend % divisor;

        flags.setZero(result == 0);
        flags.setNegative(result < 0);
        flags.setCarry(false);
        flags.setOverflow(false);

        stack.push(result);
    }
}
