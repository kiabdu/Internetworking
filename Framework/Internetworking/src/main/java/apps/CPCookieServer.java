package apps;

import cp.CPProtocol;
import exceptions.IWProtocolException;
import phy.PhyProtocol;

import java.io.IOException;

public class CPCookieServer {
    protected static final int COOKIE_SERVER_PORT = 3000;

    public static void main(String[] args) {
        // Set up the virtual link protocol
        PhyProtocol phy = new PhyProtocol(COOKIE_SERVER_PORT);

        // Set up command protocol
        CPProtocol cp;
        try {
            cp = new CPProtocol(phy, true);
        } catch (Exception e) {
            return;
        }

        // Start server processing
        while (true) {
            try {
                // Will never return from this call
                cp.receive();
            } catch (IOException e) {
                System.out.println("IO error");
                return;
            } catch (IWProtocolException e) {
                System.out.println("This should never happen.");
            }
        }
    }
}