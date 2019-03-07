package sk.pa3kc;

import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;

import sk.pa3kc.mylibrary.util.StreamUtils;

public class Logger
{
    private Logger() {}

    public static void log(String message)
    {
        OutputStream os2 = null;
        ByteArrayInputStream bais = null;
        try
        {
            os2 = new FileOutputStream(new File("output.txt"), true);
            bais = new ByteArrayInputStream(message.getBytes("UTF-8"));
            byte[] buffer = new byte[1024];

            for (int checksum = bais.read(buffer); checksum != -1; checksum = bais.read(buffer))
                os2.write(buffer);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            StreamUtils.closeStreams(os2, bais);
        }
    }
}