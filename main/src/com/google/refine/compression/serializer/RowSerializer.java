
package com.google.refine.compression.serializer;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Cell;
import com.google.refine.model.Row;

public class RowSerializer extends Serializer<Row> {

    private Row newrow;
    private ArrayList<Cell> cells;

    @Override
    public void write(Kryo kryo, Output output, Row row) {
        kryo.writeObject(output, row.cells);
        output.writeBoolean(row.flagged);
        output.writeBoolean(row.starred);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Row read(Kryo kryo, Input input, Class<Row> row) {
        cells = kryo.readObject(input, ArrayList.class);

        newrow = new Row(cells.size());
        newrow.cells.addAll(cells);
        newrow.flagged = input.readBoolean();
        newrow.starred = input.readBoolean();

        return newrow;
    }
}
