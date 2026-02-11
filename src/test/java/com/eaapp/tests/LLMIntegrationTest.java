package com.eaapp.tests;

import com.eaapp.tests.utilities.LLMClient;
import com.eaapp.tests.pages.EnhancedLoginPage;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * Test class demonstrating LLMClient usage
 * 
 * Prerequisites:
 * - Ollama running locally: ollama serve
 * - Model pulled: ollama pull qwen3-code:30b
 * - Service available at: http://localhost:11434/api/generate
 */
public class LLMIntegrationTest {
    private static final Logger logger = Logger.getLogger(LLMIntegrationTest.class);
    private LLMClient llmClient;

    @BeforeMethod
    public void setUp() {
        logger.info("========== Starting LLM Test ==========");
        llmClient = new LLMClient();
    }

    @AfterMethod
    public void tearDown() {
        logger.info("========== LLM Test Complete ==========\n");
    }

    @Test(description = "Call LLM with simple prompt")
    public void testSimpleLLMCall() throws Exception {
        logger.info("Test: Simple LLM Call");
        
        String prompt = "Write a basic Selenium java code to login to aeepp.com";
        String response = llmClient.callLLM(prompt);
        
        Assert.assertNotNull(response, "LLM response should not be null");
        Assert.assertTrue(response.length() > 0, "LLM response should not be empty");
        
        logger.info("LLM Response: " + response);
    }
}
