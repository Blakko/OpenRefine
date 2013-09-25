
package com.google.refine.compression;

import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Cell;
import com.google.refine.model.Recon;
import com.google.refine.model.ReconCandidate;

public class RowManager {

    private final Kryo kryo;
    private final Output out;
    private Input input;

    public RowManager() {
        out = new Output(4096, 12400);
        kryo = new Kryo();
        kryo.register(List.class);
        kryo.register(ArrayList.class);
        kryo.register(Cell.class);
        kryo.register(Recon.class);
        kryo.register(ReconCandidate.class);
    }

    public byte[] serialize(List<Cell> list) {
        kryo.writeObject(out, list);

        out.clear();
        return out.toBytes();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Cell> deserialize(byte[] byt) {
        input = new Input(byt);
        ArrayList<Cell> list = (ArrayList<Cell>) kryo.readObject(input, ArrayList.class);
        input.close();
        return list;
    }    
}
