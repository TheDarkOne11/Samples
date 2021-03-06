/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package cz.cvut.fit.budikpet.googlecalendarsample;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.fit.budikpet.googlecalendarsample.asyncOperations.AsyncBatchInsertCalendars;
import cz.cvut.fit.budikpet.googlecalendarsample.asyncOperations.AsyncDeleteCalendar;
import cz.cvut.fit.budikpet.googlecalendarsample.asyncOperations.AsyncInsertCalendar;
import cz.cvut.fit.budikpet.googlecalendarsample.asyncOperations.AsyncLoadCalendars;
import cz.cvut.fit.budikpet.googlecalendarsample.asyncOperations.AsyncUpdateCalendar;

/**
 * Main activity.
 * Sample activity for Google Calendar API v3. It demonstrates how to use authorization to list
 * calendars, add a new calendar, and edit or delete an existing calendar with the user's
 * permission.
 *
 */
public final class CalendarSampleActivity extends Activity {

	/**
	 * Logging level for HTTP requests/responses.
	 *
	 * <p>
	 * To turn on, set to {@link Level#CONFIG} or {@link Level#ALL} and run this from command line:
	 * </p>
	 *
	 * <pre>
	 * adb shell setprop log.tag.HttpTransport DEBUG
	 * </pre>
	 */
	private static final Level LOGGING_LEVEL = Level.OFF;

	private static final String PREF_ACCOUNT_NAME = "accountName";

	public static final String TAG = "CalendarSampleActivity";

	private static final int CONTEXT_EDIT = 0;

	private static final int CONTEXT_DELETE = 1;

	private static final int CONTEXT_BATCH_ADD = 2;

	public static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

	public static final int REQUEST_AUTHORIZATION = 1;

	public static final int REQUEST_ACCOUNT_PICKER = 2;

	private final static int ADD_OR_EDIT_CALENDAR_REQUEST = 3;

	public final HttpTransport transport = AndroidHttp.newCompatibleTransport();

	public final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

	public GoogleAccountCredential credential;

	public CalendarModel model = new CalendarModel();

	public ArrayAdapter<CalendarInfo> adapter;

