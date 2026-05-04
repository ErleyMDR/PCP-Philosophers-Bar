package PhilosophersBar.src;

import java.util.Arrays;
import java.util.HashMap;

public class Graph extends HashMap<String, Bottle[]> {

    public static Graph create(int[][] adjMat) {
        Graph graph = new Graph();
        for (int i = 0; i < adjMat.length; i++) {
            Bottle[] arestas = (Bottle[]) Arrays.stream(adjMat[i])
                    .mapToObj(aresta -> aresta == 1 ? new Bottle() : null).toArray();
            graph.put(""+i, arestas);
        }

        if (graph.isEmpty()) {
            System.out.println("========== Graph creation failed ==========");
            throw new RuntimeException("Graph creation failed");
        }

        int edges = graph.values().stream().mapToInt(v -> v.length).sum();
        System.out.printf("============ Graph created with %s vertices and %s edges ============%n",
                graph.size(), edges+"");
        return graph;
    }


}
