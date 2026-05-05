package PhilosophersBar.src;

import java.io.IOException;
import java.util.Scanner;

public class Application {
    private static boolean continueApp;

    private static void showMenu() {
        System.out.println("=".repeat(50));
        System.out.println("=== \tWelcome to Philosophers Bar Problem Application!\t ===");
        System.out.println("==========      \tTo start the application\t      ==========");
        System.out.println("==========\tChoose one of the following options:\t==========");
        System.out.println("\n");
        System.out.println("\t1. Execute application for select test cases.");
        System.out.println("\t2. Execute application for custom test case.");
        System.out.println("\t0. Exit application.");
        System.out.println("\n");

        Scanner input = new Scanner(System.in);
        int opcao = input.nextInt();
        input.nextLine();

        switch (opcao) {
            case 1:
                System.out.println("\n");
                System.out.println("=".repeat(50));
                System.out.println("\tChoose one of the following cases:");
                System.out.println("\t1. Case 1 - 5 vertices and 10 edges");
                System.out.println("\t2. Case 2 - 6 vertices and 16 edges");
                System.out.println("\t3. Case 3 - 12 vertices and 43 edges");
                System.out.println("\t0. Return.");
                System.out.println("\n");

                int opcaoTestCases = input.nextInt();
                input.nextLine();
                switch (opcaoTestCases) {
                    case 1 -> runApp("case1.txt");
                    case 2 -> runApp("case2.txt");
                    case 3 -> runApp("case3.txt");
                    case 0 -> {}
                    default -> System.out.println("\nInvalid option.\n");
                }
                break;
            case 2:
                System.out.println("\n");
                System.out.println("=".repeat(50));
                System.out.println("\tPlease, enter the name of the custom test case file (.txt file):");
                System.out.println("\n");

                String fileName = input.nextLine();
                input.nextLine();
                runApp(fileName);
                break;
            case 0:
                continueApp = false;
                break;
            default:
                System.out.println("\nInvalid option.\n");
        }

        input.close();
    }

    private static void runApp(String fileName) {
        if (fileName.trim().isBlank() || !fileName.endsWith(".txt")) {
            System.out.println("\nInvalid file.\n");
            return;
        }

        try {
            GraphFileReader gfr = new GraphFileReader();
            int[][] adjMat = gfr.readFile(fileName);
            if (adjMat == null) {
                System.out.println("Adjacency Matrix is null.\n");
                return;
            }

            if (adjMat.length != 0 && adjMat[0].length != 0 && adjMat.length != adjMat[0].length) {
                System.out.println("File " + fileName + " does not contain valid graph for problem.\n");
                return;
            }

            Graph graph = Graph.create(adjMat);
            for (String p : graph.keySet()) {
                new Philosopher(p, graph.get(p).length).start();
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        continueApp = true;
        while (continueApp) {
            showMenu();
        }
    }
}
