package me.brennan.footlocker.util;

import java.io.File;
import java.io.FileWriter;
import java.util.StringJoiner;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class CSVWriter {
    private final File file;

    public CSVWriter(String fileName) {
        this.file = new File(fileName);
    }

    public void write(String[] newData) {
        try {
            final FileWriter fileWriter = new FileWriter(file, true);

            final StringJoiner stringJoiner = new StringJoiner(",");
            for(String data : newData) {
                stringJoiner.add(data);
            }
            fileWriter.write(stringJoiner + "\n");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
