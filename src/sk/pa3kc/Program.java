package sk.pa3kc;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import sk.pa3kc.mylibrary.net.Device;

import static sk.pa3kc.Singleton.NEWLINE;

public class Program
{
    public static void init()
    {
        {
            try
            {
                Singleton.getInstance().setWatchService(FileSystems.getDefault().newWatchService());
            }
            catch (Throwable ex)
            {
                ex.printStackTrace(System.out);
                System.exit(0);
            }
    
            Path path = Paths.get(Singleton.getInstance().WEB_ROOT);
            try
            {
                @SuppressWarnings("unchecked")
                WatchEvent.Kind<Path>[] events = new WatchEvent.Kind[]
                {
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE
                };
                path.register(Singleton.getInstance().getWatchService(), events);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace(System.out);
            }

            new Thread(new Runnable()
            {
                @Override
                public void run() {
                    while (true)
                    {
                        try
                        {
                            File serverDir = new File(Singleton.getInstance().WEB_ROOT);
                            Singleton.getInstance().setFileNames(serverDir.list());

                            File[] files = serverDir.listFiles();
                            Singleton.getInstance().setFilePaths(new String[files.length]);

                            for (int i = 0; i < files.length; i++)
                                Singleton.getInstance().getFilePaths()[i] = files[i].getAbsolutePath();

                            Singleton.getInstance().setFileCount(Singleton.getInstance().getFileNames().length);

                            Singleton.getInstance().getWatchService().take();
                        }
                        catch (ClosedWatchServiceException ex)
                        {
                            System.err.print(ex.getClass().getName() + " -> " + ex.getMessage() + NEWLINE);
                            return;
                        }
                        catch (Throwable ex)
                        {
                            ex.printStackTrace(System.out);
                        }
                    }
                }
            }).start();
        }

        System.out.print("CWD = " + Singleton.getInstance().CWD + NEWLINE);
        System.out.print("WEB_ROOT = " + Singleton.getInstance().WEB_ROOT + NEWLINE);

        Device[] devices = Device.getUsableDevices();

        if (devices.length == 0)
        {
            System.err.print("No network devices are available" + NEWLINE);
            return;
        }

        for (int index = 0; index < devices.length; index++)
        try
        {
            Singleton.getInstance().setServer(new ServerSocket(8080));
            Singleton.getInstance().setDevice(devices[index]);
            break;
        }
        catch (BindException ex)
        {
            System.out.print(ex.getClass().getName() + " -> " + ex.getMessage() + NEWLINE);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace(System.out);
        }

        if (Singleton.getInstance().getServer() == null)
        {
            System.err.print("No usable network devices are available" + NEWLINE);
            System.exit(0xFFFFFFFF);
        }
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
                ex.printStackTrace(System.out);
            }
            catch (IllegalBlockingModeException ex)
            {
                ex.printStackTrace(System.out);
            }
            catch (Throwable ex)
            {
                ex.printStackTrace(System.out);
            }
        }
    }
}
