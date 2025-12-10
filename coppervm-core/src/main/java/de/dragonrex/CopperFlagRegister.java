package de.dragonrex;

public class CopperFlagRegister {
    private boolean zero;
    private boolean carry;
    private boolean negative;
    private boolean overflow;

    public CopperFlagRegister() {
        reset();
    }

    public void reset() {
        zero = false;
        carry = false;
        negative = false;
        overflow = false;
    }

    public boolean isZero() { return zero; }
    public boolean isCarry() { return carry; }
    public boolean isNegative() { return negative; }
    public boolean isOverflow() { return overflow; }

    public void setZero(boolean value) { zero = value; }
    public void setCarry(boolean value) { carry = value; }
    public void setNegative(boolean value) { negative = value; }
    public void setOverflow(boolean value) { overflow = value; }

    public void updateFlags(int result) {
        zero = (result == 0);
        negative = (result < 0);
    }

    public void updateFlagsAdd(int a, int b, int result) {
        updateFlags(result);
        carry = ((long)a + (long)b) > Integer.MAX_VALUE;
        overflow = ((a > 0 && b > 0 && result < 0) ||
                (a < 0 && b < 0 && result > 0));
    }

    public int toByte() {
        int flags = 0;
        if (zero) flags |= 0b0001;
        if (carry) flags |= 0b0010;
        if (negative) flags |= 0b0100;
        if (overflow) flags |= 0b1000;
        return flags;
    }

    public void fromByte(int flags) {
        zero = (flags & 0b0001) != 0;
        carry = (flags & 0b0010) != 0;
        negative = (flags & 0b0100) != 0;
        overflow = (flags & 0b1000) != 0;
    }
}
