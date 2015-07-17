package apiserver.jobs;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Created by mnimer on 7/17/15.
 */
public interface IProxyJob
{
    Map getArguments();

    ResponseEntity getHttpResponse();
    void setHttpResponse(ResponseEntity response_);
}
