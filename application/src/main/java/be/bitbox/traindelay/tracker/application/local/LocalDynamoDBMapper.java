package be.bitbox.traindelay.tracker.application.local;

import com.amazonaws.services.dynamodbv2.datamodeling.AbstractDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

public class LocalDynamoDBMapper extends AbstractDynamoDBMapper {

    @Override
    public <T> PaginatedQueryList<T> query(Class<T> clazz, DynamoDBQueryExpression<T> queryExpression) {
        return null;
    }

    @Override
    public <T> T load(T keyObject) {
        return null;
    }

    @Override
    public <T> T load(Class<T> clazz, Object hashKey) {
        return null;
    }

    @Override
    public <T> T load(Class<T> clazz, Object hashKey, Object rangeKey) {
        return null;
    }

    @Override
    public <T> void save(T object) {
    }
}
