package ru.practicum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        String fileName = "lng.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidLine(String line) {
        long quotationCount = line.chars().filter(ch -> ch == '"').count();
        return quotationCount % 2 == 0;
    }
}
