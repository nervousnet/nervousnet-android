/*******************************************************************************
 *
 *  *     Nervousnet - a distributed middleware software for social sensing.
 *  *      It is responsible for collecting and managing data in a fully de-centralised fashion
 *  *
 *  *     Copyright (C) 2016 ETH Zürich, COSS
 *  *
 *  *     This file is part of Nervousnet Framework
 *  *
 *  *     Nervousnet is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     Nervousnet is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU General Public License
 *  *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *******************************************************************************/

package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;


public class LocationSensor extends BaseSensor {

    private static final String LOG_TAG = BaseSensor.class.getSimpleName();

    LocationManager locationManager;
    Context mContext;

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //TODO push() reading
                location.getLongitude();
                location.getLatitude();

                SensorReading reading = new SensorReading(sensorID, sensorName, paramNames);
                ArrayList values = new ArrayList();
                values.add(location.getLatitude());
                values.add(location.getLongitude());
                reading.setValues(values);
                Log.d("LocationSensor", "pushing values (lat: " + location.getLatitude() + ", long: " + location.getLongitude());
                push(reading);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

    public LocationSensor(Context context, BasicSensorConfiguration conf) {
        super(context, conf);
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mContext = context;
    }

    @Override
    protected boolean startListener() {
        // check if permissions and network/gps available

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //TODO: showLocationAlert();
            Toast.makeText(mContext, "Please check your Network Connectivity.", Toast.LENGTH_LONG).show();
        } else {
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        return false;
    }

    @Override
    protected boolean stopListener() {
        locationManager.removeUpdates(locationListener);
        return false;
    }
}
