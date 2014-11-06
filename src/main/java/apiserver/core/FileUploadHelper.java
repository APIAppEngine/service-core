package apiserver.core;

import apiserver.MimeType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Utilities to help services that support file uploads
 * Created by mnimer on 11/5/14.
 */
@Service
public class FileUploadHelper
{

    public MimeType getOutputFileFormat(String _format, MimeType _fileFormat)
    {
        if( _format != null ) {
            //parse for the proper http mime type
            return MimeType.getMimeType(_format);
        }else{
            // no defined format, fallback to matching the file
            return _fileFormat;
        }
    }


    /**
     * Files can be uploaded as a specific FILE property or in the fileMap of the request.
     * This is for the edge case when clients can't assign a property name to an attached file.
     * @param file
     * @param request
     * @return
     */
    public MultipartFile getFileFromRequest(MultipartFile file, HttpServletRequest request)
    {
        if( file == null ){
            Map<String, MultipartFile> fileMap = ((StandardMultipartHttpServletRequest) request).getFileMap();

            if( ((StandardMultipartHttpServletRequest) request).getFileMap().size() != 1 ){
                // no parts, or more then 1 - throw error
                throw new  RuntimeException("Invalid number of files uploaded.");
            }

            for (String key : fileMap.keySet()) {

                return fileMap.get(key);
            }

        }

        return file;
    }

}
