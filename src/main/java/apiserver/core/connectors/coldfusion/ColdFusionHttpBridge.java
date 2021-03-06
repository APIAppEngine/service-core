package apiserver.core.connectors.coldfusion;

/*******************************************************************************
 Copyright (c) 2013 Mike Nimer.

 This file is part of ApiServer Project.

 The ApiServer Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 The ApiServer Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with the ApiServer Project.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

import apiserver.ApiServerConstants;
import apiserver.MimeType;
import apiserver.core.model.IDocument;
import apiserver.core.model.IDocumentJob;
import apiserver.exceptions.ColdFusionException;
import apiserver.jobs.IProxyJob;
import apiserver.model.Document;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * User: mikenimer
 * Date: 3/24/13
 */
@Component(value = "ColdFusionHttpBridge")
public class ColdFusionHttpBridge implements IColdFusionBridge
{
    private @Value("${cf.host}") String cfHost;
    private @Value("${cf.port}") Integer cfPort;
    private @Value("${cf.protocol}") String cfProtocol;
    private @Value("${cf.path}") String cfPath;
    private @Value("${cf.defaultTimeout}") Integer defaultTimeout;

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



    public Object execute(Message<?> message) throws ColdFusionException
    {

        IProxyJob props = (IProxyJob)message.getPayload();

        try
        {
            // extract properties
            //Map<String, Object> methodArgs = coldFusionBridge.extractPropertiesFromPayload(props);

            // execute
            ResponseEntity cfResults = invokeFilePost(cfcPath, cfcMethod, props.getArguments());

            props.setHttpResponse(cfResults);

            return message;
        }
        catch (Throwable e)
        {
            e.printStackTrace(); //todo use logging library
            throw new RuntimeException(e);
        }
    }




    public ResponseEntity invokeFilePost(String cfcPath_, String method_, Map<String,Object> methodArgs_) throws ColdFusionException
    {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()){

            HttpHost host = new HttpHost(cfHost, cfPort, cfProtocol);
            HttpPost method = new HttpPost( validatePath(cfPath) +cfcPath_);

            MultipartEntityBuilder me = MultipartEntityBuilder.create();
            me.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            if( methodArgs_ != null )
            {
                for (String s : methodArgs_.keySet())
                {
                    Object obj = methodArgs_.get(s);

                    if( obj != null )
                    {
                        if (obj instanceof String)
                        {
                            me.addTextBody( s, (String)obj );
                        }
                        else if (obj instanceof Integer)
                        {
                            me.addTextBody( s, ((Integer)obj).toString() );
                        }
                        else if (obj instanceof File)
                        {
                            me.addBinaryBody(s, (File) obj);
                        }
                        else if (obj instanceof IDocument)
                        {
                            me.addBinaryBody(s, ((IDocument) obj).getFile() );
                            //me.addTextBody( "name", ((IDocument)obj).getFileName() );
                            //me.addTextBody("contentType", ((IDocument) obj).getContentType().contentType );
                        }
                        else if (obj instanceof IDocument[])
                        {
                            for (int i = 0; i < ((IDocument[]) obj).length; i++) {
                                IDocument iDocument = ((IDocument[]) obj)[i];
                                me.addBinaryBody(s, iDocument.getFile() );
                                //me.addTextBody("name", iDocument.getFileName() );
                                //me.addTextBody("contentType", iDocument.getContentType().contentType );
                            }

                        }
                        else if (obj instanceof BufferedImage)
                        {

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write((BufferedImage) obj, "jpg", baos);

                            String _fileName = (String) methodArgs_.get(ApiServerConstants.FILE_NAME);
                            String _mimeType = ((MimeType) methodArgs_.get(ApiServerConstants.CONTENT_TYPE)).getExtension();
                            ContentType _contentType = ContentType.create(_mimeType);
                            me.addBinaryBody(s, baos.toByteArray(), _contentType, _fileName);
                        }
                        else if (obj instanceof byte[])
                        {
                            me.addBinaryBody(s, (byte[]) obj);
                        }
                        else if (obj instanceof Map)
                        {
                            ObjectMapper mapper = new ObjectMapper();
                            String _json = mapper.writeValueAsString(obj);

                            me.addTextBody(s, _json);
                        }
                    }
                }
            }

            HttpEntity httpEntity = me.build();
            method.setEntity(httpEntity);

            HttpResponse response = httpClient.execute(host, method);//, responseHandler);

            // Examine the response status
            if( response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                // Get hold of the response entity
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream inputStream = entity.getContent();
                    //return inputStream;

                    byte[] _body = IOUtils.toByteArray(inputStream);

                    MultiValueMap _headers = new LinkedMultiValueMap();
                    for (Header header : response.getAllHeaders()) {
                        if(  header.getName().equalsIgnoreCase("content-length") ) {
                            _headers.add(header.getName(), header.getValue());
                        }
                        else if( header.getName().equalsIgnoreCase("content-type")  ) {
                            _headers.add(header.getName(), header.getValue());

                            // special condition to add zip to the file name.
                            if( header.getValue().indexOf("text/") > -1 ) {
                                //add nothing extra
                            }else if( header.getValue().indexOf("zip") > -1 ) {
                                if( methodArgs_.get("file") != null ) {
                                    String _fileName = ((Document)methodArgs_.get("file")).getFileName();
                                    _headers.add("Content-Disposition", "attachment; filename=\"" +_fileName +".zip\"");
                                }
                            }else if( methodArgs_.get("file") != null ) {
                                String _fileName = ((Document)methodArgs_.get("file")).getFileName();
                                _headers.add("Content-Disposition", "attachment; filename=\"" +_fileName +"\"");
                            }

                        }
                    }

                    return new ResponseEntity(_body, _headers, org.springframework.http.HttpStatus.OK);
                    //Map json = (Map)deSerializeJson(inputStream);
                    //return json;
                }
            }

            MultiValueMap _headers = new LinkedMultiValueMap();
            _headers.add("Content-Type", "text/plain");
            return new ResponseEntity(response.getStatusLine().toString(), _headers, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }



    protected Object deSerializeJson(InputStream result) throws IOException, JsonParseException
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.readValue(result, new TypeReference<Map<String,Object>>() { });

        return map;
    }


    /**
     * make sure the path starts and ends with /'s
     * @param path
     */
    protected String validatePath(String path)
    {
        String _path = path.trim();
        if( !_path.startsWith("/") )
        {
            _path = "/" +path;
        }

        if( !_path.endsWith("/") )
        {
            _path = _path +"/";
        }

        _path = _path.replace("//", "/");
        return _path;
    }


    /**
     * return all of the message payload properties, without http request/response parts.
     * @param props
     * @return
     */
    public Map<String, Object> extractPropertiesFromPayload(Object props) throws IOException
    {
        Map<String, Object> methodArgs = new HashMap<String, Object>();


        if( props instanceof Map )
        {
            methodArgs.putAll( (Map)props );
        }
        else
        {
            Method[] methods = props.getClass().getDeclaredMethods();

            for (Method method : methods)
            {
                if( method.getName().startsWith("get") )
                {
                    try
                    {
                        String name = method.getName().substring(3, method.getName().length());
                        Object val = method.invoke(props, null);

                        methodArgs.put(name, val);
                    }catch (Exception ex){}
                }
            }

            if( props instanceof IDocumentJob)
            {
                methodArgs.put(ApiServerConstants.IMAGE, ((IDocumentJob)props).getDocument().getFile() );
            }
        }

        return methodArgs;
    }
}
