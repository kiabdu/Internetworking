package cp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CPCookieResponseTest {
    @Test
    @DisplayName("Cookie ACK response message creation test")
    void createACKTest() {
        Random rnd = new Random();
        int cv = Math.abs(rnd.nextInt());
        CPCookieResponseMsg resp = new CPCookieResponseMsg(true);
        resp.create(String.valueOf(cv));
        assertEquals("cp cookie_response ACK " + cv, new String(resp.getDataBytes()));
    }

    @Test
    @DisplayName("Cookie NAK response message creation test")
    void createNAKTest() {
        CPCookieResponseMsg resp = new CPCookieResponseMsg(false);
        resp.create("No cookie available");
        assertEquals("cp cookie_response NAK No cookie available", new String(resp.getDataBytes()));
    }
}
