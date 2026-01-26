package net.kapitencraft.kap_lib.core.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

public class ByteAccumulator extends OutputStream {
    private byte[] data = new byte[4096];
    private int index = 0;

    @Override
    public void write(int b) throws IOException {
        data[index] = (byte) b;
        if (++index >= data.length) {
            reallocate();
        }
    }

    private void reallocate() {
        byte[] dataNew = new byte[data.length * 2];
        System.arraycopy(data, 0, dataNew, 0, data.length);
        data = dataNew;
    }

    @Override
    public void write(byte @NotNull [] b) throws IOException {
        while (index + b.length > data.length) {
            reallocate();
        }
        System.arraycopy(b, 0, data, index, b.length);
        index += b.length;
    }

    public byte[] output() {
        byte[] output = new byte[index];
        System.arraycopy(data, 0, output, 0, index);
        return output;
    }

    @Override
    public String toString() {
        return "ByteAccumulator{size=" + this.data.length + ", index=" + this.index + "}";
    }
}
