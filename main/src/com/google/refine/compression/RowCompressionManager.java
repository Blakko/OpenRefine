
package com.google.refine.compression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Cell;
import com.google.refine.model.Recon;
import com.google.refine.model.Recon.Judgment;
import com.google.refine.model.ReconCandidate;
import com.google.refine.model.Row;

public class RowCompressionManager {

    private Kryo kryo;
    private Output out;
    private Input input;

    public RowCompressionManager() {
        init();
    }

    synchronized private void init() {
        out = new Output(4096, 12400);
        kryo = new Kryo();
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Row.class);
        kryo.register(Cell.class);
        kryo.register(Recon.class);
        kryo.register(ReconCandidate.class);
        kryo.register(Serializable.class);
        kryo.register(Judgment.class);
        kryo.register(String.class);
        kryo.register(boolean.class);
        kryo.register(Map.class);
        kryo.register(HashMap.class);
        kryo.register(Integer.class);
        kryo.register(Object.class);
        kryo.register(ReconCandidate.class);
    }

    synchronized public byte[] serialize(Row row, int compression) {
        kryo.writeObject(out, row);
        byte[] result = out.toBytes();
        out.clear();
        if (compression == 1) // basic serialization
            return result;
        return null;
    }

    synchronized public Row deserialize(byte[] byt, int compression) {
        if (compression == 1) // basic serialization
            input = new Input(byt);
        Row row = kryo.readObject(input, Row.class);
        input.close();
        return row;
    }
}
