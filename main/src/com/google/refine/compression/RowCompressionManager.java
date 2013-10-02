
package com.google.refine.compression;

import java.util.ArrayList;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeInput;
import com.esotericsoftware.kryo.io.UnsafeOutput;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.util.DefaultClassResolver;
import com.esotericsoftware.kryo.util.FastestStreamFactory;
import com.esotericsoftware.kryo.util.MapReferenceResolver;

import com.google.refine.compression.serializer.CellSerializer;
import com.google.refine.compression.serializer.ReconCandidateSerializer;
import com.google.refine.compression.serializer.ReconSerializer;
import com.google.refine.compression.serializer.RowSerializer;
import com.google.refine.model.Cell;
import com.google.refine.model.Recon;
import com.google.refine.model.ReconCandidate;
import com.google.refine.model.Row;

public class RowCompressionManager {

    private final Kryo kryo;
    private final Input input;
    private final Output out;
    private final LZ4Compressor fastComp;
    private final LZ4FastDecompressor fastDeco;
    private LZ4Factory factory;

    public RowCompressionManager() {
        synchronized (this) {
            out = new UnsafeOutput(4096, 65540);
            input = new UnsafeInput();
            factory = LZ4Factory.fastestInstance();
            fastComp = factory.fastCompressor();
            fastDeco = factory.fastDecompressor();

            kryo = new Kryo(new DefaultClassResolver(), new MapReferenceResolver(), new FastestStreamFactory());
            kryo.addDefaultSerializer(ArrayList.class, CollectionSerializer.class);
            kryo.register(byte[].class);
            kryo.register(String[].class);
            kryo.register(Row.class, new RowSerializer());
            kryo.register(Cell.class, new CellSerializer());
            kryo.register(Recon.class, new ReconSerializer());
            kryo.register(ReconCandidate.class, new ReconCandidateSerializer());
        }
    }

    synchronized public byte[] serialize(Row row) {
        out.clear();
        kryo.writeObject(out, row);
        return out.toBytes();
    }

    synchronized public Row deserialize(byte[] byt) {
        input.setBuffer(byt);
        return kryo.readObject(input, Row.class);
    }

    synchronized public byte[] compressFast(byte[] original) {
        return fastComp.compress(original);
    }

    synchronized public byte[] decompressFast(byte[] compressed, int size) {
        return fastDeco.decompress(compressed, size);
    }
}
