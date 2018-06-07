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
package be.bitbox.traindelay.tracker.nmbs.gtfs;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.transit.realtime.GtfsRealtime;

public class GtfsSomething {

    public void gtfs(byte[] file) throws InvalidProtocolBufferException {
        GtfsRealtime.FeedMessage feedMessage = GtfsRealtime.FeedMessage.parseFrom(file);
        int counterTransit = 0;
        int counterIncoming = 0;
        int counterStopped = 0;
        for (GtfsRealtime.FeedEntity entity : feedMessage.getEntityList()) {
            switch (entity.getVehicle().getCurrentStatus()) {
                case IN_TRANSIT_TO:
                    counterTransit++;
                    break;
                case INCOMING_AT:
                    counterIncoming++;
                    break;
                case STOPPED_AT:
                    counterStopped++;
                    break;
            }

            if(entity.hasAlert()) {
                System.out.println("alert");
            }
            if (entity.hasTripUpdate()) {
                if(entity.getTripUpdate().hasVehicle()){
                    System.out.println("piep");
                }
                for (GtfsRealtime.TripUpdate.StopTimeUpdate stopTimeUpdate : entity.getTripUpdate().getStopTimeUpdateList()) {
                    if(stopTimeUpdate.hasStopSequence()) {
                        System.out.println(stopTimeUpdate.getStopSequence());
                    }

                }
            } else {
                System.out.println("OWW no");
            }
            if (entity.getIsDeleted()) {
                System.out.println("headshot");
            }


        }
        System.out.println(counterTransit + "  " + counterIncoming + "   " + counterStopped);
    }
}