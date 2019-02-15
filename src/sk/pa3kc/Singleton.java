package sk.pa3kc;

import java.io.File;
import java.net.ServerSocket;

import sk.pa3kc.mylibrary.DefaultSystemPropertyStrings;
import sk.pa3kc.mylibrary.myregex.MyRegex;
import sk.pa3kc.mylibrary.net.Device;

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
    }
    public static final Singleton getInstance() { return instance; }
    //endregion
    //region properties
    private final String CWD;
    private final String WEB_ROOT;
 
    private Device device;
    private ServerSocket server;

    private String[] fileNames;
    private String[] filePaths;
    private int fileCount;
    
    public static final String NEWLINE = DefaultSystemPropertyStrings.LINE_SEPARATOR;
    //endregion
    //region Getters
    public Device getDevice() { return this.device; }
    public ServerSocket getServer() { return this.server; }
    public String getCWD() { return this.CWD; }
    public String getWEB_ROOT() { return this.WEB_ROOT; }
    public int getFileCount() { return this.fileCount; }
    public String[] getFileNames() { return this.fileNames; }
    public String[] getFilePaths() { return this.filePaths; }
    //endregion
    //region Setters
    public void setDevice(Device value) { this.device = value; }
    public void setServer(ServerSocket value) { this.server = value; }
    public void setFileCount(int value) { this.fileCount = value; }
    public void setFileNames(String[] value) { this.fileNames = value; }
    public void setFilePaths(String[] value) { this.filePaths = value; }
    //endregion
}
