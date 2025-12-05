package de.dragonrex;

public class CopperAlu {
    private CopperStack stack;

    public CopperAlu(CopperStack stack) {
        this.stack = stack;
    }

    public void add() {
        stack.push(stack.pop() + stack.pop());
    }

    public void sub() {
        stack.push(stack.pop() - stack.pop());
    }

    public void mul() {
        stack.push(stack.pop() * stack.pop());
    }

    public void div() {
        stack.push(stack.pop() / stack.pop());
    }
}
