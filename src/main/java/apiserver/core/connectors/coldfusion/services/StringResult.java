package apiserver.core.connectors.coldfusion.services;

/**
 * Job that invokes a CFC that will return data
 * Created by mnimer on 4/17/14.
 */
public interface StringResult
{
    String getResult();
    void setResult(String object);
}
