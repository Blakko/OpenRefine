
package com.google.refine.compression.serializer;

import java.io.Serializable;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import com.google.refine.model.Cell;
import com.google.refine.model.Recon;

public class CellSerializer extends Serializer<Cell> {

    private Recon recon;
    private Serializable value;
    private Class valueType;

    @Override
    public void write(Kryo kryo, Output output, Cell cell) {
        if (cell.value == null) {
            kryo.writeObjectOrNull(output, null, Class.class);
        } else {
            kryo.writeObject(output, cell.value.getClass());
            kryo.writeObjectOrNull(output, cell.value, cell.value.getClass());
        }
        kryo.writeObjectOrNull(output, cell.recon, Recon.class);
    }

    @Override
    public Cell read(Kryo kryo, Input input, Class<Cell> cell) {
        valueType = kryo.readObjectOrNull(input, Class.class);

        /*
         * if (valueType == null) value = null; else
         */
        value = (Serializable) kryo.readObjectOrNull(input, valueType);

        recon = kryo.readObjectOrNull(input, Recon.class);

        return new Cell(value, recon);
    }
}
