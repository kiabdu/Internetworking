package cp;

import core.Msg;
import exceptions.IllegalMsgException;

class CPCookieResponseMsg extends CPMsg {
    protected static final String CP_CRES_HEADER = "cookie_response";
    private boolean success;
    private int cookie;

    protected CPCookieResponseMsg() {

    }
    protected CPCookieResponseMsg(boolean s) {
        this.success = s;
    }

    protected boolean getSuccess() {return this.success;}

    protected int getCookie() {return this.cookie;}

    /*
     * Create cookie request message.
     * The cp header is prepended in the super-class.
     */
    @Override
    protected void create(String data) {
        if (this.success) {
            // prepend cres header
            data = CP_CRES_HEADER + " ACK " + data;
        } else {
            data = CP_CRES_HEADER + " NAK " + data;
        }
        // super class prepends slp header
        super.create(data);
    }

    protected Msg parse(String sentence) throws IllegalMsgException {
        if (!sentence.startsWith(CP_CRES_HEADER)) {
            throw new IllegalMsgException();
        }
        String[] parts = sentence.split("\\s+", 3);
        if(parts.length != 3)
            throw new IllegalMsgException();

        this.success = parts[1].equals("ACK");

        if (success) {
            try {
                Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                throw new IllegalMsgException();
            }
            this.cookie = Integer.parseInt(parts[2]);
        } else {
            this.data = parts[2];
        }
        return this;
    }

}
