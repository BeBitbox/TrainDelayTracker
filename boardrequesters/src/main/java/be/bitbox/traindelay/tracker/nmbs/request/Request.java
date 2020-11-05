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
package be.bitbox.traindelay.tracker.nmbs.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Request {
    private static final String LANGUAGE = "nld";
    private static final String VERSION = "1.21";
    private static final Boolean FORMATTED = false;

    private final String stationName;

    private final String stationId;

    private final LocalDate date;

    private final Authentication authentication = new Authentication();

    private final Client client = new Client();

    private final ServiceRequest[] svcReqL = {new ServiceRequest()};

    Request(String stationName, String stationId, LocalDate localDate) {
        this.stationName = stationName;
        if (stationId.startsWith("BE.NMBS.00")) {
            this.stationId = stationId.substring(10);
        } else {
            this.stationId = stationId;
        }
        this.date = localDate;
    }

    public ServiceRequest[] getSvcReqL() {
        return svcReqL;
    }

    public Authentication getAuth() {
        return authentication;
    }

    public Client getClient() {
        return client;
    }

    public String getLang() {
        return LANGUAGE;
    }

    public String getVer() {
        return VERSION;
    }

    public Boolean getFormatted() {
        return FORMATTED;
    }

    private static class Authentication {
        private static final String ID = "sncb-mobi";
        private static final String TYPE = "AID";

        public String getAid() {
            return ID;
        }

        public String getType() {
            return TYPE;
        }
    }

    private static class Client {
        private static final String ID = "SNCB";
        private static final String NAME = "NMBS";
        private static final String OS = "Android 5.0.2";
        private static final String TYPE = "AND";
        private static final String UA = "Android_5.0.2";
        private static final Integer VERSION = 302132;

        public String getId() {
            return ID;
        }

        public String getName() {
            return NAME;
        }

        public String getOs() {
            return OS;
        }

        public String getType() {
            return TYPE;
        }

        public String getUa() {
            return UA;
        }

        public Integer getV() {
            return VERSION;
        }
    }

    private class RequestData {
        private final JnyFilter[] JNY_FILTER = {new JnyFilter()};
        private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

        private final StbLocation stbLocation = new StbLocation();

        public String getDate() {
            return date.format(DATE_FORMAT);
        }

        public JnyFilter[] getJnyFltrL() {
            return JNY_FILTER;
        }

        public StbLocation getStbLoc() {
            return stbLocation;
        }

        public Integer getMaxJny() {
            return 50;
        }
    }

    private static class JnyFilter {
        private static final String MODE = "BIT";
        private static final String TYPE = "PROD";
        private static final String VALUE = "11101111000111";

        public String getMode() {
            return MODE;
        }

        public String getType() {
            return TYPE;
        }

        public String getValue() {
            return VALUE;
        }
    }

    private class ServiceRequest {
        private static final String METHODE = "StationBoard";

        private final RequestData req = new RequestData();

        public String getMeth() {
            return METHODE;
        }

        public RequestData getReq() {
            return req;
        }
    }

    private class StbLocation {
        private static final String TYPE = "S";

        public String getType() {
            return TYPE;
        }

        public String getLid() {
            return "A=1@O=" + stationName + "@U=80@L=00" + stationId + "@B=1@p=1429490515@";
        }
    }
}