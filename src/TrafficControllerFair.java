import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

public class TrafficControllerFair implements TrafficController {
    private final TrafficRegistrar registrar;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition bridgeEmptyCondition = lock.newCondition();
    private boolean bridgeEmpty = true;

    public TrafficControllerFair(TrafficRegistrar registrar) {
        this.registrar = registrar;
    }

    private void enter(Vehicle v, BiConsumer<TrafficRegistrar, Vehicle> registrarAction) {
        try {
            lock.lock();
            while (!bridgeEmpty) {
                bridgeEmptyCondition.await();
            }
            bridgeEmpty = false;
            registrarAction.accept(registrar, v);
        } catch (InterruptedException exc) {
            System.err.println("Thread was interrupted: " + exc);
            System.exit(1);
        } finally {
            lock.unlock();
        }
    }

    private void leave(Vehicle v, BiConsumer<TrafficRegistrar, Vehicle> registrarAction) {
        try {
            lock.lock();
            bridgeEmpty = true;
            registrarAction.accept(registrar, v);
            bridgeEmptyCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void enterLeft(Vehicle v) {
        enter(v, TrafficRegistrar::registerLeft);
    }

    @Override
    public void enterRight(Vehicle v) {
        enter(v, TrafficRegistrar::registerRight);
    }

    @Override
    public void leaveLeft(Vehicle v) {
        leave(v, TrafficRegistrar::deregisterLeft);
    }

    @Override
    public void leaveRight(Vehicle v) {
        leave(v, TrafficRegistrar::deregisterRight);
    }
}
