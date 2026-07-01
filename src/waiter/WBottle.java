package PhilosophersBar.src.waiter;

import PhilosophersBar.src.Philosopher;
import PhilosophersBar.src.SharedResource;

import java.util.Arrays;

public class WBottle extends SharedResource {

    private int waitingPhilosophers;

    public WBottle(Philosopher[] endpoints) {
        super(endpoints);
        this.waitingPhilosophers = 0;
    }

    public synchronized void acquire(Philosopher philosopher) throws IllegalArgumentException{
        if (!Arrays.asList(endpoints).contains(philosopher)) {
            throw new IllegalArgumentException("Philosopher "+philosopher.getName()+" tried to acquire bottle it cannot acquire");
        }

        waitingPhilosophers++;
        while (this.holder != null) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }

        waitingPhilosophers--;
        this.holder = philosopher;
    }

    public synchronized void release(Philosopher philosopher) {
        if (this.holder != philosopher) {
            throw new IllegalArgumentException(
                    String.format("Philosopher %s tried to release bottle it does not hold", philosopher.getName()));
        }

        this.holder = null;
        notifyAll();
    }

    public int getWaitingPhilosophers() {
        return waitingPhilosophers;
    }

}
