package com.google.refine.compression.serializer;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Cell;
import com.google.refine.model.Row;


public class RowSerializer extends Serializer<Row>{

    @Override
    public void write(Kryo kryo, Output output, Row row) {
        output.writeBoolean(row.flagged);
        output.writeBoolean(row.starred);
        kryo.writeObject(output, row.cells);
    }

    @Override
    public Row read(Kryo kryo, Input input, Class<Row> row) {
        boolean flagged = input.readBoolean();
        boolean starred = input.readBoolean();
        @SuppressWarnings("unchecked")
        ArrayList<Cell> cells = kryo.readObject(input, ArrayList.class);;

        Row newrow = new Row(cells.size());
        newrow.cells.addAll(cells);
        newrow.flagged = flagged;
        newrow.starred = starred;
        
        return newrow;
    }



}
