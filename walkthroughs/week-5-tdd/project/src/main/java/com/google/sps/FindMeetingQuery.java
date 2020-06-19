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
    // Build a Collection of every possible attendee
    Collection<String> allAttendees = new HashSet<>();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());

    // Determine possible meeting times for every possible attendee 
    MeetingRequest requestEveryone = new MeetingRequest(allAttendees, request.getDuration());
    Collection<TimeRange> queryEveryone = queryGivenAttendees(events, requestEveryone);
		
    if (!queryEveryone.isEmpty()) {
      return queryEveryone;
    }
    else {
      return queryGivenAttendees(events, request);
    }
  }

  /** Returns the possible meeting times given required attendees */
  public Collection<TimeRange> queryGivenAttendees(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> busyTimes = new ArrayList<>();
		
    // Determine times where meeting cannot be scheduled
    for (Event event : events) { 
      if (overlappingAttendees(event.getAttendees(), request.getAttendees())) {
        busyTimes.add(event.getWhen());
      }
    }

    // Sort these times by start time
    Collections.sort(busyTimes, TimeRange.ORDER_BY_START);

    // Simplify the times to avoid overlap
    ArrayList<TimeRange> mergedBusyTimes = merge(busyTimes); 

    // Get the complement 
		boolean moreThanOneAttendee = request.getAttendees().size() > 0;
    Collection<TimeRange> freeTimes = complementOf(mergedBusyTimes, request.getDuration(), moreThanOneAttendee);
    
    return freeTimes;
  }

  /** Eliminates the overlapping times in a sorted-by-starttime list of TimeRange objects*/
  private ArrayList<TimeRange> merge(ArrayList<TimeRange> list) {
    // invariant: merged contains non-overlapping TimeRange objects
    ArrayList<TimeRange> merged = new ArrayList<TimeRange>();

    for (int i = 0; i < list.size(); i ++) {
      int lastMergedIndex = merged.size() - 1;
      // Empty or there is no overlap, so add in the TimeRange
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

  /** Gets the complementing times of an ArrayList of TimeRange objects */
  private Collection<TimeRange> complementOf(ArrayList<TimeRange> busyTimes, long duration, boolean moreThanOneAttendee) {
    Collection<TimeRange> freeTimes = new ArrayList<>();
    if (busyTimes.size() > 0) { 
      // Add time at start of day if possible
      if (busyTimes.get(0).start() - TimeRange.START_OF_DAY >= duration) {
        freeTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, busyTimes.get(0).start(), false));
      }
      // Fill in the time between unavailable times
      for (int i = 0; i < busyTimes.size() - 1; i ++) {
        if (busyTimes.get(i + 1).start() - busyTimes.get(i).end() >= duration) {
          freeTimes.add(TimeRange.fromStartEnd(busyTimes.get(i).end(), busyTimes.get(i + 1).start(), false));
        }
      }
      // Add time at end of day if possible
      if (TimeRange.END_OF_DAY - busyTimes.get(busyTimes.size() - 1).end() >= duration) {
        freeTimes.add(TimeRange.fromStartEnd(busyTimes.get(busyTimes.size() - 1).end(), TimeRange.END_OF_DAY, true));
      }
    }
    // Add the whole day if possible
    else if (TimeRange.WHOLE_DAY.duration() >= duration && moreThanOneAttendee) {
      freeTimes.add(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
    }
    return freeTimes;
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
