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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Prepare query.	 
    Query query = new Query("Comment").addSort("toxicity", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    int maxComments = Integer.parseInt(request.getParameter("maxComments"));		
    
    // Get desired number of comments.		
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      if (comments.size() < maxComments) {
        String name = (String) entity.getProperty("name");
        String words = (String) entity.getProperty("words");
        long timestamp = (long) entity.getProperty("timestamp");
        long id = entity.getKey().getId();

        Comment comment = new Comment(name, words, timestamp, id);
        comments.add(comment);
      }
      else {
        break;
      }  				
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

}
