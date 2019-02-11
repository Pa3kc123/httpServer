package sk.pa3kc;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sk.pa3kc.httpconstants.HTTPResponseCodes;
import sk.pa3kc.mylibrary.Universal;

public class HTTPResponse
{
    private static final String NEWLINE = "\r\n";

    private final PrintWriter writer;

    private String protocol = "HTTP/1.1";
    private String responseCode = HTTPResponseCodes.Not_Found_404;
    private List<String> propertyNames = new ArrayList<String>();
    private List<String> propertyValues = new ArrayList<String>();
    private int propertyCount = 0;
    private String body = "<p>No body specified</p>";

    public HTTPResponse(OutputStream os)
    {
        this.writer = new PrintWriter(os);
    }

    public void setProtocol(String protocol) { this.protocol = protocol; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }

    public void setProperty(String property, String... args)
    {
        StringBuilder builder = new StringBuilder();
        for (String arg : args)
            builder.append(arg + " ");
        builder.append(NEWLINE);

        int index = this.propertyNames.indexOf(property);
        if (index == -1)
        {
            this.propertyNames.add(property);
            this.propertyValues.add(builder.toString());
        }
        else this.propertyValues.set(index, builder.toString());
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public void writeToOutput()
    {
        writer.print(this.protocol + this.responseCode + NEWLINE);
        for (int i = 0; i < this.propertyCount; i++)
            writer.print(this.propertyNames.get(i) + ": " + this.propertyValues.get(i) + NEWLINE);
        writer.print(NEWLINE);
        writer.print(this.body + NEWLINE);
        writer.flush();
    }

    @Override
    protected void finalize() throws Throwable
    {
        Universal.closeStreams(writer);
        super.finalize();
    }
}
