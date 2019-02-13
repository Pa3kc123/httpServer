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

import sk.pa3kc.mylibrary.net.Device;
import sk.pa3kc.mylibrary.util.StreamUtils;

import static sk.pa3kc.Singleton.NEWLINE;

public class Program
{
    public static void init()
    {
        File serverDir = new File(Singleton.getInstance().getWEB_ROOT());
        if (serverDir.exists() == false)
            serverDir.mkdirs();

        Singleton.getInstance().setFileNames(serverDir.list());
 
        File[] files = serverDir.listFiles();
        Singleton.getInstance().setFilePaths(new String[files.length]);
 
        for (int i = 0; i < files.length; i++)
            Singleton.getInstance().getFilePaths()[i] = files[i].getAbsolutePath();

        Singleton.getInstance().setFileCount(Singleton.getInstance().getFileNames().length);

        System.out.print("CWD = " + Singleton.getInstance().getCWD() + NEWLINE);
        System.out.print("WEB_ROOT = " + Singleton.getInstance().getWEB_ROOT() + NEWLINE);

        Device[] devices = Device.getUsableDevices();

        if (devices.length == 0)
        {
            System.err.print("No network devices are available" + NEWLINE);
            return;
        }

        for (int index = 0; index <= devices.length; index++)
        {
            try
            {
                Singleton.getInstance().setServer(new ServerSocket(8080, 0, InetAddress.getByName(devices[0].getLocalIP().asFormattedString())));
                Singleton.getInstance().setDevice(devices[index]);
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
        }

        if (Singleton.getInstance().getServer() == null)
            System.err.print("No usable network devices are available" + NEWLINE);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.print(NEWLINE + "Closing server ... ");
                StreamUtils.closeStreams(Singleton.getInstance().getServer());
                System.out.print("DONE" + NEWLINE);
            }
        }));
    }

    public static void main(String[] args)
    {
        init();

        while (true)
        {
            try
            {
                System.out.print("Awaiting client on " + Singleton.getInstance().getServer().getLocalSocketAddress() + " ... ");
                final Socket client = Singleton.getInstance().getServer().accept();
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
