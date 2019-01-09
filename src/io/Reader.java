package io;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Reader {

    public static String getCode(String path) throws Exception {

        if (!getFileExtension(path).equals("mcpp"))
            throw new Exception();

        String text = "";
        try (FileReader fileReader = new FileReader(path)) {
            char[] buf = new char[256];
            int c;
            while ((c = fileReader.read(buf)) > 0) {
                if (c <= 256) {
                    buf = Arrays.copyOf(buf, c);
                    text += String.valueOf(buf);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Файл " + path + " не найден");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text + "\n\0";

    }

    private static String getFileExtension(String filepath) {
        if (filepath.lastIndexOf(".") != -1 && filepath.lastIndexOf(".") != 0)
            return filepath.substring(filepath.lastIndexOf(".") + 1);
        else return "";
    }

}
