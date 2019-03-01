package sk.pa3kc;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sk.pa3kc.http.HTTPRequest;
import sk.pa3kc.http.HTTPResponse;
import sk.pa3kc.http.constants.HTTPHeaders;
import sk.pa3kc.http.constants.HTTPResponseCodes;
import sk.pa3kc.mylibrary.util.StreamUtils;

public class ConnectionHandler
{
    private ConnectionHandler() {}

    public static void handle(final Socket client)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Singleton.getInstance().clientCounterLabel.setText(String.valueOf(++Singleton.getInstance().clientCounter));
                InputStream is = null;
                OutputStream os = null;

                try
                {
                    is = client.getInputStream();
                    os = client.getOutputStream();
                }
                catch (Throwable ex)
                {
                    ex.printStackTrace(System.out);
                }
                finally
                {
                    if (is == null || os == null)
                    {
                        StreamUtils.closeStreams(is, os);
                        Singleton.getInstance().clientCounterLabel.setText(String.valueOf(--Singleton.getInstance().clientCounter));
                        return;
                    }
                }

                HTTPRequest request = new HTTPRequest(is);
                if (request.getPath() == null || request.getPath().equals("/") == true)
                {
                    Singleton.getInstance().osClientCounterLabel.setText(String.valueOf(++Singleton.getInstance().osClientCounter));
                    HTTPResponse response = new HTTPResponse(os);

                    response.setProtocol(request.getProtocol());
                    response.setProperty(HTTPHeaders.Content_Type, "text/html");
                    response.setProperty(HTTPHeaders.Keep_Alive, "false");
                    response.setResponseCode(HTTPResponseCodes.OK_200);

                    response.setBody("<html><body><div id=\"content\">" + generateLinkList() + "</div></body></html>");
                    response.writeToOutput();

                    Singleton.getInstance().osClientCounterLabel.setText(String.valueOf(--Singleton.getInstance().osClientCounter));
                    Singleton.getInstance().clientCounterLabel.setText(String.valueOf(--Singleton.getInstance().clientCounter));
                    StreamUtils.closeStreams(os, is, client);
                    return;
                }
                
                String tmpPath = request.getPath().replaceFirst("/", "");
                boolean valid = false;
                int index = 0;
                for (; index < Singleton.getInstance().getFileCount(); index++)
                if (Singleton.getInstance().getFileNames()[index].equals(tmpPath) == true)
                {
                    valid = true;
                    break;
                }

                if (valid == true)
                {
                    Singleton.getInstance().osClientCounterLabel.setText(String.valueOf(++Singleton.getInstance().osClientCounter));
                    HTTPResponse response = new HTTPResponse(os);
                    response.setProtocol(request.getProtocol());
                    response.setResponseCode(HTTPResponseCodes.OK_200);
                    response.setProperty(HTTPHeaders.Content_Disposition, "attachment; filename\"" + Singleton.getInstance().getFileNames()[index] + "\"");
                    response.setProperty(HTTPHeaders.Transfer_Encoding, "chunked");

                    File file = new File(Singleton.getInstance().getFilePaths()[index]);
                    {
                        String extension = null;
                        int tmp = file.getAbsolutePath().lastIndexOf('.');
                        
                        if (tmp != -1)
                        extension = new String(file.getAbsolutePath().getBytes(), tmp, file.getAbsolutePath().length() - tmp);

                        response.setProperty(HTTPHeaders.Content_Type, getContentTypeByExtension(extension));
                    }

                    response.setProperty(HTTPHeaders.Content_Length, String.valueOf(file.length()));

                    response.writeHeaderToOutput();
                    response.writeBinaryFileToOutput(file);
                    Singleton.getInstance().osClientCounterLabel.setText(String.valueOf(--Singleton.getInstance().osClientCounter));
                }
                else
                {
                    Singleton.getInstance().osClientCounterLabel.setText(String.valueOf(++Singleton.getInstance().osClientCounter));
                    HTTPResponse response = new HTTPResponse(os);

                    response.setProtocol(request.getProtocol());
                    response.setProperty(HTTPHeaders.Content_Type, "text/html");
                    response.setBody("<html><body><p>ERROR</p></body></html>");
                    response.writeToOutput();
                    Singleton.getInstance().osClientCounterLabel.setText(String.valueOf(--Singleton.getInstance().osClientCounter));
                }

                StreamUtils.closeStreams(os, is, client);
                Singleton.getInstance().clientCounterLabel.setText(String.valueOf(--Singleton.getInstance().clientCounter));
            }
        }).start();
    }

    private static String getContentTypeByExtension(String extension)
    {
        if (extension == null) return "application/octet-stream";

        if (extension.equals(".txt") == true) return "text/plain";

        return "application/octet-stream";
    }

    private static String generateLinkList()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("<ul style=\"list-style: none;padding-left: 0;\">");
        for (int i = 0; i < Singleton.getInstance().getFileCount(); i++)
        {
            builder.append("<li><a href=\"<p><a href=\"");
            builder.append(Singleton.getInstance().getFileNames()[i]);
            builder.append("\" download>");
            builder.append(Singleton.getInstance().getFileNames()[i]);
            builder.append("</a></li>");
        }
        builder.append("<ul>");

        return builder.toString();
    }
}
