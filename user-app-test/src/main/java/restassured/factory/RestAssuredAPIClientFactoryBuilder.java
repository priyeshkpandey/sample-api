package restassured.factory;

import client.factory.APIClientFactoryI;

public class RestAssuredAPIClientFactoryBuilder {
    private static RestAssuredAPIClientFactory restAssuredAPIClientFactory;

    public static APIClientFactoryI getRestAssuredAPIClientFactory() {
        if (null == restAssuredAPIClientFactory) {
            restAssuredAPIClientFactory = new RestAssuredAPIClientFactory();
        }
        return restAssuredAPIClientFactory;
    }
}
