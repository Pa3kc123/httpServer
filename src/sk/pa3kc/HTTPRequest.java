package sk.pa3kc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sk.pa3kc.mylibrary.Universal;

import static sk.pa3kc.Program.NEWLINE;

public class HTTPRequest
{
    private List<String> properties = new ArrayList<String>();
    private List<String> values = new ArrayList<String>();

    private final String method;
    private final String path;
    private final String protocol;

    private char[] buffer;
    private int index = 0;

    public HTTPRequest(InputStream is)
    {
        {
            List<Integer> list = new ArrayList<Integer>();
            try
            {
                int lastC = -1;
                for (int c = is.read(); c != -1; c = is.read())
                {
                    if (lastC == '\n' && c == '\r') break;
                    list.add(c);
                    lastC = c;
                }

                Integer[] arr = list.toArray(new Integer[0]);
                buffer = new char[arr.length];
                for (int i = 0; i < arr.length; i++)
                    buffer[i] = (char)arr[i].intValue();
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }
        }

        this.method = getString(' ');
        this.path = getString(' ');
        this.protocol = getString(' ');

        /*InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);

        {
            String[] args = null;
            try
            {
                args = reader.readLine().split(" ");
            }
            catch (Throwable ex)
            {
                ex.printStackTrace();
            }

            if (args == null)
            {
                this.method = null;
                this.path = null;
                this.protocol = null;
            }
            else
            {
                this.method = args[0];
                this.path = args[1];
                this.protocol = args[2];
            }
        }

        try
        {
            for (String line = reader.readLine(); line != null; line = reader.readLine())
            {
                String[] args = line.split(": ");
                if (args.length == 2)
                {
                    this.properties.add(args[0]);
                    this.values.add(args[1]);
                }
            }
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }

        Universal.closeStreams(reader, streamReader);*/
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getProtocol() { return protocol; }

    private String getString(char until)
    {
        StringBuilder builder = new StringBuilder();
        while (this.buffer[this.index] != until || this.buffer[this.index] != '\r')
            builder.append(this.buffer[this.index++]);
        this.index++;
        return builder.toString();
    }

    static class HTTPRequestMethod
    {
        public static final String GET = "GET";
        public static final String POST = "POST";
    }
}
