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

import com.google.api.services.calendar.model.Calendar;

import java.io.IOException;

import cz.cvut.fit.budikpet.googlecalendarsample.CalendarSampleActivity;

/**
 * Asynchronously insert a new calendar.
 *
 * @author Yaniv Inbar
 */
class AsyncInsertCalendar extends cz.cvut.fit.budikpet.googlecalendarsample.CalendarAsyncTask {

	private final Calendar entry;

	AsyncInsertCalendar(CalendarSampleActivity calendarSample, Calendar entry) {
		super(calendarSample);
		this.entry = entry;
	}

	@Override
	protected void doInBackground() throws IOException {
		Calendar calendar = client.calendars().insert(entry).setFields(cz.cvut.fit.budikpet.googlecalendarsample.CalendarInfo.FIELDS).execute();
		model.add(calendar);
	}
}
