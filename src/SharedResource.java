package PhilosophersBar.src;

public abstract class SharedResource {

    protected final Philosopher[] endpoints;
    protected Philosopher holder;

    protected SharedResource(Philosopher[] endpoints) {
        this.endpoints = endpoints;
    }

    public Philosopher[] getEndpoints() {
        return endpoints;
    }

    public Philosopher getHolder() {
        return holder;
    }
}
