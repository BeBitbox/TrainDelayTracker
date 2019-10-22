package be.bitbox.traindelay.tracker.persistance.db.locking;

import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.UUID;

import be.bitbox.traindelay.tracker.core.LockingDao;

@Component
public class DynamoLockingDao implements LockingDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoLockingDao.class);
    private static final String LOCK_KEY = "BoardRetrievalLock";

    private final IDynamoDBMapper dynamoDBMapper;
    private final String hostname;

    @Autowired
    public DynamoLockingDao(IDynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.hostname = getHostname();
    }

    private String getHostname() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "Server " + UUID.randomUUID().toString();
        }
        return hostname;
    }

    @Override
    public boolean obtainedLock() {
        try {
            return obtainedLockUnsafe();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving lock", ex);
            return false;
        }
    }

    private boolean obtainedLockUnsafe() {
        boolean obtainedLock = false;
        DynamoLocking lock = dynamoDBMapper.load(DynamoLocking.class, LOCK_KEY);
        if (lock == null) {
            lock = new DynamoLocking();
            lock.setLockId(LOCK_KEY);
            lock.setHostname(hostname);
            lock.setExpires(LocalDateTime.now().plusMinutes(5));
            dynamoDBMapper.save(lock);
            obtainedLock = true;
        } else {
            if (LocalDateTime.now().isAfter(lock.getExpires())) {
                lock.setHostname(hostname);
                lock.setExpires(LocalDateTime.now().plusMinutes(5));
                dynamoDBMapper.save(lock);
                obtainedLock = true;
            } else if (hostname.equals(lock.getHostname())) {
                lock.setExpires(LocalDateTime.now().plusMinutes(5));
                dynamoDBMapper.save(lock);
                obtainedLock = true;
            }
        }
        return obtainedLock;
    }
}
