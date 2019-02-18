package sk.pa3kc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;

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
                ex.printStackTrace();
                System.exit(0);
            }
    
            Path path = Paths.get(Singleton.getInstance().getWEB_ROOT());
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
                ex.printStackTrace();
            }

            new Thread(new Runnable()
            {
                @Override
                public void run() {
                    while (true)
                    {
                        WatchKey key = null;
                        
                        try
                        {
                            key = Singleton.getInstance().getWatchService().take();
                        }
                        catch (Throwable ex)
                        {
                            ex.printStackTrace();
                        }

                        for (WatchEvent<?> event : key.pollEvents())
                        {
                            WatchEvent.Kind<?> kind = event.kind();

                            if (kind == StandardWatchEventKinds.OVERFLOW) continue;
                            if (kind == StandardWatchEventKinds.ENTRY_CREATE) System.out.print("ENTRY_CREATE");
                            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) System.out.print("ENTRY_MODIFY");
                            if (kind == StandardWatchEventKinds.ENTRY_DELETE) System.out.print("ENTRY_DELETE");
                        }

                        if (key.reset() == false)
                            break;
                    }
                }
            }).start();
        }

        File serverDir = new File(Singleton.getInstance().getWEB_ROOT());
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

        for (int index = 0; index < devices.length; index++)
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

        if (Singleton.getInstance().getServer() == null)
            System.err.print("No usable network devices are available" + NEWLINE);
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
