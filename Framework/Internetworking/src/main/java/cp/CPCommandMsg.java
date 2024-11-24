package cp;

import exceptions.IllegalCommandException;
import exceptions.IllegalMsgException;

import java.util.zip.CRC32;

public class CPCommandMsg extends CPMsg {
    protected static final String CP_HEADER = "cp";
    protected static final String CP_COMMAND_HEADER = "command";
    protected static final String COMMAND_RESPONSE_HEADER = "command_response";
    protected static final String CP_COMMAND_RESPONSE_HEADER = "cp command_response";
    protected int commandId = 0;
    protected int cookie;
    protected CRC32 checksum;
    private CommandType commandType;

    public CPCommandMsg(int cookie, int commandId){
        this.commandId = commandId;
        this.cookie = cookie;
    }

    // task 3: implement a message class to create command messages
    protected void create(String cmdString) {

        // (?s)=DOTALL, \\s+=0 oder mehr Leerzeichen, .*=matched alle Chars
        if (!cmdString.matches("(?s)status\\s+|print\\s+.*")) {
            throw new IllegalArgumentException("Command not supported");
        }

        String[] cmdParts = cmdString.split("\\s+");
        String command = cmdParts[0];
        boolean isPrintCommand = command.equals("print");
        String message = isPrintCommand ? cmdParts[1] : "";

        commandType = isPrintCommand ? CommandType.PRINT : CommandType.STATUS;
        String commandMessage;

        // Format laut 3.3.: cp⟨WS⟩command⟨WS⟩⟨id⟩⟨WS⟩⟨cookie⟩⟨WS⟩⟨length⟩⟨WS⟩⟨command⟩[⟨WS⟩⟨message⟩]⟨WS⟩⟨checksum⟩
        commandMessage = CP_HEADER + " " + CP_COMMAND_HEADER + " " + this.commandId + " " + this.cookie + " " + command + " " + message.length() + message;

        this.checksum = calculateChecksum(commandMessage);

        super.create(commandMessage + " " + checksum.getValue());
    }


    // task 3: implement a message class to create command messages
    public CPMsg parse(String response) throws IllegalCommandException, IllegalMsgException {

        if (!response.startsWith(CP_COMMAND_RESPONSE_HEADER)) {
            throw new IllegalCommandException();
        }

        // cp = 0; command_response = 1; id = 2; success = 3; length = 4; [message = 5]; checksum = 5[oder 6]
        String[] responseParts = response.split("\\s+");
        Long responseChecksum = Long.parseLong(responseParts[responseParts.length - 1]);

        if (!responseChecksum.equals(this.checksum.getValue())) {
            throw new IllegalCommandException();
        }

        CPMsg parsedCommandResponseMsg = new CPMsg();
        String commandResponseMsg;
        boolean isCommandTypeStatus = this.commandType == CommandType.STATUS;


        // id: The server uses the same id the client used in the command message
        // to match response message to command message. The maximum id is 65535.
        int responseId = Integer.parseInt(responseParts[2]);

        // success: This can be either “ok” or “error”.
        String responseSuccess = responseParts[3];
        if (!responseSuccess.matches("ok|error")) {
            throw new IllegalCommandException();
        }

        // length: The length of the message field. The length is 0 if no message is included.
        int length = Integer.parseInt(responseParts[4]);

        /* message:
         *
         * The message depends on which command the client has issued. For a print
         * command the message field is omitted. For a status command the message field con-
         * tains at least two lines in JSON formatting for the number of successfully processed
         * command messages and the time-to-live value of the current cookie.
         *
         */
        String message = isCommandTypeStatus ? responseParts[5] : "";

        // Format laut 3.4.: cp⟨WS⟩command_response⟨WS⟩⟨id⟩⟨WS⟩⟨success⟩⟨WS⟩⟨length⟩[⟨WS⟩⟨message⟩]⟨WS⟩⟨checksum⟩
        commandResponseMsg = COMMAND_RESPONSE_HEADER + responseId + responseSuccess + length + message + responseChecksum;
        parsedCommandResponseMsg.create(commandResponseMsg);
        return parsedCommandResponseMsg;
    }

    protected CRC32 calculateChecksum(String data) {
        CRC32 checksum = new CRC32();

        // checksum für gesamten String außer "CP" berechnen
        // also alles ab index 2
        checksum.update(data.substring(CP_HEADER.length()).getBytes());

        return checksum;
    }

    public int getCommandId() {
        return commandId;
    }
}

enum CommandType {
    STATUS,
    PRINT
}