package ru.practicum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String fileName = "lng.txt";
        List<String[]> splitLines = readFileAndValid(fileName);
        for (String[] splitLine : splitLines) {
            System.out.println(Arrays.toString(splitLine));
        }

    }

    public static List<String[]> readFileAndValid(String fileName){
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (isValidLine(line)){
                    lines.add(line.split(";"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static boolean isValidLine(String line) {
        long quotationCount = line.chars().filter(ch -> ch == '"').count();
        return quotationCount % 2 == 0;
    }
}
