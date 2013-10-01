
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

    private long id, judgmentHistoryEntry;
    private String service, identifierSpace, schemaSpace, judgmentAction;
    private Object[] features;
    private ArrayList<ReconCandidate> candidates;
    private Judgment judgment;
    private int judgmentBatchSize, matchRank;
    private ReconCandidate match;
    private Recon newrecon;

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

    @SuppressWarnings("unchecked")
    @Override
    public Recon read(Kryo kryo, Input input, Class<Recon> recon) {
        id = input.readLong();
        service = input.readString();
        identifierSpace = input.readString();
        schemaSpace = input.readString();

        features = kryo.readObject(input, Object[].class);
        candidates = kryo.readObject(input, ArrayList.class);
        judgment = kryo.readObject(input, Judgment.class);

        judgmentAction = input.readString();
        judgmentHistoryEntry = input.readLong();
        judgmentBatchSize = input.readInt();
        match = kryo.readObject(input, ReconCandidate.class);
        matchRank = input.readInt();

        newrecon = new Recon(judgmentHistoryEntry, identifierSpace, schemaSpace);
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
