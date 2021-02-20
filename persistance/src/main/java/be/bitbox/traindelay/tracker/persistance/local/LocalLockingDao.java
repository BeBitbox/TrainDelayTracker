package be.bitbox.traindelay.tracker.persistance.local;

import be.bitbox.traindelay.tracker.core.LockingDao;

public class LocalLockingDao implements LockingDao {

    @Override
    public boolean obtainedLock() {
        return true;
    }
}
