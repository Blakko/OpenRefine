
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

    private long judgmentHistoryEntry;
    private String identifierSpace, schemaSpace;
    private Recon newrecon;

    @Override
    public void write(Kryo kryo, Output output, Recon recon) {
        output.writeLong(recon.judgmentHistoryEntry);
        output.writeString(recon.identifierSpace);
        output.writeString(recon.schemaSpace);

        output.writeLong(recon.id);
        output.writeString(recon.service);

        kryo.writeObject(output, recon.features);
        kryo.writeObjectOrNull(output, recon.candidates, ArrayList.class);
        kryo.writeObject(output, recon.judgment);

        output.writeString(recon.judgmentAction);

        output.writeInt(recon.judgmentBatchSize);
        kryo.writeObjectOrNull(output, recon.match, ReconCandidate.class);
        output.writeInt(recon.matchRank);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Recon read(Kryo kryo, Input input, Class<Recon> recon) {
        judgmentHistoryEntry = input.readLong();
        identifierSpace = input.readString();
        schemaSpace = input.readString();

        newrecon = new Recon(judgmentHistoryEntry, identifierSpace, schemaSpace);

        newrecon.id = input.readLong();
        newrecon.service = input.readString();
        newrecon.features = kryo.readObject(input, Object[].class);
        newrecon.candidates = kryo.readObjectOrNull(input, ArrayList.class);
        newrecon.judgment = kryo.readObject(input, Judgment.class);
        newrecon.judgmentAction = input.readString();
        newrecon.judgmentBatchSize = input.readInt();
        newrecon.match = kryo.readObjectOrNull(input, ReconCandidate.class);
        newrecon.matchRank = input.readInt();

        return newrecon;
    }
}
