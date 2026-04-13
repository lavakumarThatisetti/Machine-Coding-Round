package com.lavakumar.excelsheet.model;

import com.lavakumar.excelsheet.cellcontent.CellContent;

import java.util.Objects;

public class Cell {
    private final CellAddress address;
    private final String rawInput;
    private final CellContent content;

    public Cell(CellAddress address, String rawInput, CellContent content) {
        this.address = Objects.requireNonNull(address);
        this.rawInput = Objects.requireNonNull(rawInput);
        this.content = Objects.requireNonNull(content);
    }

    public CellAddress getAddress() {
        return address;
    }

    public String getRawInput() {
        return rawInput;
    }

    public CellContent getContent() {
        return content;
    }
}
