package PhilosophersBar.src;

import PhilosophersBar.src.enums.PhilosopherState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Philosopher extends Thread {

    protected PhilosopherState state;
    protected Map<Philosopher, SharedResource> connections;
    protected Set<SharedResource> holdingBottles;
    protected int numberOfRequiredBottles;
    protected int timesDrank;
    protected final int maxDrinks;

    protected long idleTime;
    protected long drinkingTime;
    protected long thirstyTime;

    public Philosopher(String name, int maxDrinks) {
        super(name);
        this.connections = new HashMap<>();
        this.holdingBottles = ConcurrentHashMap.newKeySet();
        this.state = PhilosopherState.IDLE;
        this.maxDrinks = maxDrinks;
        this.timesDrank = 0;
    }

    public PhilosopherState getPhilosopherState() {
        return state;
    }

    public Collection<SharedResource> getAvailableBottles() {
        return this.connections.values();
    }

    protected void generateRandomNumberOfBottles() {
        Random randGen = new Random();
        int n = this.connections.size();
        this.numberOfRequiredBottles = randGen.nextInt(2, n + 1);
    }

    protected void drink() {
        if (this.numberOfRequiredBottles == holdingBottles.size()) {
            try {
                sleep(1000);
                this.timesDrank++;
            } catch (InterruptedException e) {}

        } else {
            throw new IllegalStateException("Number of holding bottles is different from required number when drinking");
        }
    }

    public void addConnection(Philosopher p, SharedResource b) {
        this.connections.put(p, b);
    }

    public Map<Philosopher, SharedResource> getConnections() {
        return connections;
    }

    public void changeState() {
        long now = System.nanoTime();
        switch (state) {
            case IDLE:
                idle();
                this.idleTime += System.nanoTime() - now;
                this.state = PhilosopherState.THIRSTY;
                break;

            case THIRSTY:
                thirsty();
                this.thirstyTime += System.nanoTime() - now;
                this.state = PhilosopherState.DRINKING;
                break;

            case DRINKING:
                drinking();
                this.drinkingTime += System.nanoTime() - now;
                this.state = PhilosopherState.IDLE;
                break;
        }
    }

    protected abstract void idle();
    protected abstract void thirsty();
    protected abstract void drinking();

    @Override
    public void run() {
        long startTime = System.nanoTime();
        while (this.timesDrank < this.maxDrinks) {
            changeState();
        }
        long execTime = System.nanoTime() - startTime;

        System.out.printf("Philosopher %s has ended execution in %sms", this.getName(), execTime / 1_000_000);
        System.out.printf(" - Time spent in each state: \n Idle: %s \n Thirsty: %s \n Drinking: %s\n",
                this.idleTime / 1_000_000,
                this.thirstyTime / 1_000_000,
                this.drinkingTime / 1_000_000);
    }
}
