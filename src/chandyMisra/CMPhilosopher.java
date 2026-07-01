package PhilosophersBar.src.chandyMisra;

import PhilosophersBar.src.Philosopher;
import PhilosophersBar.src.SharedResource;
import PhilosophersBar.src.enums.PhilosopherState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.LockSupport;

public class CMPhilosopher extends Philosopher {

    private final Queue<Request> requests;
    private final List<SharedResource> requiredBottles;
    private final Set<Request> pendingRequests;

    private final Object stateLock = new Object();

    public CMPhilosopher(String name, int maxDrinks) {
        super(name, maxDrinks);
        this.requests = new ConcurrentLinkedQueue<>();
        this.requiredBottles = new ArrayList<>();
        this.pendingRequests = ConcurrentHashMap.newKeySet();

        System.out.printf(
                "[INIT] Philosopher %s created.%n",
                getName()
        );
    }

    public synchronized void processRequests() {

        if (!requests.isEmpty()) {
            System.out.printf(
                    "[REQUESTS] Philosopher %s is processing %d pending request(s).%n",
                    getName(),
                    requests.size()
            );
        }

        while (!requests.isEmpty()) {

            Request request = requests.peek();
            CMBottle bottle = request.requestedBottle();

            System.out.printf(
                    "[REQUESTS] Philosopher %s received a request from Philosopher %s.%n",
                    getName(),
                    request.requester().getName()
            );

            if (bottle.getHolder() == this) {

                System.out.printf(
                        "[REQUESTS] Philosopher %s currently owns the requested bottle.%n",
                        getName()
                );

                if (!bottle.isFull()) {
                    System.out.printf(
                            "[REQUESTS] Bottle is empty. Philosopher %s fills it before transferring.%n",
                            getName()
                    );
                    bottle.fill();
                }

                System.out.printf(
                        "[REQUESTS] Philosopher %s transfers the bottle to Philosopher %s.%n",
                        getName(),
                        request.requester().getName()
                );

                bottle.transfer(this, request.requester());
                requests.poll();
            } else {

                System.out.printf(
                        "[REQUESTS] Philosopher %s no longer owns the requested bottle. Waiting...%n",
                        getName()
                );

            }
        }
    }

    public void requestBottle(CMBottle bottle) {

        if (bottle.getHolder() == this) {
            System.out.printf(
                    "[REQUEST] Philosopher %s already owns the requested bottle.%n",
                    getName()
            );
            return;
        }

        CMPhilosopher holder = (CMPhilosopher) bottle.getHolder();

        if (!pendingRequests.add(new Request(holder, bottle))) {
            return;
        }

        System.out.printf(
                "[REQUEST] Philosopher %s requests a bottle from Philosopher %s.%n",
                getName(),
                holder.getName()
        );

        holder.receiveRequest(new Request(this, bottle));
    }

    public void receiveRequest(Request request) {

        requests.add(request);

        System.out.printf(
                "[REQUEST RECEIVED] Philosopher %s received a request from Philosopher %s.%n",
                getName(),
                request.requester().getName()
        );
    }

    private Queue<Request> getRequests() {
        return this.requests;
    }

    private void waitForBottles() {

        System.out.printf(
                "[WAIT] Philosopher %s is waiting for %d bottle(s).%n",
                getName(),
                numberOfRequiredBottles
        );

        while (!hasAllRequired()) {

            processRequests();

            for (SharedResource resource : requiredBottles) {

                CMBottle bottle = (CMBottle) resource;

                if (bottle.getHolder() != this) {
                    requestBottle(bottle);
                }
            }

            LockSupport.parkNanos(1_000_000);
        }

        System.out.printf(
                "[WAIT] Philosopher %s obtained all required bottles and is ready to drink.%n",
                getName()
        );
    }

    private List<CMBottle> getHeldBottles() {
        return connections.values().stream()
                .map(b -> (CMBottle) b)
                .filter(b -> b.getHolder() == this)
                .toList();
    }

    private boolean hasAllRequired() {
        return requiredBottles.stream()
                .allMatch(b -> b.getHolder() == this);
    }

    private void emptyHoldingBottles() {

        System.out.printf(
                "[DRINKING] Philosopher %s finished drinking and is emptying all bottles.%n",
                getName()
        );

        for (CMBottle holdingBottle : getHeldBottles()) {
            if (holdingBottle.getHolder() == this) {
                holdingBottle.empty();
            }
        }
    }

    @Override
    protected void idle() {

        System.out.printf(
                "[STATE] Philosopher %s entered IDLE state.%n",
                getName()
        );

        processRequests();

        Random randGen = new Random();
        long random = randGen.nextInt(0, connections.size());

        System.out.printf(
                "[STATE] Philosopher %s will remain idle for %d second(s).%n",
                getName(),
                random
        );

        try {
            sleep(1000 * random);
        } catch (InterruptedException e) {
        }
    }

    @Override
    protected void thirsty() {

        System.out.printf(
                "[STATE] Philosopher %s became THIRSTY.%n",
                getName()
        );

        requiredBottles.clear();

        generateRandomNumberOfBottles();

        System.out.printf(
                "[THIRSTY] Philosopher %s needs %d bottle(s).%n",
                getName(),
                numberOfRequiredBottles
        );

        List<SharedResource> bottles =
                new ArrayList<>(connections.values());

        Collections.shuffle(bottles);

        for (int i = 0; i < numberOfRequiredBottles; i++) {

            CMBottle bottle = (CMBottle) bottles.get(i);

            requiredBottles.add(bottle);

            System.out.printf(
                    "[THIRSTY] Philosopher %s selected bottle %d/%d.%n",
                    getName(),
                    i + 1,
                    numberOfRequiredBottles
            );

            if (bottle.getHolder() != this) {
                requestBottle(bottle);
            } else {
                System.out.printf(
                        "[THIRSTY] Philosopher %s already owns this bottle.%n",
                        getName()
                );
            }
        }

        waitForBottles();
    }

    @Override
    protected void drinking() {

        System.out.printf(
                "[STATE] Philosopher %s entered DRINKING state.%n",
                getName()
        );

        drink();

        System.out.printf(
                "[STATE] Philosopher %s finished drinking.%n",
                getName()
        );

        emptyHoldingBottles();
    }

    @Override
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
                synchronized (stateLock) {
                    if (hasAllRequired()) {
                        this.state = PhilosopherState.THIRSTY;
                        break;
                    }
                    drinking();
                    this.drinkingTime += System.nanoTime() - now;
                    this.state = PhilosopherState.IDLE;
                }
                break;
        }
    }

    @Override
    protected void drink() {
        if (hasAllRequired()) {
            try {
                sleep(1000);
                this.timesDrank++;
            } catch (InterruptedException e) {}

        } else {
            throw new IllegalStateException("Number of holding bottles is different from required number when drinking");
        }
    }
}