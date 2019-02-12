package sk.pa3kc;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;

import sk.pa3kc.mylibrary.Universal;
import sk.pa3kc.mylibrary.myregex.MyRegex;
import sk.pa3kc.mylibrary.DefaultSystemPropertyStrings;
import sk.pa3kc.mylibrary.Device;

public class Program
{
    public static final String NEWLINE = DefaultSystemPropertyStrings.LINE_SEPARATOR;
    public static String CWD;
    public static String SERVER_ROOT;
    public static String[] fileNames;
    public static String[] filePaths;
    public static int fileCount;

    private static Device[] devices;
    private static int index = 0;

    public static ServerSocket server;

    public static void init()
    {
        String tmp = Program.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        CWD = MyRegex.Matches(tmp, "(.*)\\/.*.jar")[0];
        SERVER_ROOT = CWD + "/web";

        File serverDir = new File(SERVER_ROOT);
        if (serverDir.exists() == false)
            serverDir.mkdirs();

        fileNames = serverDir.list();
 
        File[] files = serverDir.listFiles();
        filePaths = new String[files.length];
 
        for (int i = 0; i < files.length; i++)
            filePaths[i] = files[i].getAbsolutePath();

        fileCount = fileNames.length;

        System.out.print("CWD = " + CWD + NEWLINE);
        System.out.print("SERVER_ROOT = " + SERVER_ROOT + NEWLINE);
        System.out.print("FILE_COUNT = " + fileCount + NEWLINE);
    }

    public static void main(String[] args)
    {
        init();

        devices = Universal.getUsableDevices();

        if (devices.length == 0)
        {
            System.err.print("No network devices are available" + NEWLINE);
            return;
        }

        while (index <= devices.length)
        {
            try
            {
                server = new ServerSocket(8080, 0, InetAddress.getByName(devices[0].getLocalIP().asFormattedString()));
                break;
            }
            catch (BindException ex)
            {
                System.out.print(ex.getClass().getName() + " -> " + ex.getMessage() + NEWLINE);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }
            server = null;
        }

        if (server == null)
            System.err.print("No usable network devices are available" + NEWLINE);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.print(NEWLINE + "Closing server ... ");
                Universal.closeStreams(server);
                System.out.print("DONE" + NEWLINE);
            }
        }));

        while (true)
        {
            try
            {
                System.out.print("Awaiting client on " + server.getLocalSocketAddress() + " ... ");
                final Socket client = server.accept();
                ConnectionHandler.handle(client);
                System.out.print("CONNECTED (" + client.getInetAddress().getHostAddress() + ")" + NEWLINE);
            }
            catch (SecurityException ex)
            {
                System.err.print(ex.getClass().getName() + " -> " + ex.getMessage() + NEWLINE);
            }
            catch (SocketTimeoutException ex)
            {
                System.err.print(ex.getClass().getName() + " -> " + ex.getMessage() + NEWLINE);
            }
            catch (SocketException ex)
            {
                System.err.print(ex.getClass().getName() + " -> " + ex.getMessage() + NEWLINE);
                return;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalBlockingModeException ex)
            {
                ex.printStackTrace();
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
