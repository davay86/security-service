package com.babcock.integration;

import com.babcock.integration.application.TestApplication;
import com.babcock.integration.asserter.WaitForHelper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= TestApplication.class)
@TestPropertySource("classpath:application.properties")
public class SecurityApiIT {

    private static Logger logger = LoggerFactory.getLogger(SecurityApiIT.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private WaitForHelper waitForHelper;

    @Value("${test.service.url}")
    String testServiceUrl;

    @Value("${security.token.url}")
    String securityTokenUrl;

    @Value("${security.authorize.url}")
    String securityAuthorizeUrl;

    @Before
    public void before() throws InterruptedException {
        waitForHelper.waitForServices();
    }

    @Test
    public void oauth_with_password_grantType_works_asExpected() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();

        resourceDetails.setId("devOAuth");
        resourceDetails.setClientId("devApp");
        resourceDetails.setClientSecret("devAppSecret");

        resourceDetails.setUsername("admin");
        resourceDetails.setPassword("password");

        resourceDetails.setAccessTokenUri(securityTokenUrl);
        resourceDetails.setGrantType("password");

        DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext();
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails,context);

        int count = 2;

        for(int i = 0; i < count; i++) {
            String response = restTemplate.getForObject(testServiceUrl+"/message/greeting", String.class);
            assertEquals("Hello World!!!", response);
        }
    }

    @Test
    public void oauth_with_client_credentials_grantType_works_asExpected() {
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails ();

        resourceDetails.setId("devOAuth");
        resourceDetails.setClientId("devApp");
        resourceDetails.setClientSecret("devAppSecret");

        resourceDetails.setAccessTokenUri(securityTokenUrl);
        resourceDetails.setGrantType("client_credentials");

        DefaultOAuth2ClientContext context = new DefaultOAuth2ClientContext();
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails,context);

        int count = 2;

        for(int i = 0; i < count; i++) {
            String response = restTemplate.getForObject(testServiceUrl+"/message/greeting", String.class);
            assertEquals("Hello World!!!", response);
        }
    }

    @Test
    @Ignore
    public void authorize_with_correctPassword_returns_asExpected() {
        String url = getAuthorizeUrl("code","devClient","openid", "1234", testServiceUrl+"/message/greeting");

        HttpEntity<?> httpEntity = new HttpEntity<>(createHeaders("admin", "password"));

        ResponseEntity<String> response = getRestTemplate().exchange(url, HttpMethod.GET, httpEntity, String.class);
        assertEquals("Hello World!!!", response.toString());
    }

    @Test
    public void authorize_with_incorrectPassword_returns_401() {
        expectedException.expect(HttpClientErrorException.class);
        expectedException.expectMessage("401 null");

        String url = getAuthorizeUrl("code","devClient","openid", "1234", testServiceUrl+"/message/greeting");

        HttpEntity<?> httpEntity = new HttpEntity<>(createHeaders("admin", "password1"));

        getRestTemplate().exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

    private String getAuthorizeUrl(String responseType, String clientId, String scope, String state, String redirectUrl) {
        return securityAuthorizeUrl +
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
