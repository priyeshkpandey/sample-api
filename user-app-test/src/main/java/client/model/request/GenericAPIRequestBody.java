package client.model.request;

import client.contract.APIRequestBody;
import client.contract.MultipartI;
import client.contract.RequestBodyType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class GenericAPIRequestBody implements APIRequestBody {
    private RequestBodyType type;
    private Object object;
    private List<MultipartI> multipartBodyList;
}
