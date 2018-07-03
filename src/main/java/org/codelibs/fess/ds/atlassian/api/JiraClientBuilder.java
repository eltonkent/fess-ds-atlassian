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
package org.codelibs.fess.ds.atlassian.api;

import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class JiraClientBuilder {
    private String jiraHome;
    private OAuthGetAccessToken oAuthGetAccessToken;
    private BasicAuthentication basicAuthentication;

    JiraClientBuilder() {
    }

    public JiraClientBuilder oAuthToken(final String jiraHome, final OAuthTokenSupplier supplier) {
        this.jiraHome = jiraHome;
        oAuthGetAccessToken = new OAuthGetAccessToken(jiraHome);
        supplier.apply(oAuthGetAccessToken);
        return this;
    }

    public JiraClientBuilder basicAuth(final String jiraHome, final String userName, final String password) {
        this.jiraHome = jiraHome;
        basicAuthentication = new BasicAuthentication(userName, password);
        return this;
    }

    public JiraClient build() {
        final HttpRequestFactory httpRequestFactory;
        if (basicAuthentication != null) {
            httpRequestFactory = new NetHttpTransport().createRequestFactory(basicAuthentication);
        } else if (oAuthGetAccessToken != null) {
            httpRequestFactory = new NetHttpTransport().createRequestFactory(oAuthGetAccessToken.createParameters());
        } else {
            httpRequestFactory = new NetHttpTransport().createRequestFactory();
        }
        return new JiraClient(jiraHome, httpRequestFactory);
    }

    public interface OAuthTokenSupplier {
        void apply(OAuthGetAccessToken accessToken);
    }

    public static OAuthRsaSigner getOAuthRsaSigner(String privateKey) {
        try {
            OAuthRsaSigner oAuthRsaSigner = new OAuthRsaSigner();
            oAuthRsaSigner.privateKey = getPrivateKey(privateKey);
            return oAuthRsaSigner;
        } catch (NoSuchAlgorithmException|InvalidKeySpecException e) {
            throw new RuntimeException("Failed to get OAuth rsa signer.", e);
        }
    }

    private static PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }
}
