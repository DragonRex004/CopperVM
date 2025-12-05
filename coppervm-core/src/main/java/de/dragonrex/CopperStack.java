package de.dragonrex;

import java.util.ArrayDeque;
import java.util.Deque;

public class CopperStack {
    private Deque<Integer> stack;

    public CopperStack() {
        stack = new ArrayDeque<>();
    }

    public void push(int value) {
        stack.push(value);
    }

    public int pop() {
        return stack.pop();
    }

    public int peek() {
        return stack.peek();
    }
}
