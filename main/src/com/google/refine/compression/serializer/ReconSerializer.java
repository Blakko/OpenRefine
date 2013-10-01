
package com.google.refine.compression.serializer;

import java.util.ArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Recon;
import com.google.refine.model.Recon.Judgment;
import com.google.refine.model.ReconCandidate;

public class ReconSerializer extends Serializer<Recon> {

    @Override
    public void write(Kryo kryo, Output output, Recon recon) {
        output.writeLong(recon.id);
        output.writeString(recon.service);
        output.writeString(recon.identifierSpace);
        output.writeString(recon.schemaSpace);

        kryo.writeObject(output, recon.features);
        kryo.writeObject(output, recon.candidates);
        kryo.writeObject(output, recon.judgment);

        output.writeString(recon.judgmentAction);
        output.writeLong(recon.judgmentHistoryEntry);
        output.writeInt(recon.judgmentBatchSize);
        kryo.writeObject(output, recon.match);
        output.writeInt(recon.matchRank);
    }

    @Override
    public Recon read(Kryo kryo, Input input, Class<Recon> recon) {
        long id = input.readLong();
        String service = input.readString();
        String identifierSpace = input.readString();
        String schemaSpace = input.readString();

        Object[] features = kryo.readObject(input, Object[].class);
        @SuppressWarnings("unchecked")
        ArrayList<ReconCandidate> candidates = kryo.readObject(input, ArrayList.class);
        Judgment judgment = kryo.readObject(input, Judgment.class);

        String judgmentAction = input.readString();
        long judgmentHistoryEntry = input.readLong();
        int judgmentBatchSize = input.readInt();
        ReconCandidate match = kryo.readObject(input, ReconCandidate.class);
        int matchRank = input.readInt();

        Recon newrecon = new Recon(judgmentHistoryEntry, identifierSpace, schemaSpace);
        newrecon.id = id;
        newrecon.service = service;
        newrecon.features = features;
        newrecon.candidates = candidates;
        newrecon.judgment = judgment;
        newrecon.judgmentAction = judgmentAction;
        newrecon.judgmentBatchSize = judgmentBatchSize;
        newrecon.match = match;
        newrecon.matchRank = matchRank;
        
        return newrecon;

    }

}
