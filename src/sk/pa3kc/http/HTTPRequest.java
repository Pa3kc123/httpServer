package sk.pa3kc.http;

import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sk.pa3kc.mylibrary.util.StreamUtils;

import static sk.pa3kc.Singleton.NEWLINE;

public class HTTPRequest
{
    private String[] propertyNames;
    private String[] propertyValues;
    private int propertyCount;

    private final String method;
    private final String path;
    private final String protocol;

    private String[] buffer;

    public HTTPRequest(InputStream is)
    {
        try
        {
            int lastC = -1;
            StringBuilder builder = new StringBuilder();
            List<String> list = new ArrayList<String>();

            for (int c = is.read(); c != -1; c = is.read())
            {
                if (lastC == '\n' && c == '\r') break;
                if (lastC == '\r' && c == '\n')
                {
                    list.add(builder.toString());
                    builder.delete(0, builder.length());
                }
                else builder.append((char)c);
                lastC = c;
            }

            this.buffer = list.toArray(new String[0]);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }

        if (this.buffer != null && this.buffer.length != 0)
        {
            String[] args = this.buffer[0].split(" ");
            this.method = args[0];
            this.path = args[1];
            this.protocol = args[2];
        }
        else
        {
            this.method = null;
            this.path = null;
            this.protocol = null;
        }

        {
            List<String> propertyNames = new ArrayList<String>();
            List<String> propertyValues = new ArrayList<String>();

            for (int i = 1; i < this.buffer.length; i++)
            {
                if (this.buffer[i] == null) continue;
    
                String[] args = this.buffer[i].split(": ", 2);
                if (args.length == 2)
                {
                    propertyNames.add(args[0]);
                    propertyValues.add(args[1]);
                }
            }

            this.propertyNames = propertyNames.toArray(new String[0]);
            this.propertyValues = propertyValues.toArray(new String[0]);
            this.propertyCount = this.propertyNames.length;
        }

        FileWriter writer = null;
        try
        {
            writer = new FileWriter(new java.io.File("output.log"), true);
            for (String line : this.buffer)
                writer.append(line);
            writer.append("-------------------------" + NEWLINE);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            StreamUtils.closeStreams(writer);
        }
    }

    public String getMethod() { return this.method; }
    public String getPath() { return this.path; }
    public String getProtocol() { return this.protocol; }

    public int getPropertyCount() { return this.propertyCount; }
    public String[] getPropertyNames() { return this.propertyNames; }
    public String[] getPropertyValues() { return this.propertyValues; }

    static class HTTPRequestMethod
    {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }
}
