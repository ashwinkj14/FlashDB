package org.ashwin.flash.database.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class SSTable {

    private final File file;
    private final long sequenceNumber;

    public record Pair(String key, byte[] value) {}

    public SSTable(long sequenceNumber, String filePath) {
        this.sequenceNumber = sequenceNumber;
        this.file = new File(filePath);
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public String getFileName() {
        return file.getName();
    }

    public void delete() {
        file.delete();
    }

    public void write(Map<String, byte[]> data) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
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
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
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

    public Map<String, byte[]> readAll() {
        Map<String, byte[]> map = new TreeMap<>();
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            while (in.available() > 0) {
                String key = in.readUTF();
                int len = in.readInt();
                byte[] val = new byte[len];
                in.readFully(val);
                map.put(key, val);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read SSTable", e);
        }
        return map;
    }


}
