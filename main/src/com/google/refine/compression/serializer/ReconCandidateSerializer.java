
package com.google.refine.compression.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.ReconCandidate;

public class ReconCandidateSerializer extends Serializer<ReconCandidate> {

    private String id,name;
    private String[] types;
    private double score;
    private ReconCandidate recon;

    @Override
    public void write(Kryo kryo, Output output, ReconCandidate recon) {
        output.writeString(recon.id);
        output.writeString(recon.name);
        kryo.writeObject(output, recon.types);
        output.writeDouble(recon.score);
    }

    @Override
    public ReconCandidate read(Kryo kryo, Input input, Class<ReconCandidate> paramClass) {
        id = input.readString();
        name = input.readString();
        types = kryo.readObject(input, String[].class);
        score = input.readDouble();

        recon = new ReconCandidate(id, name, types, score);
        kryo.reference(recon);
        return recon;
    }

}
