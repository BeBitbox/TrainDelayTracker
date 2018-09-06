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
package be.bitbox.traindelay.tracker.nmbs.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class Common {
    private Location locL[];

    private Vehicule prodL[];

    public Location[] getLocL() {
        return locL;
    }

    public Vehicule[] getProdL() {
        return prodL;
    }

    public void setProdL(Vehicule[] prodL) {
        this.prodL = prodL;
    }

    public void setLocL(Location[] locL) {
        this.locL = locL;
    }
}