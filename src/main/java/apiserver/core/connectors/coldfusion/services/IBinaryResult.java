package apiserver.core.connectors.coldfusion.services;

import java.util.Map;

/**
 * Job that invokes a CFC that will return a Binary File
 * Created by mnimer on 4/17/14.
 */
public interface IBinaryResult
{
    Integer getHttpStatus();
    void setHttpStatus(Integer code);

    Object getResult();
    void setResult(Object bytes);

    Map getHeaders();
    void setHeaders(Map headers);

}
