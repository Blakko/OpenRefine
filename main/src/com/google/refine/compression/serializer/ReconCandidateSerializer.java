
package com.google.refine.compression.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.ReconCandidate;

public class ReconCandidateSerializer extends Serializer<ReconCandidate> {

    @Override
    public void write(Kryo kryo, Output output, ReconCandidate recon) {
        output.writeString(recon.id);
        output.writeString(recon.name);
        kryo.writeObject(output, recon.types);
        output.writeDouble(recon.score);
    }

    @Override
    public ReconCandidate read(Kryo kryo, Input input, Class<ReconCandidate> paramClass) {
        String id = input.readString();
        String name = input.readString();
        String[] types = kryo.readObject(input, String[].class);
        double score = input.readDouble();
        
        ReconCandidate recon = new ReconCandidate(id, name, types, score);
        kryo.reference(recon);
        return recon;
    }

}
