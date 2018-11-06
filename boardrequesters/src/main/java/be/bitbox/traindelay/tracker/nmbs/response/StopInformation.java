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
public class StopInformation {

    private String dTimeS;

    private String dTimeR;

    private String dPlatfR;

    private String dPlatfS;

    private int dProdX;

    private boolean dCncl;

    public String getdTimeS() {
        return dTimeS;
    }

    public void setdTimeS(String dTimeS) {
        this.dTimeS = dTimeS;
    }

    public String getdTimeR() {
        return dTimeR;
    }

    public boolean isdCncl() {
        return dCncl;
    }

    public void setdCncl(boolean dCncl) {
        this.dCncl = dCncl;
    }

    public void setdTimeR(String dTimeR) {
        this.dTimeR = dTimeR;
    }

    public String getdPlatfR() {
        return dPlatfR;
    }

    public void setdPlatfR(String dPlatfR) {
        this.dPlatfR = dPlatfR;
    }

    public String getdPlatfS() {
        return dPlatfS;
    }

    public void setdPlatfS(String dPlatfS) {
        this.dPlatfS = dPlatfS;
    }

    public int getdProdX() {
        return dProdX;
    }

    public void setdProdX(int dProdX) {
        this.dProdX = dProdX;
    }
}