package sk.pa3kc.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sk.pa3kc.http.constants.HTTPResponseCodes;
import sk.pa3kc.mylibrary.util.StreamUtils;

public class HTTPResponse
{
    private static final String NEWLINE = "\r\n";

    private final OutputStream output;
    private final PrintWriter writer;

    private String protocol = "HTTP/1.1";
    private String responseCode = HTTPResponseCodes.Not_Found_404;
    private List<String> propertyNames = new ArrayList<String>();
    private List<String> propertyValues = new ArrayList<String>();
    private int propertyCount = 0;
    private List<String> scripts = new ArrayList<String>();
    private String body = "<p>No body specified</p>";

    public HTTPResponse(OutputStream os)
    {
        this.output = os;
        this.writer = new PrintWriter(os, false);
    }

    public void addScript(String... pathToJSFile)
    {
        this.scripts.addAll(Arrays.asList(pathToJSFile));
    }

    public void setProtocol(String protocol) { this.protocol = protocol; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    public void setProperty(String property, String arg)
    {
        int index = this.propertyNames.indexOf(property);
        if (index == -1)
        {
            this.propertyCount++;
            this.propertyNames.add(property);
            this.propertyValues.add(arg);
        }
        else this.propertyValues.set(index, arg);
    }
    public void setBody(String body)
    {
        this.body = body;
    }

    //region Public methods
    public void writeHeaderToOutput()
    {
        writer.print(this.protocol + " " + this.responseCode + NEWLINE);
        for (int i = 0; i < this.propertyCount; i++)
            writer.print(this.propertyNames.get(i) + ": " + this.propertyValues.get(i) + NEWLINE);
        writer.print(NEWLINE);
        writer.flush();
    }

    public void writeBodyToOutput()
    {
        writer.print(this.body + NEWLINE);
        writer.flush();
    }

    public void writeToOutput()
    {
        writeHeaderToOutput();
        writeBodyToOutput();
    }

    public void writeBinaryFileToOutput(File file)
    {
        FileInputStream fis = null;
        DataOutputStream dos = null;

        try
        {
            fis = new FileInputStream(file);
            dos = new DataOutputStream(this.output);

            byte[] buffer = new byte[512];
            for (int checksum = fis.read(buffer, 0, buffer.length); checksum != -1; checksum = fis.read(buffer, 0, buffer.length))
            {
                dos.write((Integer.toHexString(checksum) + NEWLINE).getBytes());
                dos.write(buffer, 0, checksum);
                dos.write(NEWLINE.getBytes());
            }
            //* To end the stream its needed to send '0\r\n\r\n'
            dos.write((Integer.toHexString(0) + NEWLINE).getBytes());
            dos.flush();
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            StreamUtils.closeStreams(dos, fis);
        }

        writer.print(NEWLINE);
        writer.flush();
    }
    //endregion

    @Override
    protected void finalize() throws Throwable
    {
        StreamUtils.closeStreams(writer);
        super.finalize();
    }
}
