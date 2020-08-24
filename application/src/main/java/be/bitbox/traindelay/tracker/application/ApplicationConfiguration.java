/*
 * Copyright 2018 Bitbox : TrainDelayTracker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.bitbox.traindelay.tracker.application;

import be.bitbox.traindelay.tracker.application.local.LocalAmazonSQS;
import be.bitbox.traindelay.tracker.application.local.LocalDynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.IDynamoDBMapper;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(basePackages = {"be.bitbox.traindelay.tracker"})
public class ApplicationConfiguration {

    @Bean
    public EventBus getEventBus() {
        return new EventBus();
    }

    @Bean
    @Profile("!local")
    public IDynamoDBMapper getDynamoDBMapper() {
        var amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion("eu-west-3")
                .build();
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Bean
    @Profile("local")
    public IDynamoDBMapper getLocalDynamoDBMapper() {
        return new LocalDynamoDBMapper();
    }

    @Bean
    @Profile("!local")
    public AmazonSQS getAmazonSQS() {
        return AmazonSQSClientBuilder
                .standard()
                .withRegion("eu-west-3")
                .build();
    }

    @Bean
    @Profile("local")
    public AmazonSQS getLocalAmazonSQS() {
        return new LocalAmazonSQS();
    }
}