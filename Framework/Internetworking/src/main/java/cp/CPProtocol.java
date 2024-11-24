package cp;


import core.*;
import exceptions.*;
import org.junit.platform.commons.util.StringUtils;
import phy.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.zip.CRC32;

public class CPProtocol extends Protocol {
    private static final int CP_TIMEOUT = 2000;
    private static final int CP_HASHMAP_SIZE = 20;
    private int cookie;
    private int id;
    private PhyConfiguration PhyConfigCommandServer;
    private PhyConfiguration PhyConfigCookieServer;
    private final PhyProtocol PhyProto;
    private final cp_role role;
    HashMap<PhyConfiguration, Cookie> cookieMap;
    Random rnd;

    private enum cp_role {
        CLIENT, COOKIE, COMMAND
    }

    // Constructor for clients
    public CPProtocol(InetAddress rname, int rp, PhyProtocol phyP) throws UnknownHostException {
        this.PhyConfigCommandServer = new PhyConfiguration(rname, rp, proto_id.CP);
        this.PhyProto = phyP;
        this.role = cp_role.CLIENT;
        this.cookie = -1;
    }
    // Constructor for servers
    public CPProtocol(PhyProtocol phyP, boolean isCookieServer) {
        this.PhyProto = phyP;
        if (isCookieServer) {
            this.role = cp_role.COOKIE;
            this.cookieMap = new HashMap<>();
            this.rnd = new Random();
        } else {
            this.role = cp_role.COMMAND;
        }
    }

    public void setCookieServer(InetAddress rname, int rp) throws UnknownHostException {
        this.PhyConfigCookieServer = new PhyConfiguration(rname, rp, proto_id.CP);
    }

    private static int commandId = 0;
    private static List<Integer> existingCommandIds = new ArrayList<>();
    private int createCommandId(){
        if(commandId < 65535){
            commandId++;
        }

        if(existingCommandIds.contains(commandId)){
            while(existingCommandIds.contains(commandId)){
                commandId++;
            }
        }

        this.id = commandId;
        existingCommandIds.add(this.id);

        return this.id;
    }


    @Override
    public void send(String s, Configuration config) throws IOException, IWProtocolException {

        if (cookie < 0) {
            // Request a new cookie from server
            // Either updates the cookie attribute or returns with an exception
            requestCookie();
        }

        // Task 1.2.1: complete send method
        // 1a: legal command
        if(StringUtils.isBlank(s)){
            throw new IllegalMsgException();
        }

        // 1b: create cmd msg obj
        int id = createCommandId();
        CPCommandMsg commandMsg = new CPCommandMsg(this.cookie, id);
        commandMsg.create(s);
        this.PhyProto.send(Arrays.toString(commandMsg.getDataBytes()), this.PhyConfigCommandServer);
    }

    @Override
    public Msg receive() throws IOException, IWProtocolException {
        // Task 1.2.1: implement receive method
        // 2a: For any command message sent, the client waits at most three seconds
        Msg in = this.PhyProto.receive(3000);
        Msg responseMsg = new CPCommandMsg(this.cookie, this.id);

        // 2b: if the parser throws an exception, the message shall be silently discarded.
        try {
            responseMsg = ((CPCommandMsg) responseMsg).parse(in.getData());
        } catch (Exception e){
            return null;
        }

        String[] responseParts = responseMsg.getData().split("\\s+");
        // Check that the response matches the command message by comparing the message id of the received message with id of the sent message
        assert this.id == Integer.parseInt(responseParts[2]);

        // d. Check if the command server has accepted the command.
        assert responseParts[3].matches("ok");

        // Task 2.1.1: enhance receive method

        // e. Return to the client appropriately (wtf does this even mean?)
        return responseMsg;
    }


    // Method for the client to request a cookie
    public void requestCookie() throws IOException, IWProtocolException {
        CPCookieRequestMsg reqMsg = new CPCookieRequestMsg();
        reqMsg.create(null);
        Msg resMsg = new CPMsg();

        boolean waitForResp = true;
        int count = 0;
        while(waitForResp && count < 3) {
            this.PhyProto.send(new String(reqMsg.getDataBytes()), this.PhyConfigCookieServer);

            try {
                Msg in = this.PhyProto.receive(CP_TIMEOUT);
                if (((PhyConfiguration) in.getConfiguration()).getPid() != proto_id.CP)
                    continue;
                resMsg = ((CPMsg) resMsg).parse(in.getData());
                if(resMsg instanceof CPCookieResponseMsg)
                    waitForResp = false;
            } catch (SocketTimeoutException e) {
                count += 1;
            } catch (IWProtocolException ignored) {
            }
        }

        if(count == 3)
            throw new CookieRequestException();
        if(resMsg instanceof CPCookieResponseMsg && !((CPCookieResponseMsg) resMsg).getSuccess()) {
            throw new CookieRequestException();
        }
         assert resMsg instanceof CPCookieResponseMsg;
         this.cookie = ((CPCookieResponseMsg)resMsg).getCookie();
    }
}

class Cookie {
    private final long timeOfCreation;
    private final int cookieValue;

    public Cookie(long toc, int c) {
        this.timeOfCreation = toc;
        this.cookieValue = c;
    }

    public long getTimeOfCreation() {
        return timeOfCreation;
    }

    public int getCookieValue() { return cookieValue;}
}

