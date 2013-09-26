
package com.google.refine.compression;

import java.io.Writer;
import java.util.List;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONWriter;

import com.google.refine.ProjectManager;
import com.google.refine.expr.CellTuple;
import com.google.refine.model.Cell;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

public class CompressedRow {

    private byte[] compressedRow;
    private Row row;
    private final static RowCompressionManager manager = new RowCompressionManager();
    
    private static final String FLAGGED = "flagged";
    private static final String STARRED = "starred";

    private final int COMPRESSION_LEVEL;

    public CompressedRow(int cellCount) {
        COMPRESSION_LEVEL = (Integer) (ProjectManager.singleton.getPreferenceStore().get("compression") == null ? 0
                : ProjectManager.singleton.getPreferenceStore().get("compression"));
        Row tmprow = new Row(cellCount);
        if (COMPRESSION_LEVEL == 0)
            row = tmprow;
        else
            compressedRow = manager.serialize(tmprow, COMPRESSION_LEVEL);
    }

    public CompressedRow(Row row) {
        COMPRESSION_LEVEL = (Integer) (ProjectManager.singleton.getPreferenceStore().get("compression") == null ? 0
                : ProjectManager.singleton.getPreferenceStore().get("compression"));
        if (COMPRESSION_LEVEL == 0)
            this.row = row;
        else
            this.compressedRow = manager.serialize(row, COMPRESSION_LEVEL);
    }

    public Row getRow() {
        if (COMPRESSION_LEVEL == 0)
            return row;
        else
            return manager.deserialize(compressedRow, COMPRESSION_LEVEL);
    }

    public CompressedRow dup() {
        return new CompressedRow(row.dup());
    }

    public Object getField(String name, Properties bindings) {
        if (FLAGGED.equals(name)) {
            return getRow().flagged;
        } else if (STARRED.equals(name)) {
            return getRow().starred;
        }
        return null;
    }

    public boolean fieldAlsoHasFields(String name) {
        return "cells".equals(name) || "record".equals(name);
    }

    public boolean isEmpty() {
        if (COMPRESSION_LEVEL == 0)
            return row.isEmpty();
        else
            return getRow().isEmpty();
    }

    public Cell getCell(int cellIndex) {
        if (COMPRESSION_LEVEL == 0)
            return row.getCell(cellIndex);
        else
            return getRow().getCell(cellIndex);
    }

    public Object getCellValue(int cellIndex) {
        if (COMPRESSION_LEVEL == 0)
            return row.getCellValue(cellIndex);
        else
            return getRow().getCellValue(cellIndex);
    }

    public boolean isCellBlank(int cellIndex) {
        return isValueBlank(getCellValue(cellIndex));
    }

    protected boolean isValueBlank(Object value) {
        return value == null || (value instanceof String && ((String) value).trim().length() == 0);
    }

    public void setCell(int cellIndex, Cell cell) {
        if (COMPRESSION_LEVEL == 0)
            row.cells.set(cellIndex, cell);
        else {
            Row tmprow = getRow();
            tmprow.cells.set(cellIndex, cell);
            compressedRow = manager.serialize(tmprow, COMPRESSION_LEVEL);
        }
    }
    
    public void addCell(Cell cell){
        if (COMPRESSION_LEVEL == 0)
            row.cells.add(cell);
        else {
            Row tmprow = getRow();
            tmprow.cells.add(cell);
            compressedRow = manager.serialize(tmprow, COMPRESSION_LEVEL);
        }
    }

    public CellTuple getCellTuple(Project project) {
        return getRow().getCellTuple(project);
    }

    public void write(JSONWriter writer, Properties options)
            throws JSONException {
        if (COMPRESSION_LEVEL == 0)
            row.write(writer, options);
        else
            getRow().write(writer, options);
    }

    public void save(Writer writer, Properties options) {
        if (COMPRESSION_LEVEL == 0)
            row.save(writer, options);
        else
            getRow().save(writer, options);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        List<Cell> tmpcell;
        if (COMPRESSION_LEVEL == 0)
            tmpcell = row.cells;
        else
            tmpcell = getRow().cells;
        for (Cell cell : tmpcell) {
            result.append(cell == null ? "null" : cell.toString());
            result.append(',');
        }
        return result.toString();
    }

    public List<Cell> getCells() {
        return getRow().cells;
    }

    public void setFlagged(boolean flagged) {
        Row row = getRow();
        row.flagged = flagged;
        compressedRow = manager.serialize(row, COMPRESSION_LEVEL);
    }

    public boolean getFlagged() {
        return getRow().flagged;
    }

    public void setStarred(boolean starred) {
        Row row = getRow();
        row.starred = starred;
        compressedRow = manager.serialize(row, COMPRESSION_LEVEL);
    }

    public boolean getStarred() {
        return getRow().starred;
    }
}