	public com.google.api.services.calendar.Calendar client;
	public int numAsyncTasks;

	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable logging
		Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);

		// view and menu
		setContentView(R.layout.calendarlist);
		listView = findViewById(R.id.list);
		registerForContextMenu(listView);

		// Google Accounts
		credential =
				GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

		// Calendar client
		client = new com.google.api.services.calendar.Calendar.Builder(
				transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
				.build();

		checkPermissions();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (checkGooglePlayServicesAvailable()) {
			haveGooglePlayServices();
		}
	}

	private void checkPermissions() {
		ArrayList<String> list = new ArrayList<>();
		list.add(Manifest.permission.INTERNET);
		list.add(Manifest.permission.GET_ACCOUNTS);
		list.add(Manifest.permission.READ_CALENDAR);
		list.add(Manifest.permission.WRITE_CALENDAR);
		list.add(Manifest.permission.READ_CONTACTS);
		list.add(Manifest.permission.WRITE_CONTACTS);

		for(String perm: list) {
			if (ContextCompat.checkSelfPermission(this, perm)
					!= PackageManager.PERMISSION_GRANTED) {
				Log.i("PERM_TEST", perm + " is not granted.");
				askForPermission(perm);
			} else {
				Log.i("PERM_TEST", perm + " was granted.");
			}
		}
	}

	private void askForPermission(String permission) {
		int REQUEST_CODE_ASK_PERMISSIONS = 123;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermissions(new String[] {permission},
					REQUEST_CODE_ASK_PERMISSIONS);
		}
	}

	/**
	 * Dialog window shown when Google Play services are unavailable.
	 * @param connectionStatusCode
	 */
	public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(
						CalendarSampleActivity.this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});
	}

	public void refreshView() {
		adapter = new ArrayAdapter<CalendarInfo>(
				this, android.R.layout.simple_list_item_1, model.toSortedArray()) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// by default it uses toString; override to use summary instead
				TextView view = (TextView) super.getView(position, convertView, parent);
				CalendarInfo calendarInfo = getItem(position);
				view.setText(calendarInfo.summary);
				return view;
			}
		};
		listView.setAdapter(adapter);
	}

	/**
	 * Check for results of all activities that can be executed from this activity.
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQUEST_GOOGLE_PLAY_SERVICES:
				if (resultCode == Activity.RESULT_OK) {
					haveGooglePlayServices();
				} else {
					checkGooglePlayServicesAvailable();
				}
				break;
			case REQUEST_AUTHORIZATION:
				if (resultCode == Activity.RESULT_OK) {
					AsyncLoadCalendars.run(this);
				} else {
					chooseAccount();
				}
				break;
			case REQUEST_ACCOUNT_PICKER:
				if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
					String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
					if (accountName != null) {
						credential.setSelectedAccountName(accountName);
						SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(PREF_ACCOUNT_NAME, accountName);
						editor.commit();
						AsyncLoadCalendars.run(this);
					}
				}
				break;
			case ADD_OR_EDIT_CALENDAR_REQUEST:
				if (resultCode == Activity.RESULT_OK) {
					Calendar calendar = new Calendar();
					calendar.setSummary(data.getStringExtra("summary"));
					String id = data.getStringExtra("id");
					if (id == null) {
						new AsyncInsertCalendar(this, calendar).execute();
					} else {
						calendar.setId(id);
						new AsyncUpdateCalendar(this, id, calendar).execute();
					}
				}
				break;
		}
	}

	// MARK: NavBar methods.

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_refresh:
				AsyncLoadCalendars.run(this);
				break;
			case R.id.menu_accounts:
				chooseAccount();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, CONTEXT_EDIT, 0, R.string.edit);
		menu.add(0, CONTEXT_DELETE, 0, R.string.delete);
		menu.add(0, CONTEXT_BATCH_ADD, 0, R.string.batchadd);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int calendarIndex = (int) info.id;
		if (calendarIndex < adapter.getCount()) {
			final CalendarInfo calendarInfo = adapter.getItem(calendarIndex);
			switch (item.getItemId()) {
				case CONTEXT_EDIT:
					startAddOrEditCalendarActivity(calendarInfo);
					return true;
				case CONTEXT_DELETE:
					new AlertDialog.Builder(this).setTitle(R.string.delete_title)
							.setMessage(calendarInfo.summary)
							.setCancelable(false)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {
									new AsyncDeleteCalendar(CalendarSampleActivity.this, calendarInfo).execute();
								}
							})
							.setNegativeButton(R.string.no, null)
							.create()
							.show();
					return true;
				case CONTEXT_BATCH_ADD:
					List<Calendar> calendars = new ArrayList<Calendar>();
					for (int i = 0; i < 3; i++) {
						Calendar cal = new Calendar();
						cal.setSummary(calendarInfo.summary + " [" + (i + 1) + "]");
						calendars.add(cal);
					}
					new AsyncBatchInsertCalendars(this, calendars).execute();
					return true;
			}
		}
		return super.onContextItemSelected(item);
	}

	public void onAddClick(View view) {
		startAddOrEditCalendarActivity(null);
	}

	/**
	 * Check that Google Play services APK is installed and up to date.
	 */
	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
		if (GoogleApiAvailability.getInstance().isUserResolvableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;
	}

	/**
	 * Checks if the user has already signed in to a Google Play account.
	 */
	private void haveGooglePlayServices() {
		// check if there is already an account selected
		if (credential.getSelectedAccountName() == null) {
			// ask user to choose account
			chooseAccount();
		} else {
			// load calendars
			AsyncLoadCalendars.run(this);
		}
	}

	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	private void startAddOrEditCalendarActivity(CalendarInfo calendarInfo) {
		Intent intent = new Intent(this, AddOrEditCalendarActivity.class);
		if (calendarInfo != null) {
			intent.putExtra("id", calendarInfo.id);
			intent.putExtra("summary", calendarInfo.summary);
		}
		startActivityForResult(intent, ADD_OR_EDIT_CALENDAR_REQUEST);
	}
}
