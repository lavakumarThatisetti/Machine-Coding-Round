package com.lavakumar.excelsheet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Spreadsheet {
    private final Map<CellAddress, Cell> cells = new HashMap<>();

    public void putCell(CellAddress address, Cell cell) {
        cells.put(address, cell);
    }

    public void removeCell(CellAddress address) {
        cells.remove(address);
    }

    public Cell getCell(CellAddress address) {
        return cells.get(address);
    }

    public List<CellAddress> getAllAddresses() {
        return new ArrayList<>(cells.keySet());
    }
}
