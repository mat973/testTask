package com.mypack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    static int[] parent;

    record Key(String value, int column) {}

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        if (args.length != 1) {
            System.out.println("Usage: java -jar app.jar <input_file_path>");
            return;
        }
        String inputFilePath = args[0];
        Set<String> uniqueLinesParse = new LinkedHashSet<>();


        if (inputFilePath.toLowerCase().endsWith(".zip")) {
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFilePath), StandardCharsets.UTF_8)) {
                ZipEntry entry = zis.getNextEntry();
                if (entry == null) {
                    System.out.println("Archive is empty");
                    return;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isValidLine(line)) {
                            uniqueLinesParse.add(line);
                        }
                    }
                }
            }
        } else if (inputFilePath.toLowerCase().endsWith(".gz")) {
            try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(inputFilePath));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(gzis, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isValidLine(line)) uniqueLinesParse.add(line);
                }
            }
        } else {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(inputFilePath), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (isValidLine(line)) {
                        uniqueLinesParse.add(line);
                    }
                }
            }
        }

        List<String> lines = new ArrayList<>(uniqueLinesParse);

        int n = lines.size();
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        Map<Key, Integer> valueToIndex = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String[] parts = lines.get(i).split(";", -1);
            for (int j = 0; j < parts.length; j++) {
                String val = parts[j].trim().replace("\"", "");
                if (val.isEmpty()) continue;

                Key key = new Key(val, j);
                if (valueToIndex.containsKey(key)) {
                    union(i, valueToIndex.get(key));
                } else {
                    valueToIndex.put(key, i);
                }
            }
        }

        int[] groupSizes = new int[n];
        for (int i = 0; i < n; i++) {
            int root = find(i);
            groupSizes[root]++;
        }

        int groupCount = 0;
        for (int size : groupSizes) {
            if (size > 1) groupCount++;
        }

        int[][] groupsArrays = new int[groupCount][];
        int idx = 0;
        int[] rootToGroupIndex = new int[n];
        for (int i = 0; i < n; i++) {
            if (groupSizes[i] > 1) {
                groupsArrays[idx] = new int[groupSizes[i]];
                rootToGroupIndex[i] = idx++;
            } else {
                rootToGroupIndex[i] = -1;
            }
        }

        int[] currentIndices = new int[groupCount];
        for (int i = 0; i < n; i++) {
            int root = find(i);
            int groupIdx = rootToGroupIndex[root];
            if (groupIdx != -1) {
                groupsArrays[groupIdx][currentIndices[groupIdx]++] = i;
            }
        }


        Arrays.sort(groupsArrays, (a, b) -> Integer.compare(b.length, a.length));

        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            writer.println("Групп с более чем одним элементом: " + groupsArrays.length);
            for (int i = 0; i < groupsArrays.length; i++) {
                writer.println();
                writer.println("Группа " + (i + 1));
                for (int idxInGroup : groupsArrays[i]) {
                    writer.println(lines.get(idxInGroup));
                }
            }
        }


        long end = System.currentTimeMillis();
        System.out.println("Групп с более чем одним элементом: " + groupsArrays.length);
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
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    static void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA != rootB) parent[rootB] = rootA;
    }
}
