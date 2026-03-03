package com.terminal.model;

/**
 * @author tankaiwen
 */
public class Cell {
    private String content;
    private Attributes attributes;
    private boolean isWide;

    public Cell() {
        this.content = " ";
        this.attributes = Attributes.DEFAULT;
        this.isWide = false;
    }

    public void set(String content, Attributes attributes, boolean isWide) {
        this.content = content;
        this.attributes = attributes;
        this.isWide = isWide;
    }

    public void reset() {
        this.content = " ";
        this.attributes = Attributes.DEFAULT;
        this.isWide = false;
    }

    public String getContent() { return content; }
    public Attributes getAttributes() { return attributes; }
    public boolean isWide() { return isWide; }
}
