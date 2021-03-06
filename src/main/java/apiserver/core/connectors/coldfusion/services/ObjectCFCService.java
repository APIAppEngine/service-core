package apiserver.core.connectors.coldfusion.services;

import apiserver.core.connectors.coldfusion.IColdFusionBridge;
import apiserver.exceptions.ColdFusionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;

import java.util.Map;

/**
 * Used to invoke CFC that returns data
 * Created by mnimer on 4/16/14.
 */
public class ObjectCFCService
{
    @Autowired
    public IColdFusionBridge coldFusionBridge;

    private String cfcMethod = null;
    private String cfcPath = null;



    public String getCfcMethod()
    {
        return cfcMethod;
    }

    public void setCfcMethod(String cfcMethod)
    {
        this.cfcMethod = cfcMethod;
    }


    public String getCfcPath()
    {
        return cfcPath;
    }

    public void setCfcPath(String cfcPath)
    {
        this.cfcPath = cfcPath;
    }


    public void setColdFusionBridge(IColdFusionBridge coldFusionBridge)
    {
        this.coldFusionBridge = coldFusionBridge;
    }


    public Object execute(Message<?> message) throws ColdFusionException
    {

        IObjectResult props = (IObjectResult)message.getPayload();

        try
        {
            // extract properties
            Map<String, Object> methodArgs = coldFusionBridge.extractPropertiesFromPayload(props);

            // execute
            ResponseEntity<byte[]> cfResult = coldFusionBridge.invokeFilePost(cfcPath, cfcMethod, methodArgs);
            props.setResult(cfResult.getBody());


            return message;
        }
        catch (Throwable e)
        {
            e.printStackTrace(); //todo use logging library
            throw new RuntimeException(e);
        }
    }

}
