# Terminal Text Buffer

A Java implementation of a terminal emulator's core data structure. This project provides the memory model and logic required to manage a grid of character cells, handle cursor movements, text editing, styling, and scrollback history.

## Features

* Bounded screen grid with strict cursor coordinate management.

* Scrollback buffer with a configurable maximum capacity.

* Text styling support including 16 standard terminal colors and text styles (bold, italic, underline).

* Support for wide characters (CJK, Emoji) utilizing surrogate pairs and proper cell span alignment.

* Dynamic terminal resizing, smoothly transferring overflowing lines to the scrollback buffer when the height shrinks.

## Architecture and Trade-offs

### Memory Allocation

Terminal screens update at a high frequency. To minimize Garbage Collection (GC) pauses during rendering bursts, the buffer avoids creating new `Cell` objects on every write. Instead, `TerminalLine` pre-allocates all cells during initialization. Writing text mutates the state of these existing cell instances.

### Data Structures

* **Screen**: Backed by an `ArrayList<TerminalLine>` to ensure O(1) random access by row index, which is essential for arbitrary cursor movements.

* **Scrollback**: Managed via an `ArrayDeque<TerminalLine>`. Since scrollback is strictly a FIFO queue with a size limit, `ArrayDeque` offers better memory locality and throughput compared to a standard linked list.

* **Line Content**: Backed by a fixed `Cell[]` array. Terminal widths are static until explicitly resized, making plain arrays the most efficient choice for row data.

### Coordinate System

The buffer uses a unified coordinate system to simplify data retrieval. Row indices `>= 0` represent the active screen, while indices `< 0` transparently map to the scrollback history (where `-1` is the most recently scrolled-out line).

## Future Improvements

If this were to be expanded into a production-grade emulator component, the following optimizations would be prioritized:

1. **Circular Screen Buffer**: Scrolling currently uses `ArrayList.remove(0)` and appends to the end. While fast enough for standard terminal dimensions, migrating the screen list to a logical ring buffer would make scrolling a strict O(1) pointer-shift operation.

2. **Bitmasking Attributes**: The current implementation uses an immutable `Attributes` object (or record) for code readability. For extreme memory efficiency, these flags and colors could be packed into a single primitive `long` per cell using bitwise operations.

3. **Text Reflow**: When the terminal width is reduced, the current implementation truncates overflowing text. A robust reflow mechanism would wrap these characters to the next line, requiring a decoupled logical document model underlying the physical grid.

## Build and Test

This project uses Maven and requires Java 17 or higher. JUnit 5 is the only test dependency.

Compile the project:

```
mvn clean compile
```

Run the test suite:

```
mvn test
```