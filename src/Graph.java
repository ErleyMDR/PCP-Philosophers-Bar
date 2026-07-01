package PhilosophersBar.src;

import PhilosophersBar.src.chandyMisra.CMBottle;
import PhilosophersBar.src.chandyMisra.CMPhilosopher;
import PhilosophersBar.src.waiter.WBottle;
import PhilosophersBar.src.waiter.WPhilosopher;
import PhilosophersBar.src.waiter.Waiter;

import java.util.ArrayList;
import java.util.List;

public class Graph {

    private List<Philosopher> philosophers;
    private List<SharedResource> sharedResources;

    public Graph() {
        this.philosophers = new ArrayList<>();
        this.sharedResources = new ArrayList<>();
    }

    public static Graph create(int[][] adjMat, int maxDrinks, final String solution) {
        Graph graph = new Graph();

        switch (solution) {
            case "WAITER":
                Waiter waiter = new Waiter(adjMat.length);
                for (int i = 0; i < adjMat.length; i++) {
                    graph.getPhilosophers().add(new WPhilosopher(i+1+"", maxDrinks, waiter));
                }

                for (int i = 0; i < adjMat.length; i++) {
                    Philosopher p1 = graph.getPhilosophers().get(i);

                    for (int j = i+1; j < adjMat.length; j++) {
                        if (adjMat[i][j] == 1) {
                            Philosopher p2 = graph.getPhilosophers().get(j);

                            WBottle wBottle = new WBottle(new Philosopher[]{p1, p2});

                            // Acrescenta à lista de vizinhos
                            p1.addConnection(p2, wBottle);
                            p2.addConnection(p1, wBottle);

                            graph.getSharedResources().add(wBottle);
                        }
                    }

                }
                break;

            case "CHANDY_MISRA":

                for (int i = 0; i < adjMat.length; i++) {
                    graph.getPhilosophers().add(new CMPhilosopher(i+1+"", maxDrinks));
                }

                for (int i = 0; i < adjMat.length; i++) {
                    Philosopher p1 = graph.getPhilosophers().get(i);

                    for (int j = i+1; j < adjMat.length; j++) {
                        if (adjMat[i][j] == 1) {
                            Philosopher p2 = graph.getPhilosophers().get(j);

                            CMBottle cmBottle = new CMBottle(new Philosopher[]{p1, p2}, p1);

                            // Acrescenta à lista de vizinhos
                            p1.addConnection(p2, cmBottle);
                            p2.addConnection(p1, cmBottle);

                            graph.getSharedResources().add(cmBottle);
                        }
                    }
                }
                break;

            case null, default:
                throw new IllegalStateException("String 'solution' is different from expected values.");
        }

        if (graph.getPhilosophers().isEmpty()) {
            System.out.println("========== Graph creation failed ==========");
            throw new RuntimeException("Graph creation failed");
        }

        System.out.printf("============ Graph created with %s vertices and %s edges ============%n",
                graph.getPhilosophers().size(), graph.getSharedResources().size());
        return graph;
    }

    public List<Philosopher> getPhilosophers() {
        return philosophers;
    }

    public void setPhilosophers(List<Philosopher> philosophers) {
        this.philosophers = philosophers;
    }

    public List<SharedResource> getSharedResources() {
        return sharedResources;
    }

    public void setSharedResources(List<SharedResource> sharedResources) {
        this.sharedResources = sharedResources;
    }

}
