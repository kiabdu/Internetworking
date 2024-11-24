package cp;

import core.Msg;
import exceptions.IllegalMsgException;

class CPCookieRequestMsg extends CPMsg {
    protected static final String CP_CREQ_HEADER = "cookie_request";

    /*
     * Create cookie request message.
     * The cp header is prepended in the super-class.
     */
    @Override
    protected void create(String data) {
        // prepend reg header
        data = CP_CREQ_HEADER;
        // super class prepends slp header
        super.create(data);
    }

    @Override
    protected Msg parse(String sentence) throws IllegalMsgException {
        if (!sentence.startsWith(CP_CREQ_HEADER)) {
            throw new IllegalMsgException();
        }
        return this;
    }
}