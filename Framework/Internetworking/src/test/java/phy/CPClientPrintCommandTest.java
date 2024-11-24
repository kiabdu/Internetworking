package phy;

import core.Protocol;
import cp.CPProtocol;
import exceptions.CookieTimeoutException;
import exceptions.IWProtocolException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CPClientPrintCommandTest {
    String serverName = "localhost";
    int serverPort = 3027;

    @Mock
    PhyProtocol phyProtocolMock;
    PhyConfiguration phyConfig;
    PhyMsg testMsg;
    PhyMsg corruptMsg;
    CPProtocol cProtocol;

    @BeforeEach
    void setup() throws UnknownHostException {
        try {
            phyConfig = new PhyConfiguration(InetAddress.getByName(serverName), serverPort, Protocol.proto_id.CP);
            testMsg = new PhyMsg(phyConfig);
            corruptMsg = new PhyMsg(phyConfig);
        } catch (UnknownHostException e) {
            fail();
        }
        // Set up the object-under-test
        cProtocol = new CPProtocol(InetAddress.getByName(serverName), serverPort, phyProtocolMock);
    }

    @Test
    void testCommandResponseOk() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 ok 0 2368828647");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(1)).receive(2000);
    }

    @Test
    void testCommandResponseError() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 error 0 4233987072");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);

        // Run the test
        assertThrows(CookieTimeoutException.class,
                ()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(1)).receive(2000);
    }

    @Test
    void testCommandResponseErrorWithMessage() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 error 16 Out of Resources 367825814");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(testMsg);

        // Run the test
        assertThrows(CookieTimeoutException.class,
                ()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(1)).receive(2000);
    }

    @Test
    void testCommandResponseOkWithMissingField() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 ok 0 2368828647");
        corruptMsg = (PhyMsg)corruptMsg.parse("phy 7 cp command_response 0 4003252835");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(2)).receive(2000);
    }

    @Test
    void testCommandResponseOkWithIllegalField() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 ok 0 2368828647");
        corruptMsg = (PhyMsg)corruptMsg.parse("phy 7 cp command_response 1 ok Null 4003252835");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(2)).receive(2000);
    }

    @Test
    void testCommandResponseOkWithIllegalChecksum() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 ok 0 2368828647");
        corruptMsg = (PhyMsg)corruptMsg.parse("phy 7 cp command_response 1 ok 0 400325283");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(2)).receive(2000);
    }

    @Test
    void testCommandResponseOkWithTooLongMessage() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 ok 0 2368828647");
        corruptMsg = (PhyMsg)corruptMsg.parse("phy 7 cp command_response 1 ok 0 Hello 4003252835");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(2)).receive(2000);
    }

    @Test
    void testCommandResponseOkWithTooShortMessage() throws IWProtocolException, IOException {
        // Fill the message object that is going to be returned to the object-under-test
        // with the message needed for this test case
        testMsg = (PhyMsg)testMsg.parse("phy 7 cp command_response 0 ok 0 2368828647");
        corruptMsg = (PhyMsg)corruptMsg.parse("phy 7 cp command_response 1 ok 10 Hello 4003252835");

        // Implement behavior of the mocked object
        when(phyProtocolMock.receive(anyInt())).thenReturn(corruptMsg).thenReturn(testMsg);

        // Run the test
        assertDoesNotThrow(()->cProtocol.receive());

        // verify a specified behavior
        verify(phyProtocolMock, times(2)).receive(2000);
    }
}
