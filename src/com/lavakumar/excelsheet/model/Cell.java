package com.lavakumar.excelsheet.model;

import com.lavakumar.excelsheet.cellcontent.CellContent;

import java.util.Objects;

public record Cell(CellAddress address, String rawInput, CellContent content) {
    public Cell(CellAddress address, String rawInput, CellContent content) {
        this.address = Objects.requireNonNull(address);
        this.rawInput = Objects.requireNonNull(rawInput);
        this.content = Objects.requireNonNull(content);
    }
}
