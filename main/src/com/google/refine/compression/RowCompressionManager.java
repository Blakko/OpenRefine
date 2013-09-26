
package com.google.refine.compression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Cell;
import com.google.refine.model.Recon;
import com.google.refine.model.ReconCandidate;
import com.google.refine.model.Row;

public class RowCompressionManager {

    private final Kryo kryo;
    private final Output out;
    private Input input;

    public RowCompressionManager() {
        out = new Output(4096, 12400);
        kryo = new Kryo();
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Row.class);
        kryo.register(Cell.class);
        kryo.register(Recon.class);
        kryo.register(ReconCandidate.class);
        kryo.register(Serializable.class);
    }

    public byte[] serialize(Row row, int compression) {
        kryo.writeObject(out, row);
        byte[] result = out.toBytes();
        out.clear();
        if (compression == 1) // basic serialization
            return result;
        return null;
    }

    public Row deserialize(byte[] byt, int compression) {
        if (compression == 1) // basic serialization
            input = new Input(byt);
        Row row = kryo.readObject(input, Row.class);
        input.close();
        return row;
    }
}
