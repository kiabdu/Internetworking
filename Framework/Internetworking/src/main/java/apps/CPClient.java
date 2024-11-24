package apps;

import cp.*;
import exceptions.*;
import phy.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

public class CPClient {
    private static final String SERVER_NAME = "localhost";


    public static void main(String[] args) {
        // Each client needs to start on a unique UDP port provided by the user
        if (args.length != 1) {
            System.out.println("Provide an address identifier (int) from range [5000:65534]");
            return;
        }
        var id = Integer.parseInt(args[0]);
        if (id < 5000 || id > 65534) {
            System.out.println("Invalid address identifier! Range [5000:65534]");
            return;
        }

        // Set up the virtual link protocol
        PhyProtocol phy = new PhyProtocol(id);

        // Set up command protocol
        CPProtocol cp;
        try {
            cp = new CPProtocol(InetAddress.getByName(SERVER_NAME), CPCommandServer.COMMAND_SERVER_PORT, phy);
            cp.setCookieServer(InetAddress.getByName(SERVER_NAME), CPCookieServer.COOKIE_SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Read data from user to send to server
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                String sentence;
                System.out.println("Command: ");
                sentence = inFromUser.readLine();
                // Currently only these two commands are supported by the specification
                if(!(sentence.equals("status") || sentence.startsWith("print"))) {
                    System.out.println("Only these two commands are supported: status, print \"text\"");
                    continue;
                }

                cp.send(sentence, null);
                System.out.println("Command sent to server ... waiting for response");
                String answer = cp.receive().getData();
                System.out.println(answer);
            } catch (IWProtocolException | IOException e) {
                System.out.println("Command not executed ... something appears to be wrong");
            }
        }
    }
}
