package com.babcock.integration.asserter;

import com.noveria.assertion.exception.WaitUntilAssertionError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.fail;

@Component
@TestPropertySource("classpath:application.properties")
public class WaitForHelper {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${test.service.url}")
    String baseUrl;

    private static boolean serviceUnavailable = false;

    public void waitForServices() throws InterruptedException {
        WaitForService waitForService = new WaitForService(baseUrl + "/info", restTemplate);
        waitForService.setMaxWaitTime(720000);

        if(serviceUnavailable) {
            fail("security-api-test docker environment unavailable");
        }

        System.out.println("waiting for test : " + baseUrl + "/info");

        try {
            waitForService.performAssertion();
        }catch (WaitUntilAssertionError wae) {
            serviceUnavailable = true;
            fail("user-test docker environment unavailable");
        }
    }

}
