package com.consol.citrus.http;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.consol.citrus.testng.AbstractIntegrationTest;

/**
 * 
 * @author deppisch Christoph Deppisch Consol* Software GmbH
 * @since 31.10.2008
 */
public class HttpServerStandaloneTest extends AbstractIntegrationTest {
    @Test
    public void httpServerStandaloneTest(ITestContext testContext) {
        executeTest(testContext);
    }
}