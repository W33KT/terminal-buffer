package com.terminal.model;

public class TerminalLine {
    private final Cell[] cells;
    private final int width;

    public TerminalLine(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than 0");
        }
        this.width = width;
        this.cells = new Cell[width];

        for (int i = 0; i < width; i++) {
            this.cells[i] = new Cell();
        }
    }

    public int getWidth() {
        return width;
    }

    public Cell getCell(int col) {
        if (col < 0 || col >= width) {
            throw new IndexOutOfBoundsException("Column index out of bounds: " + col);
        }
        return cells[col];
    }

    public void fill(String content, Attributes attributes) {
        for (Cell cell : cells) {
            cell.set(content, attributes, false);
        }
    }

    public void shiftRight(int startCol, int count) {
        if (startCol < 0 || startCol >= width || count <= 0) {
            return;
        }

        for (int i = width - 1; i >= startCol + count; i--) {
            Cell target = cells[i];
            Cell source = cells[i - count];
            target.set(source.getContent(), source.getAttributes(), source.isWide());
        }

        int endClear = Math.min(startCol + count, width);
        for (int i = startCol; i < endClear; i++) {
            cells[i].reset();
        }
    }

    public String getText() {
        StringBuilder sb = new StringBuilder(width);
        for (Cell cell : cells) {
            sb.append(cell.getContent());
        }
        return sb.toString();
    }
}