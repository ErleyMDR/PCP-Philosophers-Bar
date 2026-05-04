package PhilosophersBar.src;

import PhilosophersBar.src.enums.PhilosopherState;

import java.util.Random;

public class Philosopher extends Thread {

    private PhilosopherState state;
    private int neighbours;

    public Philosopher(String name, int neighbours) {
        super(name);
        this.state = PhilosopherState.BING_CHILLING;
        this.neighbours = neighbours;
    }

    public PhilosopherState getPhilosopherState() {
        return state;
    }

    public void nextState() {
        switch (state) {
            case BING_CHILLING -> {
                bingChilling();
                this.state = PhilosopherState.THIRSTY;
            }
            case THIRSTY -> this.state = PhilosopherState.DRINKING;
            case DRINKING -> this.state = PhilosopherState.BING_CHILLING;
            case null -> throw new IllegalStateException("Philosopher's state is null");
        }
    }

    private void bingChilling() {
        Random randGen = new Random();
        long random = randGen.nextInt(0, neighbours);
        try {
            sleep(1000 * random);
        } catch (InterruptedException e) {
        }
    }
}
