package PhilosophersBar.src.waiter;

public class Waiter {

    private final int maxDrinking;
    private int currentlyDrinking;
    private int currentlyWaiting;

    public Waiter(int numberOfPhilosophers) {
        this.maxDrinking = numberOfPhilosophers - 1;
        this.currentlyDrinking = 0;
        this.currentlyWaiting = 0;
    }

    public synchronized void requestPermission(WPhilosopher philosopher) {
        System.out.printf("Philosopher %s is requesting permission.%n", philosopher.getName());

        currentlyWaiting++;
        while (currentlyDrinking >= maxDrinking) {
            System.out.printf("Philosopher %s will wait as there are already %s philosophers drinking.%n", philosopher.getName(), maxDrinking);
            try {
                this.wait();
            } catch (InterruptedException e) {
            }
        }

        currentlyWaiting--;
        currentlyDrinking++;

        System.out.printf("Philosopher %s has been granted permission.%n", philosopher.getName());
    }

    public synchronized void finishedDrinking(WPhilosopher philosopher) {
        currentlyDrinking--;
        this.notifyAll();
        System.out.printf("Philosopher %s has finished drinking.%n", philosopher.getName());
    }
}
