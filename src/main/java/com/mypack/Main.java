package com.mypack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    static int[] parent;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        if (args.length != 1) {
            System.out.println("Usage: java -jar app.jar <input_file_path>");
            return;
        }

        String inputFilePath = args[0];
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFilePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidLine(line)) {
                    lines.add(line);
                }
            }
        }

        int n = lines.size();
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        Map<String, Integer> valueToIndex = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String[] values = lines.get(i).split(";", -1);
            for (int j = 0; j < values.length; j++) {
                String value = values[j].trim().replace("\"", "");
                if (!value.isEmpty()) {
                    String key = value + "_" + j;
                    if (valueToIndex.containsKey(key)) {
                        union(i, valueToIndex.get(key));
                    } else {
                        valueToIndex.put(key, i);
                    }
                }
            }
        }

        Map<Integer, Set<String>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = find(i);
            groups.computeIfAbsent(root, k -> new LinkedHashSet<>()).add(lines.get(i));
        }


        List<Set<String>> resultGroups = new ArrayList<>();
        for (Set<String> group : groups.values()) {
            if (group.size() > 1) {
                resultGroups.add(group);
            }
        }


        resultGroups.sort((a, b) -> Integer.compare(b.size(), a.size()));


        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get("output.txt")))) {
            writer.println("Групп с более чем одним элементом: " + resultGroups.size());
            int groupNum = 1;
            for (Set<String> group : resultGroups) {
                writer.println();
                writer.println("Группа " + groupNum++);
                for (String s : group) {
                    writer.println(s);
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Групп с более чем одним элементом: " + resultGroups.size());
        System.out.println("Время выполнения: " + (end - start) + " мс");
    }

    static boolean isValidLine(String line) {
        String[] parts = line.split(";");
        for (String part : parts) {
            int quoteCount = part.length() - part.replace("\"", "").length();
            if (quoteCount % 2 != 0) return false;
        }
        return true;
    }

    static int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    static void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA != rootB) {
            parent[rootB] = rootA;
        }
    }
}