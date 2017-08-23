package com.babcock;

import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;


public class SecurityServerTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    @Ignore
    public void oauth_with_password_grantType_works_asExpected() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();

        resourceDetails.setId("devOAuth");
        resourceDetails.setClientId("devApp");
        resourceDetails.setClientSecret("devAppSecret");
        resourceDetails.setUsername("admin");
        resourceDetails.setPassword("password");
        //resourceDetails.setAccessTokenUri("http://localhost:1111/auth/oauth/token");
        resourceDetails.setAccessTokenUri("http://10.52.180.31/auth/oauth/token");
        resourceDetails.setGrantType("password");

        DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext();
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails,context);

        int count = 2;

        for(int i = 0; i < count; i++) {
            String response = restTemplate.getForObject("http://localhost:2222/security-admin-service/securityadmin/permissions", String.class);
            //String response = restTemplate.getForObject("http://localhost:3333/message-service/message/hystrixTest", String.class);
            assertEquals("circuit working as expected", response);
        }
    }

    @Test
    @Ignore
    public void oauth_with_client_credentials_grantType_works_asExpected() {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails ();

        resourceDetails.setId("devOAuth");
        resourceDetails.setClientId("devApp");
        resourceDetails.setClientSecret("devAppSecret");

        resourceDetails.setAccessTokenUri("http://localhost:1113/auth/oauth/token");
        //resourceDetails.setAccessTokenUri("http://10.52.180.31/auth/oauth/token");
        resourceDetails.setGrantType("client_credentials");

        DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext();
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails,context);

        int count = 2;

        for(int i = 0; i < count; i++) {
            String response = restTemplate.getForObject("http://localhost:2222/security-admin-service/securityadmin/permissions", String.class);
            //String response = restTemplate.getForObject("http://localhost:2222/security-admin-service/securityadmin/permissions", String.class);
            //String response = restTemplate.getForObject("http://localhost:3333/message-service/message/hystrixTest", String.class);
            assertEquals("circuit working as expected", response);
        }
    }

    @Test
    @Ignore
    public void authorize_with_correctPassword_returns_200() {
        String url = getAuthorizeUrl("code","devClient","openid", "1234", "http://example.com/");
        HttpEntity<?> httpEntity = new HttpEntity<>(createHeaders("admin", "password"));

        ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, httpEntity, String.class);
        assertEquals(200, response.getStatusCode().value());

    }

    @Test
    @Ignore
    public void authorize_with_incorrectPassword_returns_401() {
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage("401 null");

        String url = getAuthorizeUrl("code","devClient","openid", "1234", "http://example.com/");

        HttpEntity<?> httpEntity = new HttpEntity<>(createHeaders("admin", "password1"));

        getRestTemplate().exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    private String getAuthorizeUrl(String responseType, String clientId, String scope, String state, String redirectUrl) {
        return "http://localhost:1111/auth/oauth/authorize" +
                "?response_type=" + responseType +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUrl +
                "&scope=" + scope +
                "&state=" + state;
    }

    private RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    private HttpHeaders createHeaders(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());

        String authHeader = "Basic " + new String(encodedAuth);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", authHeader);

        return httpHeaders;
    }
}