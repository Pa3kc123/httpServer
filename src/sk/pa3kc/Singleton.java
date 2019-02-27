package sk.pa3kc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
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
    private Singleton()
    {
        String path = Program.class.getProtectionDomain().getCodeSource().getLocation().getFile().replaceFirst("\\/", "");
        this.CWD = path.endsWith(".jar") == true ? MyRegex.Matches(path, "(.*)\\/.*.jar")[0] : path;
        this.WEB_ROOT = this.CWD + "/web";

        File webRootFile = new File(this.WEB_ROOT);
        if (webRootFile.exists() == false || webRootFile.isDirectory() == false)
            webRootFile.mkdirs();

        this.scriptEngine = new ScriptEngineManager().getEngineByExtension("JavaScript");

        InputStream stream = null;
        InputStreamReader streamReader = null;
        BufferedReader reader = null;
        try
        {
            stream = this.getClass().getResourceAsStream("assets/exts.mime");
            streamReader = new InputStreamReader(stream);
            reader = new BufferedReader(streamReader);

            List<String> extensions = new ArrayList<String>();
            List<String> contentTypes = new ArrayList<String>();
            int lineNumber = 1;
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                if (line.startsWith("#") == true) continue;
                
                String[] values = line.split("=");
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

            this.extensions = extensions.toArray(new String[0]);
            this.contentTypes = contentTypes.toArray(new String[0]);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            StreamUtils.closeStreams(reader, streamReader, stream);
        }

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
    public static final Singleton getInstance() { return instance; }
    //endregion
    //region properties
    public final String CWD;
    public final String WEB_ROOT;
 
    private Device device;
    private ServerSocket server;
    private WatchService watchService;

    private String[] fileNames;
    private String[] filePaths;
    private int fileCount;
    private String[] extensions;
    private String[] contentTypes;
    
    public static final String NEWLINE = DefaultSystemPropertyStrings.LINE_SEPARATOR;
    public final ScriptEngine scriptEngine;
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
