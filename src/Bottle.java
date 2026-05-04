package PhilosophersBar.src;

public class Bottle {

    private boolean isFull;

    public Bottle() {
        this.isFull = true;
    }

    public boolean isFull() {
        return isFull;
    }

    public void fill() {
        if (!isFull) {
            this.isFull = true;
        } else throw new IllegalStateException("Bottle is already full");
    }
}
