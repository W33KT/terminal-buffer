package com.terminal.core;

import com.terminal.model.Attributes;
import com.terminal.model.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(10, 3, 5);
    }

    @Test
    @DisplayName("Cursor boundary test: The cursor must never move beyond the screen area.")
    void testCursorBoundaryConditions() {
        buffer.moveCursorLeft(5);
        buffer.moveCursorUp(5);
        assertEquals(0, buffer.getCursorX());
        assertEquals(0, buffer.getCursorY());

        buffer.moveCursorRight(50);
        buffer.moveCursorDown(50);
        assertEquals(9, buffer.getCursorX());
        assertEquals(2, buffer.getCursorY());
    }

    @Test
    @DisplayName("Basic write test: The written text should overwrite the current content and move the cursor correctly.")
    void testBasicWriteAndAttributes() {
        Attributes redText = new Attributes(Color.RED, Color.DEFAULT, true, false, false);
        buffer.setCurrentAttributes(redText);

        buffer.write("Hello");

        assertEquals(5, buffer.getCursorX());
        assertEquals(0, buffer.getCursorY());
        assertEquals("H", buffer.getCharAt(0, 0));
        assertEquals(Color.RED, buffer.getAttributesAt(0, 0).foreground());
        assertTrue(buffer.getAttributesAt(0, 0).isBold());

        String line = buffer.getLineAsString(0);
        assertTrue(line.startsWith("Hello"));
    }

    @Test
    @DisplayName("Insert text test: Inserting text should shift the existing content to the right.")
    void testInsertText() {
        buffer.write("12345");
        buffer.setCursorPosition(2, 0);
        buffer.insert("AB");

        String line = buffer.getLineAsString(0);
        assertTrue(line.startsWith("12AB345"));
        assertEquals(4, buffer.getCursorX());
    }

    @Test
    @DisplayName("Scrolling and rollback zone test: When a newline character is touched at the bottom, scrolling should be triggered, and the old line should be pushed into the rollback zone.")
    void testScrollingAndScrollback() {
        buffer.write("Line1");
        buffer.setCursorPosition(0, 1);
        buffer.write("Line2");
        buffer.setCursorPosition(0, 2);
        buffer.write("Line3");

        buffer.setCursorPosition(9, 2);
        buffer.write("XY");

        assertTrue(buffer.getLineAsString(0).startsWith("Line2"));

        assertTrue(buffer.getLineAsString(-1).startsWith("Line1"));
    }

    @Test
    @DisplayName("Cleanup Functionality Test: Cleaning the screen and rollback area should reset all content.")
    void testClearAll() {
        buffer.write("Test");
        buffer.insertEmptyLineAtBottom();

        buffer.clearAll();

        assertEquals(" ", buffer.getCharAt(0, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getLineAsString(-1));
    }

    @Test
    @DisplayName("Bonus: In the wide character test, Chinese characters should occupy two spaces and wrap correctly at the end of the line.")
    void testWideCharacters() {
        // 测试中文字符占据两格
        buffer.write("中");
        assertEquals(2, buffer.getCursorX());
        assertEquals("中", buffer.getCharAt(0, 0));
        assertEquals("", buffer.getCharAt(1, 0));

        buffer.setCursorPosition(8, 2);
        buffer.write("A");

        buffer.write("文");

        assertEquals(" ", buffer.getCharAt(9, 1));
        assertEquals("文", buffer.getCharAt(0, 2));
        assertEquals(2, buffer.getCursorX());
    }

    @Test
    @DisplayName("Bonus: Screen resizing test; when reducing the height, it should be correctly saved to the rollback area.")
    void testResizeShrinkHeight() {
        buffer.write("Line1");
        buffer.setCursorPosition(0, 1);
        buffer.write("Line2");
        buffer.setCursorPosition(0, 2);
        buffer.write("Line3");

        buffer.resize(10, 2);

        assertEquals(2, buffer.getHeight());
        assertTrue(buffer.getLineAsString(0).startsWith("Line2"));
        assertTrue(buffer.getLineAsString(1).startsWith("Line3"));
        assertTrue(buffer.getLineAsString(-1).startsWith("Line1")); // 回滚区数据
    }
}