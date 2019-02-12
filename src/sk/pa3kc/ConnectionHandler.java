package sk.pa3kc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sk.pa3kc.httpconstants.HTTPHeaders;
import sk.pa3kc.httpconstants.HTTPResponseCodes;
import sk.pa3kc.mylibrary.Universal;

public class ConnectionHandler
{
    private ConnectionHandler() {}

    public static void handle(final Socket client)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                InputStream is = null;
                OutputStream os = null;

                try
                {
                    is = client.getInputStream();
                    os = client.getOutputStream();
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    if (is == null || os == null)
                    {
                        if (is != null) Universal.closeStreams(is);
                        if (os != null) Universal.closeStreams(os);
                        return;
                    }
                }

                HTTPRequest request = new HTTPRequest(is);
                if (request.getPath().equals("/") == true)
                {
                    HTTPResponse response = new HTTPResponse(os);

                    response.setProtocol(request.getProtocol());
                    response.setProperty(HTTPHeaders.Content_Type, "text/html");
                    response.setResponseCode(HTTPResponseCodes.OK_200);

                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < Program.fileCount; i++)
                        builder.append("<p><a href=\"" + Program.fileNames[i] + "\" download>" + Program.fileNames[i] + "</a></p>");

                    response.setBody("<html><body>" + builder.toString() + "</body></html>");
                    response.writeToOutput();

                    Universal.closeStreams(os, is, client);
                    return;    
                }
                
                String tmpPath = request.getPath().replaceFirst("/", "");
                boolean valid = false;
                int index = 0;
                for (; index < Program.fileCount; index++)
                if (Program.fileNames[index].equals(tmpPath) == true)
                {
                    valid = true;
                    break;
                }

                if (valid == true)
                {
                    HTTPResponse response = new HTTPResponse(os);
                    response.setProtocol(request.getProtocol());
                    response.setResponseCode(HTTPResponseCodes.OK_200);
                    response.setProperty(HTTPHeaders.Content_Disposition, "attachment; filename\"" + Program.fileNames[index] + "\"");
                    response.setProperty(HTTPHeaders.Transfer_Encoding, "chunked");

                    File file = new File(Program.filePaths[index]);
                    {
                        String extension = null;
                        int tmp = file.getAbsolutePath().lastIndexOf('.');
                        
                        if (tmp != -1)
                        extension = new String(file.getAbsolutePath().getBytes(), tmp, file.getAbsolutePath().length() - tmp);

                        if (extension != null && extension.equals("txt") == true)
                            response.setProperty(HTTPHeaders.Content_Type, "text/plain");
                        else
                            //?response.setProperty(HTTPHeaders.Content_Type, java.net.URLConnection.guessContentTypeFromName(Program.fileNames[index]));
                            response.setProperty(HTTPHeaders.Content_Type, "application/octet-stream");
                    }

                    response.setProperty(HTTPHeaders.Content_Length, String.valueOf(file.length()));

                    response.writeHeaderToOutput();
                    response.writeBinaryFileToOutput(file);
                }
                else
                {
                    HTTPResponse response = new HTTPResponse(os);

                    response.setProtocol(request.getProtocol());
                    response.setProperty(HTTPHeaders.Content_Type, "text/html");
                    response.setBody("<html><body><p>ERROR</p></body></html>");
                    response.writeToOutput();
                }

                Universal.closeStreams(os, is, client);
            }
        });
        thread.start();
    }
}
