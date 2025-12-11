package de.dragonrex;

public class CopperStack {
    private int stackPointer;
    private int[] stack;

    public CopperStack() {
        this.stack = new int[1024];
        this.stackPointer = 0;
    }

    public void push(int value) {
        if (stackPointer >= stack.length) {
            throw new StackOverflowError("Stack overflow");
        }
        stack[stackPointer++] = value;
    }

    public int pop() {
        if (stackPointer <= 0) {
            throw new IllegalStateException("Stack underflow");
        }
        return stack[--stackPointer];
    }

    public int peek() {
        if (stackPointer <= 0) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack[stackPointer - 1];
    }

    public boolean isEmpty() {
        return stackPointer == 0;
    }
}
