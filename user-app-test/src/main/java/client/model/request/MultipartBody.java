package client.model.request;

import client.contract.MultipartI;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
@Builder
public class MultipartBody implements MultipartI {
    private String controlName;
    private String contentBody;
    private String mimeType;
    private File file;
}
