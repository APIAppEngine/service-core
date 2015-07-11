package apiserver.core.connectors.coldfusion.services;

import java.util.Map;

/**
 * Job that invokes a CFC that will return a Binary File
 * Created by mnimer on 4/17/14.
 */
public interface BinaryResult
{
    byte[] getResult();
    void setResult(byte[] bytes);

    Map toMap();
}
