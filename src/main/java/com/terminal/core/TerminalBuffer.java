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
        if (n <= 0) {
            return;
        }
        setCursorPosition(cursorX, cursorY - n);
    }

    public void moveCursorDown(int n) {
        if (n <= 0) {
            return;
        }
        setCursorPosition(cursorX, cursorY + n);
    }

    public void moveCursorLeft(int n) {
        if (n <= 0) {
            return;
        }
        setCursorPosition(cursorX - n, cursorY);
    }

    public void moveCursorRight(int n) {
        if (n <= 0) {
            return;
        }
        setCursorPosition(cursorX + n, cursorY);
    }

    protected TerminalLine getCurrentLine() {
        return screen.get(cursorY);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void insertEmptyLineAtBottom() {
        TerminalLine topApples = screen.remove(0);

        if (maxScrollback > 0) {
            if (scrollback.size() >= maxScrollback) {
                scrollback.pollFirst();
            }
            scrollback.addLast(topApples);
        }

        screen.add(new TerminalLine(width));
    }

    public void write(String text) {
        for (int i = 0; i < text.length(); i++) {
            String ch = String.valueOf(text.charAt(i));

            if (cursorX >= width) {
                cursorX = 0;
                if (cursorY == height - 1) {
                    insertEmptyLineAtBottom();
                } else {
                    cursorY++;
                }
            }

            getCurrentLine().getCell(cursorX).set(ch, currentAttributes, false);
            cursorX++;
        }
    }

    public void insert(String text) {
        int len = text.length();
        getCurrentLine().shiftRight(cursorX, len);
        write(text);
    }

    public void fillLine(String content) {
        getCurrentLine().fill(content, currentAttributes);
    }

    public void clearScreen() {
        for (TerminalLine line : screen) {
            line.fill(" ", Attributes.DEFAULT);
        }
    }

    public void clearAll() {
        clearScreen();
        scrollback.clear();
    }

    private TerminalLine getTargetLine(int row) {
        if (row >= 0 && row < height) {
            return screen.get(row);
        } else if (row < 0 && row >= -scrollback.size()) {
            java.util.Iterator<TerminalLine> it = scrollback.descendingIterator();
            TerminalLine target = null;
            for (int i = 0; i < Math.abs(row); i++) {
                target = it.next();
            }
            return target;
        }
        throw new IndexOutOfBoundsException("Row index out of bounds: " + row);
    }

    public String getCharAt(int col, int row) {
        return getTargetLine(row).getCell(col).getContent();
    }

    public Attributes getAttributesAt(int col, int row) {
        return getTargetLine(row).getCell(col).getAttributes();
    }

    public String getLineAsString(int row) {
        return getTargetLine(row).getText();
    }

    public String getScreenContent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            sb.append(screen.get(i).getText()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String getEntireContent() {
        StringBuilder sb = new StringBuilder();
        for (TerminalLine line : scrollback) {
            sb.append(line.getText()).append(System.lineSeparator());
        }
        sb.append(getScreenContent());
        return sb.toString();
    }
}