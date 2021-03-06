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
 *  * 	Contributors:
 *  * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.sample;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import ch.ethz.coss.nervousnet.lib.Utils;

/**
 * StartUpActivity is the main activity within the Nervousnet app. This activity
 * starts when the app is launched. If the TERMS_ENABLED flag is true it shows
 * the "Terms Of Use" Dialog and if this flag is not enabled it launches the
 * MainActivity
 */
public class StartUpActivity extends Activity {

	public static boolean TERMS_ENABLED = true;

	public StartUpActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (TERMS_ENABLED)
			new TermsOfUse(StartUpActivity.this).showTerms();
		else
			skipTermsScreen();

	}

	public void skipTermsScreen() {
		
		if(SampleUtils.checkForNervousnetHubApp("ch.ethz.coss.nervousnet.hub", StartUpActivity.this)){

			Intent intent = new Intent(StartUpActivity.this, SampleAppActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

			startActivity(intent);
			finish();
		} else {
			
			Utils.displayAlert(StartUpActivity.this, "Alert",
					"Nervousnet HUB application is required to be installed. If not installed please download it from the App Store. If already installed, please turn on the Data Collection option inside the Nervousnet HUB application.",
					"Download Now", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
									"https://play.google.com/apps/testing/ch.ethz.coss.nervousnet.hub")));
						}
					}, "Exit", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							System.exit(0);
						}
					});
		}

	}


}