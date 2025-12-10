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
        if(!stack.isEmpty()) {
            return stack.pop();
        }
        return 0;
    }

    public int peek() {
        if(!stack.isEmpty()) {
            return stack.peek();
        }
        return 0;
    }
}
