/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.citrus.analytics;

import android.app.Application;

import com.citrus.mobile.Config;
import com.citrus.sdk.Constants;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.orhanobut.logger.LogLevel;

import java.util.HashMap;

/**
 * Created by MANGESH KADAM on 4/21/2015.
 */
public class CitrusLibraryApp extends Application {
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    private final String LOG_TAG = "CITRUSLIBRARY";

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public CitrusLibraryApp() {
        super();
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            //analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(Config.getAnalyticsID()):null;

           // t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (Constants.ENABLE_LOGS) {
            com.orhanobut.logger.Logger.init(LOG_TAG)
                    .setMethodCount(2)
                    .hideThreadInfo()
                    .setLogLevel(LogLevel.FULL);
        } else {
            com.orhanobut.logger.Logger.init(LOG_TAG)
                    .setMethodCount(2)
                    .hideThreadInfo()
                    .setLogLevel(LogLevel.NONE);
        }
    }


}
