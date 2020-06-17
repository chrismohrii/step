// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    Collection<String> allAttendees = new HashSet<>();
		allAttendees.addAll(request.getAttendees());
		allAttendees.addAll(request.getOptionalAttendees());
		MeetingRequest requestEveryone = new MeetingRequest(allAttendees, request.getDuration());
		Collection<TimeRange> queryAllPeople = queryGivenAttendees(events, requestEveryone);
		if (!queryAllPeople.isEmpty()) {
			System.out.println("returning with everyone" + queryAllPeople.toString());
			return queryAllPeople;
		}
		else {
      System.out.println("returning with madatory attendees" + queryGivenAttendees(events, request));
			return queryGivenAttendees(events, request);
		}
  }

	public Collection<TimeRange> queryGivenAttendees(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> busy = new ArrayList<>();
		
    // Determine times where meeting cannot be scheduled
    for (Event event : events) { 
      if (overlappingAttendees(event.getAttendees(), request.getAttendees())) {
        busy.add(event.getWhen());
      }
    }

    // Sort these times by start time
    Collections.sort(busy, TimeRange.ORDER_BY_START);

    // Simplify the times to avoid overlap
    ArrayList<TimeRange> mergedBusy = merge(busy); 

    Collection<TimeRange> free = new ArrayList<>();
    if (mergedBusy.size() > 0) { 
      // Add time at start of day if possible
      if (mergedBusy.get(0).start() - TimeRange.START_OF_DAY >= request.getDuration()){
        free.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, mergedBusy.get(0).start(), false));
      }
      // Fill in the time between unavailable times
      for (int i = 0; i < mergedBusy.size() - 1; i++) {
        if (mergedBusy.get(i+1).start() - mergedBusy.get(i).end() >= request.getDuration()) {
          free.add(TimeRange.fromStartEnd(mergedBusy.get(i).end(), mergedBusy.get(i+1).start(), false));
        }
      }
      // Add time at end of day if possible
      if (TimeRange.END_OF_DAY - mergedBusy.get(mergedBusy.size() - 1).end() >= request.getDuration()){
        free.add(TimeRange.fromStartEnd(mergedBusy.get(mergedBusy.size() - 1).end(), TimeRange.END_OF_DAY, true));
      }
    }
    else if (TimeRange.WHOLE_DAY.duration() >= request.getDuration() && request.getAttendees().size() > 0){
      // Add the whole day if possible
      free.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
    }
    return free;
  }

  /** Eliminates the overlapping times in a list of TimeRange objects*/
	private ArrayList<TimeRange> merge(ArrayList<TimeRange> list) {
    // invariant: merged contains non-overlapping TimeRange objects
    ArrayList<TimeRange> merged = new ArrayList<TimeRange>();
    for (int i = 0; i < list.size(); i++) {
      int lastMergedIndex = merged.size() - 1;
      // Empty or there is no overlap, so add in the time
      if (merged.size() == 0 || !merged.get(lastMergedIndex).overlaps(list.get(i))) {
        merged.add(list.get(i));
      }
      // There is uncontained overlap, so replace last element
      else if (!merged.get(lastMergedIndex).contains(list.get(i))) {
        int start = merged.get(lastMergedIndex).start();
        int end = list.get(i).end();
        merged.set(lastMergedIndex, TimeRange.fromStartEnd(start, end, false));
      }
    }
    return merged;
  }

  /** Determines whether or not there are overlapping attendees in an event and MeetingRequest */
  private boolean overlappingAttendees(Set<String> eventAttendees, Collection<String> requestAttendees) {
    for (String requestAttendee : requestAttendees) {
      if (eventAttendees.contains(requestAttendee)) {
        return true;
      }
    }
    return false;
  }
}
