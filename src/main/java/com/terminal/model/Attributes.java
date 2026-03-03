package com.terminal.model;

/**
 * @author tankaiwen
 */
public record Attributes(
        Color foreground,
        Color background,
        boolean isBold,
        boolean isItalic,
        boolean isUnderline
) {
    public static final Attributes DEFAULT = new Attributes(
            Color.DEFAULT, Color.DEFAULT, false, false, false
    );
}
