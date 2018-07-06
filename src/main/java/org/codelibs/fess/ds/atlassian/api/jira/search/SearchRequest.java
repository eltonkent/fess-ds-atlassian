/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.ds.atlassian.api.jira.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.GenericData;

import org.codelibs.fess.ds.atlassian.api.jira.JiraClient;
import org.codelibs.fess.ds.atlassian.api.jira.JiraRequest;

public class SearchRequest extends JiraRequest {

    private String jql;
    private Integer startAt, maxResults;
    private Boolean validateQuery;
    private String[] fields, expand;

    public SearchRequest(JiraClient jiraClient) {
        super(jiraClient);
    }

    @Override
    public SearchResponse execute() {
        try {
            final HttpContent content = new JsonHttpContent(new JacksonFactory(),
                    buildData(jql, startAt, maxResults, validateQuery, fields, expand));
            final HttpRequest request = jiraClient.request().buildPostRequest(buildUrl(jiraClient.jiraHome()), content);
            final HttpResponse response = request.execute();
            final Scanner s = new Scanner(response.getContent()).useDelimiter("\\A");
            final String result = s.hasNext() ? s.next() : "";
            final ObjectMapper mapper = new ObjectMapper();
            final Map<String, Object> map = mapper.readValue(result, new TypeReference<Map<String, Object>>() {
            });
            @SuppressWarnings("unchecked")
            final List<Map<String, Object>> issues = (List<Map<String, Object>>) map.get("issues");
            return new SearchResponse(issues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SearchRequest jql(String jql) {
        this.jql = jql;
        return this;
    }

    public SearchRequest startAt(int startAt) {
        this.startAt = startAt;
        return this;
    }

    public SearchRequest maxResults(int maxResults) {
        this.maxResults = maxResults;
        return this;
    }

    public SearchRequest validateQuery(boolean validateQuery) {
        this.validateQuery = validateQuery;
        return this;
    }

    public SearchRequest fields(String... fields) {
        this.fields = fields;
        return this;
    }

    public SearchRequest expand(String... expand) {
        this.expand = expand;
        return this;
    }

    protected GenericUrl buildUrl(final String jiraHome) {
        return new GenericUrl(jiraHome + "/rest/api/latest/search");
    }

    protected GenericData buildData(final String jql, final Integer startAt, final Integer maxResults,
            final Boolean validateQuery, final String[] fields, final String[] expand) {
        GenericData data = new GenericData();
        if (jql != null) {
            data.put("jql", jql);
        }
        if (startAt != null) {
            data.put("startAt", startAt);
        }
        if (maxResults != null) {
            data.put("maxResults", maxResults);
        }
        if (validateQuery != null) {
            data.put("validateQuery", validateQuery);
        }
        if (fields != null) {
            data.put("fields", fields);
        }
        if (expand != null) {
            data.put("expand", String.join(",", expand));
        }
        return data;
    }

}