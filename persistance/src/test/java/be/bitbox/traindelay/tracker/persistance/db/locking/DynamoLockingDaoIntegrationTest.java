package be.bitbox.traindelay.tracker.persistance.db.locking;

import be.bitbox.traindelay.tracker.core.LockingDao;
import be.bitbox.traindelay.tracker.persistance.db.AWSTestClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DynamoLockingDaoIntegrationTest {

    @Test
    @Ignore("Going to a real database")
    public void obtainedLock() {
        AmazonDynamoDB client = AWSTestClient.create();
        LockingDao lockingDao = new DynamoLockingDao(client);
        
        assertThat(lockingDao.obtainedLock()).isTrue();
    }
}