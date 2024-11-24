package phy;

import core.Protocol;
import cp.CPProtocol;
import exceptions.CookieRequestException;
import exceptions.IWProtocolException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CPClientCookieRequestTest {
    String serverName = "localhost";
    int commandServerPort = 3027;
    int cookieServerPort = 2999;

    @Mock
    PhyProtocol phyProtocolMock;
    PhyMsg testMsg;
    PhyMsg corruptedMsg;
    CPProtocol cProtocol;

    @BeforeEach
    void setup() throws UnknownHostException {
        // Create additionally needed objects for every test
        PhyConfiguration phyConfig;
        try {
            phyConfig = new PhyConfiguration(InetAddress.getByName(serverName), commandServerPort, Protocol.proto_id.CP);
            testMsg = new PhyMsg(phyConfig);
            corruptedMsg = new PhyMsg(phyConfig);
        } catch (UnknownHostException e) {
            fail();
        }
        // Set up the object-under-test
        cProtocol = new CPProtocol(InetAddress.getByName(serverName), commandServerPort, phyProtocolMock);
        cProtocol.setCookieServer(InetAddress.getByName(serverName), cookieServerPort);
    }

    @Test
    void testCookieRequestSuccessful() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp cookie_response ACK 12345");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.requestCookie());

        // verify a specified behavior
        verify(phyProtocolMock, times(1)).receive(2000);
        verify(phyProtocolMock).send(eq("cp cookie_request"), any(PhyConfiguration.class));
        verify(phyProtocolMock, times(1)).send(anyString(), any(PhyConfiguration.class));
    }

    @Test
    void testNoCookie() throws IOException, IWProtocolException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg) testMsg.parse("phy 7 cp cookie_response NAK no resources");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);

        // Run the test
        assertThrows(CookieRequestException.class,
                ()->cProtocol.requestCookie());

        verify(phyProtocolMock, times(1)).receive(2000);
        verify(phyProtocolMock, times(1)).send(eq("cp cookie_request"), any(PhyConfiguration.class));
    }

    @Test
    void testIllegalPhyMsg() throws IOException, IWProtocolException {

        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case (also create corresponding configuration object)
        PhyConfiguration corruptedPhyConfig = new PhyConfiguration(InetAddress.getByName(serverName), commandServerPort, Protocol.proto_id.SLP);
        PhyMsg corruptedPhyMsg = new PhyMsg(corruptedPhyConfig);
        corruptedPhyMsg = (PhyMsg) corruptedPhyMsg.parse("phy 5 cp cookie_response ACK 12345");
        testMsg = (PhyMsg) testMsg.parse("phy 7 cp cookie_response ACK 12345");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptedPhyMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.requestCookie());

        verify(phyProtocolMock, times(2)).receive(2000);
        verify(phyProtocolMock, times(2)).send(eq("cp cookie_request"), any(PhyConfiguration.class));
    }

    @Test
    void testMalformedCPMsg() throws IOException, IWProtocolException {

        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case (also create corresponding configuration object)
        corruptedMsg = (PhyMsg) corruptedMsg.parse("phy 7 cp cookie_response ACK abc");
        testMsg = (PhyMsg) testMsg.parse("phy 7 cp cookie_response ACK 12345");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptedMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.requestCookie());

        verify(phyProtocolMock, times(2)).receive(2000);
        verify(phyProtocolMock, times(2)).send(eq("cp cookie_request"), any(PhyConfiguration.class));
    }

    @Test
    void testIncompleteCPMsg() throws IOException, IWProtocolException {

        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case (also create corresponding configuration object)
        corruptedMsg = (PhyMsg) corruptedMsg.parse("phy 7 cp cookie_response ACK");
        testMsg = (PhyMsg) testMsg.parse("phy 7 cp cookie_response ACK 12345");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptedMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.requestCookie());

        verify(phyProtocolMock, times(2)).receive(anyInt());
        verify(phyProtocolMock, times(2)).send(eq("cp cookie_request"), any(PhyConfiguration.class));
    }

    @Test
    void testMessageLoss() throws IOException, IWProtocolException {

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenThrow(new SocketTimeoutException());

        // Run the test
        assertThrows(CookieRequestException.class,
                ()->cProtocol.requestCookie());
        verify(phyProtocolMock, times(3)).receive(2000);
        verify(phyProtocolMock, times(3)).send(eq("cp cookie_request"), any(PhyConfiguration.class));
    }

}
