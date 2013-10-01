
package com.google.refine.compression;

import java.io.Serializable;
import java.util.ArrayList;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;

import com.google.refine.compression.serializer.ReconCandidateSerializer;
import com.google.refine.compression.serializer.ReconSerializer;
import com.google.refine.compression.serializer.RowSerializer;
import com.google.refine.model.Cell;
import com.google.refine.model.Recon;
import com.google.refine.model.Recon.Judgment;
import com.google.refine.model.ReconCandidate;
import com.google.refine.model.Row;

public class RowCompressionManager {

    private Kryo kryo;
    private Input input;
    private final Output out;
    private final LZ4Compressor fastComp;
    private final LZ4FastDecompressor fastDeco;
    static private LZ4Factory factory;

    public RowCompressionManager() {
        synchronized (this) {
            out = new Output(4096, 65540);
            factory = LZ4Factory.fastestInstance();
            fastComp = factory.fastCompressor();
            fastDeco = factory.fastDecompressor();

            kryo = new Kryo();
            kryo.addDefaultSerializer(ArrayList.class, CollectionSerializer.class);
            kryo.register(Row.class, new RowSerializer());
            kryo.register(Cell.class);
            kryo.register(Recon.class, new ReconSerializer());
            kryo.register(ReconCandidate.class, new ReconCandidateSerializer());
            kryo.register(Serializable.class);
            kryo.register(Judgment.class);
            kryo.register(Object.class);
        }
    }

    synchronized public byte[] serialize(Row row) {
        kryo.writeObject(out, row);
        byte[] result = out.toBytes();
        out.clear();
        return result;
    }

    synchronized public Row deserialize(byte[] byt) {
        input = new Input(byt);
        Row row = kryo.readObject(input, Row.class);
        input.close();
        return row;
    }

    synchronized public byte[] compressFast(byte[] original) {
        return fastComp.compress(original);
    }

    synchronized public byte[] decompressFast(byte[] compressed, int size) {
        return fastDeco.decompress(compressed, size);
    }
}
