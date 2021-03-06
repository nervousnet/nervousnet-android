/**
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 */
/**
 *
 */
package ch.ethz.coss.nervousnet.hub.ui.fragments;

import android.support.v4.app.Fragment;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.lib.InfoReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

public abstract class BaseFragment extends Fragment {

    private static final String LOG_TAG = BaseFragment.class.getSimpleName();

    public int type = 0;

    Switch sensorSwitch;
    TextView sensorStatusTV;

    RadioGroup radioGroup;
    byte lastCollectionRate;


    public BaseFragment() {

    }

    public BaseFragment(int type) {
        // TODO Auto-generated constructor stub

        this.type = type;
    }

    public abstract void updateReadings(SensorReading reading);

    public abstract void handleError(InfoReading reading);

    @Override
    public void onResume() {
        NNLog.d(LOG_TAG, "onResume of BaseFragment");
        super.onResume();
    }

    @Override
    public void onPause() {
        NNLog.d(LOG_TAG, "onPause of BaseFragment");
        super.onPause();
    }

}
