package restassured.factory;

import client.contract.APIClient;
import client.factory.APIClientFactoryI;
import restassured.RestAssuredClient;

public class RestAssuredAPIClientFactory implements APIClientFactoryI {
    @Override
    public APIClient get() {
        return new RestAssuredClient();
    }
}
