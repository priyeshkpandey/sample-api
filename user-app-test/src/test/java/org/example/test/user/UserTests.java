package org.example.test.user;

import api.model.request.LoginRequest;
import api.model.request.RegisterRequest;
import client.auth.AuthFactoryBuilder;
import client.contract.*;
import client.model.request.GenericAPIRequest;
import client.model.request.GenericAPIRequestBody;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import restassured.factory.RestAssuredAPIClientFactoryBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserTests {
    private static final Random random = new Random();
    private static final ObjectMapper mapper = new ObjectMapper();

    private APIClient client;
    private String baseUrl;
    private String dbUrl;
    private AmazonDynamoDB dynamoDBClient;


    @BeforeTest(groups = {"user"})
    public void beforeTestSetup(final ITestContext context) {
        context.setAttribute("client", RestAssuredAPIClientFactoryBuilder.getRestAssuredAPIClientFactory().get());
        this.baseUrl = "http://localhost:11000";
        this.dbUrl = "http://localhost:8000";
        context.setAttribute("base_url", this.baseUrl);
        this.dynamoDBClient = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummy-key", "dummy-secret")))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(this.dbUrl, Regions.US_EAST_1.getName()))
                .build();
        context.setAttribute("dynamo_client", this.dynamoDBClient);
    }

    @Test(dataProvider = "getRegistrationData")
    public void validateRegistration(String name, String email, String password, ITestContext context) {
        this.client = (APIClient) context.getAttribute("client");
        this.baseUrl = (String) context.getAttribute("base_url");
        this.dynamoDBClient = (AmazonDynamoDB) context.getAttribute("dynamo_client");
        final String userId = registerUser(name, email, password);
        DynamoDB dynamoDB = new DynamoDB(this.dynamoDBClient);
        Table userTable = dynamoDB.getTable("user");
        Item item = userTable.getItem("id", userId, "id, email, password", null);
        Assert.assertNotNull(item);
        Assert.assertEquals(item.get("email"), email);
        Assert.assertEquals(item.get("password"), password);
    }

    private String registerUser(String name, String email, String password) {
        String url = this.baseUrl + "/user/register";
        final RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName(name);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        final APIRequestBody requestBody = GenericAPIRequestBody.builder()
                .type(RequestBodyType.OBJECT).object(registerRequest).build();
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        final APIRequest request = GenericAPIRequest.builder()
                .URL(url).httpMethod(HttpMethod.POST).headers(headers)
                .body(requestBody).auth(AuthFactoryBuilder.getNoneAuthFactory().get()).build();
        final APIResponse response = this.client.execute(request);
        Assert.assertEquals(response.getStatusCode(), "200");
        final String userId = (String) response.getObject();
        return userId;
    }

    @Test(dataProvider = "getRegistrationData")
    public void validateLogin(String name, String email, String password, ITestContext context) {
        this.client = (APIClient) context.getAttribute("client");
        this.baseUrl = (String) context.getAttribute("base_url");
        this.dynamoDBClient = (AmazonDynamoDB) context.getAttribute("dynamo_client");
        registerUser(name, email, password);
        final String token = login(email, password);
        Assert.assertFalse(token.contains("ERROR"));
    }

    private String login(String email, String password) {
        String url = this.baseUrl + "/user/login";
        final LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        final APIRequestBody requestBody = GenericAPIRequestBody.builder()
                .type(RequestBodyType.OBJECT).object(loginRequest).build();
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        final APIRequest request = GenericAPIRequest.builder()
                .URL(url).httpMethod(HttpMethod.POST).headers(headers)
                .body(requestBody).auth(AuthFactoryBuilder.getNoneAuthFactory().get()).build();
        final APIResponse response = this.client.execute(request);
        Assert.assertEquals(response.getStatusCode(), "200");
        final String token = (String) response.getObject();
        return token;
    }

    @Test
    public void validateInvalidLogin(ITestContext context) {
        this.client = (APIClient) context.getAttribute("client");
        this.baseUrl = (String) context.getAttribute("base_url");
        final String token = login("no-email@acme.com", "no-pass");
        Assert.assertTrue(token.contains("ERROR"));
    }

    @Test(dataProvider = "getRegistrationData")
    public void validateProtectedResource(String name, String email, String password, ITestContext context) throws JsonProcessingException {
        this.client = (APIClient) context.getAttribute("client");
        this.baseUrl = (String) context.getAttribute("base_url");
        registerUser(name, email, password);
        final String token = login(email, password);
        Assert.assertFalse(token.contains("ERROR"));
        final Map<String, String> loginResponse = mapper.readValue(token, Map.class);
        final String content = getProtectedResource(loginResponse.get("loginToken"));
        Assert.assertEquals(content, "Secure content accessible");
    }

    private String getProtectedResource(String token) {
        String url = this.baseUrl + "/user/resource";
        final APIRequestBody requestBody = GenericAPIRequestBody.builder()
                .type(RequestBodyType.OBJECT).object(token).build();
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        headers.put("Accept", "text/plain");
        final APIRequest request = GenericAPIRequest.builder()
                .URL(url).httpMethod(HttpMethod.GET).headers(headers)
                .body(requestBody).auth(AuthFactoryBuilder.getNoneAuthFactory().get()).build();
        final APIResponse response = this.client.execute(request);
        Assert.assertEquals(response.getStatusCode(), "200");
        final String content = (String) response.getObject();
        return content;
    }

    @DataProvider
    public Object[][] getRegistrationData() {
        Object[][] data = new Object[1][3];
        String name = "Test" + random.nextInt(1000);
        String email = name + "@acme.com";
        String password = name + "_pass";
        data[0][0] = name;
        data[0][1] = email;
        data[0][2] = password;
        return data;
    }

}
