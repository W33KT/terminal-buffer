package com.terminal.core;

import com.terminal.model.Attributes;
import com.terminal.model.TerminalLine;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author tankaiwen
 */
public class TerminalBuffer {
    private final int width;
    private final int height;
    private final int maxScrollback;

    private final List<TerminalLine> screen;
    private final Deque<TerminalLine> scrollback;

    private int cursorX;
    private int cursorY;
    private Attributes currentAttributes;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        if (width <= 0 || height <= 0 || maxScrollback < 0) {
            throw new IllegalArgumentException("Invalid terminal dimensions or scrollback size");
        }
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;

        this.screen = new ArrayList<>(height);
        for (int i = 0; i < height; i++) {
            this.screen.add(new TerminalLine(width));
        }

        this.scrollback = new ArrayDeque<>(maxScrollback);

        this.cursorX = 0;
        this.cursorY = 0;
        this.currentAttributes = Attributes.DEFAULT;
    }

    public void setCurrentAttributes(Attributes attributes) {
        this.currentAttributes = attributes != null ? attributes : Attributes.DEFAULT;
    }

    public Attributes getCurrentAttributes() {
        return currentAttributes;
    }

    public int getCursorX() { return cursorX; }
    public int getCursorY() { return cursorY; }

    public void setCursorPosition(int col, int row) {
        this.cursorX = Math.max(0, Math.min(width - 1, col));
        this.cursorY = Math.max(0, Math.min(height - 1, row));
    }

    public void moveCursorUp(int n) {
        if (n > 0) setCursorPosition(cursorX, cursorY - n);
    }

    public void moveCursorDown(int n) {
        if (n > 0) setCursorPosition(cursorX, cursorY + n);
    }

    public void moveCursorLeft(int n) {
        if (n > 0) setCursorPosition(cursorX - n, cursorY);
    }

    public void moveCursorRight(int n) {
        if (n > 0) setCursorPosition(cursorX + n, cursorY);
    }

    protected TerminalLine getCurrentLine() {
        return screen.get(cursorY);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}