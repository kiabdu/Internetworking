package phy;

import core.Protocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhyMsgTest {
    @Test
    @DisplayName("App message creation test")
    void createAppMsgTest() {
        PhyConfiguration config;
        PhyMsg msg;
        try {
            config = new PhyConfiguration(InetAddress.getByName("localhost"), 1000, Protocol.proto_id.APP);
            msg = new PhyMsg(config);
            msg.create("Hello World");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }

        assertEquals("phy 3 Hello World", new String(msg.getDataBytes()));
    }
}
