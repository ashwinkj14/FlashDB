package org.ashwin.flash.database.engine;

import java.io.*;
import java.util.Map;

public class SSTable {

    private final String filePath;

    public SSTable(String filePath) {
        this.filePath = filePath;
    }

    public void write(Map<String, String> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        }
    }

    public String read(String key) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] keyValue = line.split(":");
                if (keyValue[0].equals(key)) {
                    key = keyValue[1];
                }
            }
        }
        return null;
    }

}
