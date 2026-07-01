package PhilosophersBar.src;

import java.io.IOException;
import java.util.Scanner;

public class Application {
    private static boolean continueApp;
    private static final Scanner INPUT = new Scanner(System.in);

    private static void showMenu1() {
        System.out.println("\n");
        System.out.println("=".repeat(60));
        System.out.println("=== \tWelcome to Philosophers Bar Problem Application!\t ===");
        System.out.println("==========      \tTo start the application\t      ==========");
        System.out.println("==========\tChoose one of the following options:\t==========");
        System.out.println("\n");
        System.out.println("\t1. Execute application for select test cases.");
        System.out.println("\t2. Execute application for custom test case.");
        System.out.println("\t0. Exit application.");
        System.out.println("\n");

        int opcao = INPUT.nextInt();
        INPUT.nextLine();

        switch (opcao) {
            case 1:
                System.out.println("\n");
                System.out.println("=".repeat(60));
                System.out.println("\tChoose one of the following cases:");
                System.out.println("\t1. Case 1 - 5 vertices and 10 edges");
                System.out.println("\t2. Case 2 - 6 vertices and 16 edges");
                System.out.println("\t3. Case 3 - 12 vertices and 43 edges");
                System.out.println("\t0. Return. ");
                System.out.println("\n");

                int opcaoTestCases = INPUT.nextInt();
                INPUT.nextLine();

                int solOpt = showMenu2();
                if (solOpt == 0) break;

                switch (opcaoTestCases) {
                    case 1 -> runApp("case1.txt", solOpt,6);
                    case 2 -> runApp("case2.txt", solOpt,6);
                    case 3 -> runApp("case3.txt", solOpt,3);
                    case 0 -> {}
                    default -> System.out.println("\nInvalid option.\n");
                }

                break;

            case 2:
                System.out.println("\n");
                System.out.println("=".repeat(60));
                System.out.println("\tPlease, enter the name of the custom test case file (.txt file):");
                System.out.println("\n");

                String fileName = INPUT.nextLine();
                INPUT.nextLine();
                int solutionOpt = showMenu2();
                int maxDrinks = showMenu3();
                runApp(fileName, solutionOpt, maxDrinks);
                break;

            case 0:
                continueApp = false;
                break;

            default:
                System.out.println("\nInvalid option.\n");
        }

    }

    private static int showMenu2() {
        System.out.println("".repeat(60));
        System.out.println("===== \t Choose a solution to run: \t =====");
        System.out.println("\n");
        System.out.println("\t 1. Chandy-Misra solution. ");
        System.out.println("\t 2. Waiter solution. ");
        System.out.println("\t 0. Return. ");
        System.out.println("\n");
        System.out.println("=".repeat(60));
        System.out.println("\n");

        int opcao = INPUT.nextInt();
        INPUT.nextLine();

        return opcao;
    }

    private static int showMenu3() {
        System.out.println("".repeat(60));
        System.out.println("\n");
        System.out.println("===== \t Input the maximum number of drinks for Philosophers: \t =====");
        System.out.println("\n");
        System.out.println("=".repeat(60));
        System.out.println("\n");

        int maxDrinks = INPUT.nextInt();
        INPUT.nextLine();

        return maxDrinks;
    }

    private static void runApp(String fileName, int solution, int maxDrinks) {
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
                System.out.println("File " + fileName + " does not contain a valid graph for this problem.\n");
                return;
            }

            long startTime = System.nanoTime();
            Graph graph;
            switch (solution) {
                case 1:
                    graph = Graph.create(adjMat, maxDrinks, "CHANDY_MISRA");
                    for (Philosopher p : graph.getPhilosophers()) {
                        p.start();
                    }

                    for (Philosopher p : graph.getPhilosophers()) {
                        p.join();
                    }

                    break;

                case 2:
                    graph = Graph.create(adjMat, maxDrinks, "WAITER");
                    for (Philosopher p : graph.getPhilosophers()) {
                        p.start();
                    }

                    for (Philosopher p : graph.getPhilosophers()) {
                        p.join();

                    }

                    break;

                default:
                    throw new IllegalArgumentException("Invalid solution option");
            }

            long endTime = System.nanoTime();
            double execTime = (double) (endTime - startTime) / 1_000_000_000;
            long avgThirstyTime = graph.getPhilosophers().stream().mapToLong(p -> p.thirstyTime).sum();
            System.out.printf(" Tempo médio de sede (starvation): %sms\n", avgThirstyTime / 1_000_000);
            System.out.printf(" Tempo de execução final: %2f segundos.\n", execTime);
            System.out.println("===== \t FIM DA EXECUÇÃO \t =====");
            System.out.println("=".repeat(60));
            System.out.println("\n");

        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        continueApp = true;
        while (continueApp) {
            showMenu1();
        }

        INPUT.close();
    }
}
