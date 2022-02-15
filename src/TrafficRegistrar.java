public interface TrafficRegistrar {
	void registerLeft(Vehicle v);
	void registerRight(Vehicle v);
	void deregisterLeft(Vehicle v);
	void deregisterRight(Vehicle v);
}
