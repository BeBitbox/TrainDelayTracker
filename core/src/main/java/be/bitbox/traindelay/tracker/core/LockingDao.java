package be.bitbox.traindelay.tracker.core;

public interface LockingDao {
    boolean obtainedLock();
}
