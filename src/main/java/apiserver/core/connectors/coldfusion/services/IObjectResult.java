package apiserver.core.connectors.coldfusion.services;

import java.util.Map;

/**
 * Job that invokes a CFC that will return data
 * Created by mnimer on 4/17/14.
 */
public interface IObjectResult
{
    Object getResult();
    void setResult(Object object);

    Map getHeaders();
    void setHeaders(Map headers);
}
