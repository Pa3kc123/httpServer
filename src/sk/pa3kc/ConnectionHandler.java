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
                response.setProperty(HTTPHeaders.Content_Type, "text/html");
                if (request.getPath().equals("/") == true)
                {
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
                    FileInputStream fileStream = null;
                    DataOutputStream dos = null;

                    try
                    {
                        fileStream = new FileInputStream(new File(Program.filePaths[index]));
                        dos = new DataOutputStream(os);

                        int c = -1;
                        while ((c = fileStream.read()) != -1)
                            dos.write(c);
                        dos.flush();
                    }
                    catch (Throwable ex)
                    {
                        ex.printStackTrace();
                    }
                    finally
                    {
                        Universal.closeStreams(dos, fileStream);
                    }
                }
                else
                {
                    response.setBody("<html><body><p>ERROR</p></body></html>");
                    response.writeToOutput();
                }

                Universal.closeStreams(os, is, client);
            }
        });
        thread.start();

        try
        {
            //thread.join();
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
