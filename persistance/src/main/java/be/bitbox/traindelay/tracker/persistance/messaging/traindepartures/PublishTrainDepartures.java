/*
 * Copyright 2019 Bitbox : TrainDelayTracker
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
package be.bitbox.traindelay.tracker.persistance.messaging.traindepartures;

import be.bitbox.traindelay.tracker.core.traindeparture.JsonTrainDeparture;
import be.bitbox.traindelay.tracker.core.traindeparture.TrainDepartureEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class PublishTrainDepartures {
    private final RecentTrainDepartures recentTrainDepartures;

    @Autowired
    PublishTrainDepartures(EventBus eventBus,
                           RecentTrainDepartures recentTrainDepartures) {
        this.recentTrainDepartures = recentTrainDepartures;
        eventBus.register(this);
    }

    @Subscribe
    void subscribeDepartureEvent(TrainDepartureEvent trainDepartureEvent) {
        var jsonTrainDeparture = new JsonTrainDeparture(trainDepartureEvent);

        recentTrainDepartures.addTrainDeparture(jsonTrainDeparture);
    }
}