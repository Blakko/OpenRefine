
package com.google.refine.compression;

import java.io.Writer;
import java.util.ArrayList;
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

    private int cellCount;

    private static final String FLAGGED = "flagged";
    private static final String STARRED = "starred";

    private final int COMPRESSION_LEVEL;
    private int rawSize;

    public CompressedRow(int cellCount) {
        COMPRESSION_LEVEL = (Integer) (ProjectManager.singleton.getPreferenceStore().get("compression") == null ? 0
                : ProjectManager.singleton.getPreferenceStore().get("compression"));
        Row tmprow = new Row(cellCount);
        this.cellCount = tmprow.cells.size();
        if (COMPRESSION_LEVEL == 0)
            row = tmprow;
        else if (COMPRESSION_LEVEL == 1)
            this.compressedRow = manager.serialize(tmprow);
        else if (COMPRESSION_LEVEL == 2) {
            byte[] raw = manager.serialize(tmprow);
            byte[] ret = manager.compressFast(raw);
            this.rawSize = raw.length;
            this.compressedRow = ret;
        }
    }

    public CompressedRow(Row row) {
        COMPRESSION_LEVEL = (Integer) (ProjectManager.singleton.getPreferenceStore().get("compression") == null ? 0
                : ProjectManager.singleton.getPreferenceStore().get("compression"));
        this.cellCount = row.cells.size();
        if (COMPRESSION_LEVEL == 0)
            this.row = row;

        else if (COMPRESSION_LEVEL == 1)
            this.compressedRow = manager.serialize(row);

        else if (COMPRESSION_LEVEL == 2) {
            byte[] raw = manager.serialize(row);
            byte[] ret = manager.compressFast(raw);
            this.rawSize = raw.length;
            this.compressedRow = ret;
        }
    }

    public Row getRow() {
        if (COMPRESSION_LEVEL == 0)
            return row;

        else if (COMPRESSION_LEVEL == 1)
            return manager.deserialize(compressedRow);

        else if (COMPRESSION_LEVEL == 2) {
            byte[] raw = manager.decompressFast(compressedRow, rawSize);
            return manager.deserialize(raw);
        } else
            return null;
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
        return getRow().isEmpty();
    }

    public Cell getCell(int cellIndex) {
        return getRow().getCell(cellIndex);
    }

    public Object getCellValue(int cellIndex) {
        return getRow().getCellValue(cellIndex);
    }

    public boolean isCellBlank(int cellIndex) {
        return isValueBlank(getCellValue(cellIndex));
    }

    protected boolean isValueBlank(Object value) {
        return value == null || (value instanceof String && ((String) value).trim().length() == 0);
    }

    public void setCell(int cellIndex, Cell cell) {
        if (COMPRESSION_LEVEL == 0) {
            row.setCell(cellIndex, cell);
            cellCount = row.cells.size();
        } else {
            Row tmprow = getRow();
            tmprow.setCell(cellIndex, cell);
            cellCount = tmprow.cells.size();
            byte[] tmp = manager.serialize(tmprow);
            if (COMPRESSION_LEVEL == 1)
                compressedRow = tmp;
            else if (COMPRESSION_LEVEL == 2) {
                byte[] comp = manager.compressFast(tmp);
                rawSize = tmp.length;
                compressedRow = comp;
            }
        }
    }

    public void addCell(Cell cell) {
        if (COMPRESSION_LEVEL == 0) {
            row.cells.add(cell);
            this.cellCount = row.cells.size();
        } else {
            Row tmprow = getRow();
            tmprow.cells.add(cell);
            byte[] tmp = manager.serialize(tmprow);
            this.cellCount = tmprow.cells.size();
            if (COMPRESSION_LEVEL == 1)
                compressedRow = tmp;
            else if (COMPRESSION_LEVEL == 2) {
                byte[] comp = manager.compressFast(tmp);
                rawSize = tmp.length;
                compressedRow = comp;
            }
        }
    }

    public CellTuple getCellTuple(Project project) {
        return getRow().getCellTuple(project);
    }

    public void write(JSONWriter writer, Properties options)
            throws JSONException {
        getRow().write(writer, options);
    }

    public void save(Writer writer, Properties options) {
        getRow().save(writer, options);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        List<Cell> tmpcell = getRow().cells;
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
        Row tmprow = getRow();
        tmprow.flagged = flagged;
        if (COMPRESSION_LEVEL == 0)
            this.row = tmprow;
        else {
            byte[] tmp = manager.serialize(tmprow);
            if (COMPRESSION_LEVEL == 1)
                compressedRow = tmp;
            else if (COMPRESSION_LEVEL == 2) {
                byte[] comp = manager.compressFast(tmp);
                rawSize = tmp.length;
                compressedRow = comp;
            }
        }
    }

    public boolean getFlagged() {
        return getRow().flagged;
    }

    public void setStarred(boolean starred) {
        Row tmprow = getRow();
        tmprow.starred = starred;
        if (COMPRESSION_LEVEL == 0)
            this.row = tmprow;
        else {
            byte[] tmp = manager.serialize(tmprow);
            if (COMPRESSION_LEVEL == 1)
                compressedRow = tmp;
            else if (COMPRESSION_LEVEL == 2) {
                byte[] comp = manager.compressFast(tmp);
                rawSize = tmp.length;
                compressedRow = comp;
            }
        }
    }

    public boolean getStarred() {
        return getRow().starred;
    }

    public int getCellCount() {
        return this.cellCount;
    }

    public static List<CompressedRow> toCompressedList(List<Row> list) {
        List<CompressedRow> retList = new ArrayList<CompressedRow>();
        for (Row row : list) {
            retList.add(new CompressedRow(row));
        }
        return retList;
    }
}
