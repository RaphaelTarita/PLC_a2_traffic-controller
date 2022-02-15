import java.util.function.BiConsumer;

public class TrafficControllerSimple implements TrafficController {
    private final TrafficRegistrar registrar;
    private boolean bridgeEmpty = true;

    public TrafficControllerSimple(TrafficRegistrar registrar) {
        this.registrar = registrar;
    }

    private synchronized void enter(Vehicle v, BiConsumer<TrafficRegistrar, Vehicle> registrarAction) {
        try {
            while (!bridgeEmpty) {
                wait();
            }
            bridgeEmpty = false;
            registrarAction.accept(registrar, v);
        } catch (InterruptedException exc) {
            System.err.println("Thread was interrupted: " + exc);
            System.exit(1);
        }
    }

    private synchronized void leave(Vehicle v, BiConsumer<TrafficRegistrar, Vehicle> registrarAction) {
        bridgeEmpty = true;
        registrarAction.accept(registrar, v);
        notifyAll();
    }

    @Override
    public synchronized void enterLeft(Vehicle v) {
        enter(v, TrafficRegistrar::registerLeft);
    }

    @Override
    public void enterRight(Vehicle v) {
        enter(v, TrafficRegistrar::registerRight);
    }

    @Override
    public synchronized void leaveLeft(Vehicle v) {
        leave(v, TrafficRegistrar::deregisterLeft);
    }

    @Override
    public synchronized void leaveRight(Vehicle v) {
        leave(v, TrafficRegistrar::deregisterRight);
    }
}
