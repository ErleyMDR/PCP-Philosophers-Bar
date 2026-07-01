package PhilosophersBar.src.chandyMisra;

import PhilosophersBar.src.Philosopher;
import PhilosophersBar.src.SharedResource;

public class CMBottle extends SharedResource {

    private boolean isFull;

    public CMBottle(Philosopher[] endpoints, Philosopher holder) {
        super(endpoints);
        this.isFull = false;
        this.holder = holder;

        System.out.printf(
                "[INIT] Bottle created between Philosopher %s and Philosopher %s. Initial holder: Philosopher %s%n",
                endpoints[0].getName(),
                endpoints[1].getName(),
                holder.getName()
        );
    }

    public boolean isFull() {
        return isFull;
    }

    public synchronized void fill() {
        if (!isFull) {
            isFull = true;

            System.out.printf(
                    "[BOTTLE] Philosopher %s filled the bottle.%n",
                    holder.getName()
            );
        } else {
            throw new IllegalStateException("Bottle is already full");
        }
    }

    public synchronized void empty() {
        if (!isFull) return;

        isFull = false;

        System.out.printf(
                "[BOTTLE] Philosopher %s emptied the bottle after drinking.%n",
                holder.getName()
        );

    }

    public synchronized void transfer(CMPhilosopher from, CMPhilosopher to) {

        if (holder != from) {
            throw new IllegalStateException(
                    "Transfer attempted by philosopher that does not hold the bottle.");
        }

        System.out.printf(
                "[TRANSFER] Philosopher %s is transferring a %s bottle to Philosopher %s%n",
                from.getName(),
                isFull ? "FULL" : "EMPTY",
                to.getName()
        );

        holder = to;

        System.out.printf(
                "[TRANSFER] Bottle ownership changed. New holder: Philosopher %s%n",
                to.getName()
        );

        notifyAll();
    }
}