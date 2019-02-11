package sk.pa3kc;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import sk.pa3kc.httpconstants.HTTPHeaders;
import sk.pa3kc.httpconstants.HTTPResponseCodes;
import sk.pa3kc.mylibrary.Universal;

import static sk.pa3kc.Program.NEWLINE;

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
                HTTPResponse response = new HTTPResponse(os);
                response.setProtocol(request.getProtocol());
                response.setResponseCode(HTTPResponseCodes.OK_200);
                response.setProperty(HTTPHeaders.Content_Type, "text/html");
                response.writeToOutput();

                Universal.closeStreams(os, is, client);
            }
        });
        thread.start();
    }
}
