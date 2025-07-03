package com.mypack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    static int[] parent;


    record Key(float value, int column) {}

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();

        if (args.length != 1) {
            System.out.println("Usage: java -jar app.jar <input_file_path>");
            return;
        }

        String inputFilePath = args[0];
        List<String> lines = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFilePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isValidLine(line)) {
                    lines.add(line);
                }
            }
        }

        int n = lines.size();
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;

        Map<Key, Integer> valueToIndex = new HashMap<>();


        for (int i = 0; i < n; i++) {
            String[] parts = lines.get(i).split(";", -1);
            for (int j = 0; j < parts.length; j++) {
                String val = parts[j].trim();
                if (val.isEmpty()) continue;

                if (val.startsWith("\"") && val.endsWith("\"") && val.length() > 1) {
                    val = val.substring(1, val.length() - 1);
                }

                try {
                    float f = Float.parseFloat(val);
                    float rounded = Math.round(f * 100.0f) / 100.0f;
                    Key key = new Key(rounded, j);
                    if (valueToIndex.containsKey(key)) {
                        union(i, valueToIndex.get(key));
                    } else {
                        valueToIndex.put(key, i);
                    }
                } catch (NumberFormatException ignored) {

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
                Set<String> uniqueLines = new LinkedHashSet<>();
                for (int idxInGroup : groupsArrays[i]) {
                    uniqueLines.add(lines.get(idxInGroup));
                }
                for (String uniqueLine : uniqueLines) {
                    writer.println(uniqueLine);
                }
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Group with more then one element: " + groupsArrays.length);
        System.out.println("Load time : " + (end - start) + " ms");
    }

    static boolean isValidLine(String line) {
        String[] parts = line.split(";");
        for (String part : parts) {
            int quotes = part.length() - part.replace("\"", "").length();
            if (quotes % 2 != 0) return false;
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
