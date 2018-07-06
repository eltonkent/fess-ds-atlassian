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
package org.codelibs.fess.ds.atlassian.api.jira;

import com.google.api.client.http.HttpRequestFactory;

import org.codelibs.fess.ds.atlassian.api.jira.issue.GetIssueRequest;
import org.codelibs.fess.ds.atlassian.api.jira.project.GetProjectRequest;
import org.codelibs.fess.ds.atlassian.api.jira.project.GetProjectsRequest;
import org.codelibs.fess.ds.atlassian.api.jira.search.SearchRequest;

public class JiraClient {
    protected final String jiraHome;

    protected final HttpRequestFactory httpRequestFactory;

    protected JiraClient(final String jiraHome, final HttpRequestFactory httpRequestFactory) {
        this.jiraHome = jiraHome;
        this.httpRequestFactory = httpRequestFactory;
    }

    public static JiraClientBuilder builder() {
        return new JiraClientBuilder();
    }

    public String jiraHome() {
        return jiraHome;
    }

    public HttpRequestFactory request() {
        return httpRequestFactory;
    }

    public GetProjectsRequest getProjects() {
        return new GetProjectsRequest(this);
    }

    public GetProjectRequest getProject(String projectIdOrKey) {
        return new GetProjectRequest(this, projectIdOrKey);
    }

    public SearchRequest search() {
        return new SearchRequest(this);
    }

    public GetIssueRequest getIssue(String issueIdOrKey) {
        return new GetIssueRequest(this, issueIdOrKey);
    }
}