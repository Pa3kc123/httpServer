package sk.pa3kc;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;

import static sk.pa3kc.Singleton.NEWLINE;

public class Program
{
    public static void init()
    {
        System.out.print("CWD = " + Singleton.getInstance().CWD + NEWLINE);
        System.out.print("WEB_ROOT = " + Singleton.getInstance().WEB_ROOT + NEWLINE);
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
