package PhilosophersBar.src.waiter;

import PhilosophersBar.src.Philosopher;
import PhilosophersBar.src.SharedResource;

import java.util.*;

public class WPhilosopher extends Philosopher {

    private final Waiter waiter;

    public WPhilosopher(String name, int maxDrinks, Waiter waiter) {
        super(name, maxDrinks);
        this.waiter = waiter;
    }

    public void acquire(WBottle b) {
        if (getAvailableBottles().contains(b) && !holdingBottles.contains(b)) {
            b.acquire(this);
            this.holdingBottles.add(b);
        } else {
            throw new IllegalArgumentException("Philosopher tried to acquire bottle it either already holds or cannot acquire.");
        }
    }

    public void release(WBottle b) {
        if (getAvailableBottles().contains(b) && holdingBottles.contains(b)) {
            b.release(this);
            this.holdingBottles.remove(b);
        } else {
            throw new IllegalArgumentException("Philosopher tried to release bottle it doesn't or cannot hold.");
        }
    }

    public void acquireRequiredBottles(int requiredBottles) throws IllegalArgumentException {
        List<WBottle> WBottles = getAvailableBottles().stream().map(b -> (WBottle) b).toList();
        for (int i = 0; i < requiredBottles; i++) {
            WBottle b = WBottles.get(i);
            acquire(b);
        }
    }

    public void releaseAll() {
        for (SharedResource WBottle : new ArrayList<>(holdingBottles)) {
            release((WBottle) WBottle);
        }
    }

    @Override
    public void idle() {
        Random randGen = new Random();
        long random = randGen.nextInt(0, connections.size());
        try {
            sleep(1000 * random);
        } catch (InterruptedException e) {
        }
    }

    @Override
    protected void thirsty() {
        waiter.requestPermission(this);

        generateRandomNumberOfBottles();
        acquireRequiredBottles(this.numberOfRequiredBottles);
    }

    @Override
    protected void drinking() {
        drink();
        releaseAll();
        waiter.finishedDrinking(this);
    }
}
