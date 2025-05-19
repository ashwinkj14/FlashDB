package org.ashwin.flash.database.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class SSTable {

    private final String filePath;

    public SSTable(String filePath) {
        this.filePath = filePath;
    }

    public void write(Map<String, byte[]> data) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
            for (Map.Entry<String, byte[]> entry : data.entrySet()) {
                byte[] keyBytes = entry.getKey().getBytes();
                byte[] valueBytes = entry.getValue();

                dos.writeInt(keyBytes.length);
                dos.write(keyBytes);

                dos.writeInt(valueBytes.length);
                dos.write(valueBytes);
            }
        }
    }


    public Pair read(String targetKey) throws IOException {
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(Paths.get(filePath)))) {
            while (dis.available() > 0) {
                int keyLength = dis.readInt();
                byte[] keyBytes = new byte[keyLength];
                dis.readFully(keyBytes);
                String key = new String(keyBytes);

                int valueLength = dis.readInt();
                byte[] valueBytes = new byte[valueLength];
                dis.readFully(valueBytes);

                if (key.equals(targetKey)) {
                    return new Pair(key, valueBytes);
                }
            }
        }
        return null;
    }

    public record Pair(String key, byte[] value) {}

}
