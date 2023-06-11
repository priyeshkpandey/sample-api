package client.model.request;

import client.auth.AuthI;
import client.contract.APIRequest;
import client.contract.APIRequestBody;
import client.contract.HttpMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Builder
public class GenericAPIRequest implements APIRequest {
    private String URL;
    private AuthI auth;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private Map<String, String> pathParams;
    private HttpMethod httpMethod;
    private APIRequestBody body;
}
