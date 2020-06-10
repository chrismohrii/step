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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/vote-data")
public class VoteDataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Prepare query.	 
    Query query = new Query("Vote");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Prepare data
    Map<String, Integer> pageVotes = new HashMap<>(); 
    for (Entity entity : results.asIterable()) {
      String page = (String) entity.getProperty("page");
      Integer count = (int) (long) entity.getProperty("count");
      pageVotes.put(page, count);
    }

    // Return the data
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(pageVotes);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get input from the URL
    String page = request.getParameter("userVote");
 
    // Prepare query.	 
    Query query = new Query("Vote");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Check if the page is in the query
    Entity foundEntity = null;
    for (Entity entity : results.asIterable()) {
      String entityPage = (String) entity.getProperty("page");
      if (page.equals(entityPage)) {
        foundEntity = entity;
      } 
    }

    if (foundEntity != null) {
      // If yes, increment count by 1
      long currentVotes = (Long) foundEntity.getProperty("count");
      foundEntity.setProperty("count", currentVotes + 1);
      datastore.put(foundEntity);
    }
    else {
      // If not, create new entity			
      Entity voteEntity = new Entity("Vote");
      voteEntity.setProperty("page", page);
      voteEntity.setProperty("count", 1);
      datastore.put(voteEntity);
    }
  }
}
