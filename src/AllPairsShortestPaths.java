import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class AllPairsShortestPaths {
    static final int INF = 999999999; // Don't use Integer.MAX_VALUE because this results in errors due to overflow

    public static void main(String[] args) {
//        sanityCheck();

        int n = 10;

        // Run the same random sparse graph 5 times
        System.out.println("Sparse graph of size: " + n);
        int[][] sparseGraph = generateGraphOfSizeN(n, 0.4);
        printMatrix(sparseGraph);
        run(sparseGraph, n);
        run(sparseGraph, n);
        run(sparseGraph, n);
        run(sparseGraph, n);
        run(sparseGraph, n);

        System.out.println();

        // Run the same random dense graph 5 times
        System.out.println("Dense graph of size: " + n);
        int[][] denseGraph = generateGraphOfSizeN(n, 0.85);
        printMatrix(denseGraph);
        run(denseGraph, n);
        run(denseGraph, n);
        run(denseGraph, n);
        run(denseGraph, n);
        run(denseGraph, n);
    }
    
    public static void run(int[][] graph, int n) {
        // Run Dijkstra
        System.out.println("Dijkstra's all pairs shortest paths:");
        long startTime = System.nanoTime();
        int[][] dijkstraResult = dijkstraAllPairs(graph);
        System.out.printf("Dijkstra elapsed time for n=%d: %f seconds\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        printMatrix(dijkstraResult);

        // Run Floyd-Warshall
        System.out.println("Floyd-Warshall's all pairs shortest paths:");
        startTime = System.nanoTime();
        int[][] floydWarshallResult = floydWarshall(graph);
        System.out.printf("Floyd-Warshall elapsed time for n=%d: %f seconds\n", n, (System.nanoTime()-startTime)/(float)1000000000);
        printMatrix(floydWarshallResult);

        // Compare the results of the two. If they are different, something is definitely wrong
        System.out.printf("Floyd-Warshall == Dijkstra: %b\n\n", Arrays.deepEquals(dijkstraResult, floydWarshallResult));
    }

    public static void sanityCheck() {
        int[][] graph = new int[][] {
                {0,9,INF,INF,INF},
                {INF,0,9,INF,4},
                {5,INF,0,5,INF},
                {8,INF,INF,0,INF},
                {7,8,1,8,0}
        };

        System.out.println("Sanity check with graph of size: " + 5);
        printMatrix(graph);
        run(graph, 5);
    }

    public static int[][] floydWarshall(int[][] graph) {
        int n = graph.length;
        int[][] shortestPaths = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(graph[i], 0, shortestPaths[i], 0, n);
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    shortestPaths[i][j] = Integer.min(shortestPaths[i][j], shortestPaths[i][k] + shortestPaths[k][j]);
                }
            }
        }
        return shortestPaths;
    }

    public static int[][] dijkstraAllPairs(int[][] graph) {
        int[][] shortestPaths = new int[graph.length][graph.length];
        for (int i = 0; i < graph.length; i++) {
            dijkstra(graph, shortestPaths, i);
        }
        return shortestPaths;
    }

    public static void dijkstra(int[][] graph, int[][] shortestPaths, int startingNode) {
        int n = graph.length;
        int[] dist = new int[n];
//        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dist[i] = graph[startingNode][i];
//            prev[i] = startingNode;
            visited[i] = false;
        }
        visited[startingNode] = true;

        for (int i = 0; i < n-1; i++) {
            int minNotVisited = unvisitedNodeWithMinDist(dist, visited);
            visited[minNotVisited] = true;

            for (int j = 0; j < n; j++) {
                if (!visited[j] & dist[j] > dist[minNotVisited] + graph[minNotVisited][j]) {
                    dist[j] = dist[minNotVisited] + graph[minNotVisited][j];
//                    prev[j] = minNotVisited;
                }
            }
        }

        System.arraycopy(dist, 0, shortestPaths[startingNode], 0, n);
    }

    public static int unvisitedNodeWithMinDist(int[] dist, boolean[] visited) {
        int minDist = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < dist.length; i++) {
            if (!visited[i] & dist[i] < minDist) {
                minDist = dist[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    // ----------------------------------------------------
    // ----------------- HELPER FUNCTIONS -----------------
    // ----------------------------------------------------

    public static int[][] generateGraphOfSizeN(int n, double density) {
        int[][] graph = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int weight = ThreadLocalRandom.current().nextInt(1, 10); // randomly generate a weight
                boolean edge = ThreadLocalRandom.current().nextFloat()%1 < density; // determine whether to add an edge
                graph[i][j] = edge ? weight : INF;
            }
            graph[i][i] = 0;
        }
        return graph;
    }

    // Formats and prints matrix/graph
    public static void printMatrix(int[][] matrix) {
        for (int[] rows : matrix) {
            for (int elem : rows)
                if (elem == INF) {
                    System.out.print("   ∞");
                } else {
                    System.out.printf("%4d", elem);
                }
            System.out.println();
        }
        System.out.println();
    }
}
