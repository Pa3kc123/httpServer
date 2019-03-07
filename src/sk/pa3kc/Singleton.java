package sk.pa3kc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import sk.pa3kc.mylibrary.DefaultSystemPropertyStrings;
import sk.pa3kc.mylibrary.myregex.MyRegex;
import sk.pa3kc.mylibrary.net.Device;
import sk.pa3kc.mylibrary.util.StreamUtils;

public class Singleton
{
    //region Singleton
    private static final Singleton instance = new Singleton();
    public static final Singleton getInstance() { return instance; }
    private Singleton()
    {
        //* Class loader
        this.classLoader = this.getClass().getClassLoader();

        //* CWD & WEB_ROOT
        String path = Program.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceFirst("\\/", "");
        this.CWD = path.endsWith(".jar") == true ? MyRegex.Matches(path, "(.*)\\/.*.jar")[0] : path;
        this.WEB_ROOT = this.CWD + "/web";

        //* Creating web directory
        File webRootFile = new File(this.WEB_ROOT);
        if (webRootFile.exists() == false || webRootFile.isDirectory() == false)
            webRootFile.mkdirs();

        //* JavaScript engine
        this.scriptEngine = new ScriptEngineManager().getEngineByExtension("JavaScript");

        //* Loading mime types
        InputStream stream = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        List<String> extensions = new ArrayList<String>();
        List<String> contentTypes = new ArrayList<String>();
 
        try
        {
            stream = this.classLoader.getResourceAsStream("assets/exts.mime");
            streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            int lineNumber = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                if (line.startsWith("#") == true || line.equals("") == true) continue;
                
                String[] values = line.split("=", 2);
                if (values.length != 2)
                {
                    System.err.print("ERROR while loading mime types -> Invalid format (line " + lineNumber + ")" + NEWLINE);
                    System.err.flush();
                    System.exit(0);
                }

                extensions.add(values[0]);
                contentTypes.add(values[1]);

                lineNumber++;
            }

        }
        catch (Throwable ex)
        {
            ex.printStackTrace(System.out);
        }
        finally
        {
            StreamUtils.closeStreams(reader, streamReader, stream);
        }
        this.extensions = extensions.toArray(new String[0]);
        this.contentTypes = contentTypes.toArray(new String[0]);

        //* Web directory watch service
        try
        {
            this.watchService = FileSystems.getDefault().newWatchService();
        }
        catch (Throwable ex)
        {
            ex.printStackTrace(System.out);
            System.exit(0);
        }

        Path webDirPath = Paths.get(this.WEB_ROOT);
        try
        {
            @SuppressWarnings("unchecked")
            WatchEvent.Kind<Path>[] events = new WatchEvent.Kind[]
            {
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            };
            webDirPath.register(this.watchService, events);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace(System.out);
        }

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                File serverDir = new File(Singleton.getInstance().WEB_ROOT);

                while (true)
                {
                    Singleton.getInstance().setFileNames(serverDir.list());
                    Singleton.getInstance().setFileCount(Singleton.getInstance().getFileNames().length);
                    Singleton.getInstance().setFilePaths(new String[Singleton.getInstance().getFileCount()]);
                    {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < Singleton.getInstance().getFileCount(); i++)
                        {
                            builder.append(serverDir.getAbsolutePath());
                            builder.append(DefaultSystemPropertyStrings.FILE_SEPARATOR);
                            builder.append(Singleton.getInstance().getFileNames()[i]);
                            Singleton.getInstance().getFilePaths()[i] = builder.toString();
                            builder.delete(0, builder.length());
                        }
                        builder = null;
                    }

                    try
                    {
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

        //* Net devices
        Device[] devices = Device.getUsableDevices();

        if (devices.length == 0)
        {
            System.err.print("No network devices are available" + NEWLINE);
            return;
        }

        //* Http server socket
        for (int index = 0; index < devices.length; index++)
        try
        {
            final int PORT = 8080;
            final int BACKLOG = 0;
            final String BIND_ADDRESS = devices[index].getLocalIP().asFormattedString();

            this.server = new ServerSocket(PORT, BACKLOG, InetAddress.getByName(BIND_ADDRESS));
            this.device = devices[index];
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

        if (this.server == null)
        {
            System.err.print("No usable network devices are available" + NEWLINE);
            System.exit(0xFFFFFFFF);
        }

        //* On application exit event
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.print(NEWLINE + "Closing streams ... ");
                StreamUtils.closeStreams(watchService, server);        
                System.out.print("DONE" + NEWLINE);
            }
        }));
    }
    //endregion
    //region properties
    public static final String NEWLINE = DefaultSystemPropertyStrings.LINE_SEPARATOR;
    public final String CWD;
    public final String WEB_ROOT;
    public final ClassLoader classLoader;
    public final ScriptEngine scriptEngine;
    public final String[] extensions;
    public final String[] contentTypes;
 
    private Device device;
    private ServerSocket server;
    private WatchService watchService;

    private String[] fileNames;
    private String[] filePaths;
    private int fileCount;
    //endregion
    //region Getters
    public Device getDevice() { return this.device; }
    public ServerSocket getServer() { return this.server; }
    public int getFileCount() { return this.fileCount; }
    public String[] getFileNames() { return this.fileNames; }
    public String[] getFilePaths() { return this.filePaths; }
    public WatchService getWatchService() { return this.watchService; }
    //endregion
    //region Setters
    public void setDevice(Device value) { this.device = value; }
    public void setServer(ServerSocket value) { this.server = value; }
    public void setFileCount(int value) { this.fileCount = value; }
    public void setFileNames(String[] value) { this.fileNames = value; }
    public void setFilePaths(String[] value) { this.filePaths = value; }
    public void setWatchService(WatchService value) { this.watchService = value; }
    //endregion
}
