package org.example.rangetree1d;

import java.util.*;

class RangeTree1D
{
    class Node
    {
        int value;
        Node left, right;

        Node(int value)
        {
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node root;

    public Node getRoot()
    {
        return root;
    }

    public RangeTree1D(int[] data)
    {
        //O(nlog(n))
        Arrays.sort(data);
        this.root = buildRangeTree(data, 0, data.length - 1);
    }

    private Node buildRangeTree(int[] data, int start, int end)
    {
        int mid = (start + end) / 2;

        //Liść znaleziony
        if (start == end)
        {
            Node leaf = new Node(data[mid]);
            //System.out.println("Liść: " + data[mid]);
            return leaf;
        }
        
        Node node = new Node(data[mid]);
        //System.out.println("Tworzenie węzła dla zakresu: " + start + " do " + end); // Debugowanie

        node.left = buildRangeTree(data, start, mid);
        node.right = buildRangeTree(data, mid + 1, end);
        return node;
    }

    static Set<Integer> matchingValues = new HashSet<>();
    static int counter = 0;

    public static void searchRange(Node root, int min, int max)
    {
        Map<Integer, String> longestPaths = new HashMap<>();
        searchRangeRec(root, min, max, "", longestPaths);

        longestPaths.forEach((key, value) ->
        {
            if (matchingValues.contains(key))
            {
                System.out.println(value);
            }
        });

        System.out.println("Total nodes visited: " + counter);
    }

    private static void searchRangeRec(Node node, int min, int max, String currentPath, Map<Integer, String> longestPaths)
    {
        if (node == null)
        {
            return;
        }
        counter++;
        String newPath = currentPath.isEmpty() ? String.valueOf(node.value) : currentPath + "->" + node.value;

        if (node.value >= min && node.value <= max)
        {
            //Aktualizuje mapę najdłuższych ścieżek, jeśli znaleziono nową dłuższą ścieżkę.
            if (!longestPaths.containsKey(node.value) || longestPaths.get(node.value).length() < newPath.length())
            {
                longestPaths.put(node.value, newPath);
            }
            matchingValues.add(node.value);
        }

        if (node.value >= min)
        {
            searchRangeRec(node.left, min, max, newPath, longestPaths);
        }
        if (node.value <= max)
        {
            searchRangeRec(node.right, min, max, newPath, longestPaths);
        }
    }

    /*
     * Złożoność obliczeniowa funkcji searchRangeRec zależy głównie od struktury drzewa i liczby elementów. W najlepszym
     * przypadku, gdy wartości min i max zawężają wyszukiwanie do jednego konkretnej części drzewa, złożoność
     * obliczeniowa może zbliżyć się do O(log(n)). Jednak w najgorszym przypadku, gdy musimy odwiedzić wiele węzłów
     * (np. gdy zakres min-max jest bardzo szeroki), złożoność może wzrosnąć do O(n).
     */
}
