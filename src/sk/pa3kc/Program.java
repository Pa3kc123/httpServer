package sk.pa3kc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import sk.pa3kc.mylibrary.Universal;
import sk.pa3kc.mylibrary.DefaultSystemPropertyStrings;
import sk.pa3kc.mylibrary.Device;

public class Program
{
    public static final String NEWLINE = DefaultSystemPropertyStrings.LINE_SEPARATOR;
    public static ServerSocket server;

    public static void main(String[] args)
    {
        Device[] devices = Universal.getUsableDevices();

        try
        {
            server = new ServerSocket(8080, 0, InetAddress.getByName(devices[0].getLocalIP().asFormattedString()));
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }

        while (true)
        {
            Socket tmpClient = null;

            try
            {
                System.out.print("Awaiting client on " + server.getLocalSocketAddress() + " ... ");
                tmpClient = server.accept();
                System.out.print("CONNECTED" + NEWLINE);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }

            if (tmpClient == null) continue;

            final Socket client = tmpClient;
            new Thread(new Runnable()
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

                    String header = request.getProtocol() + " 200 OK\r\n\r\n";
                    String body = "<p>" + new Date().toString() + "</p>";

                    String result = (header + body);
                    System.out.print("Sending: '" + result + "'" + NEWLINE);
                    
                    try
                    {
                        os.write(result.getBytes("UTF-8"));
                    }
                    catch (Throwable ex)
                    {
                        ex.printStackTrace();
                    }

                    Universal.closeStreams(os, is, client);
                }
            }).start();

            System.out.print("-------------------------" + NEWLINE);
            System.out.flush();
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        Universal.closeStreams(server);
        super.finalize();
    }
}
