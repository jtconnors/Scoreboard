
package scoreboard.swing.networking;

import static scoreboard.common.Constants.DEBUG_STATUS;
import static scoreboard.common.Constants.DEBUG_NONE;
import static scoreboard.common.Constants.DEFAULT_SESSION_ADDR;
import static scoreboard.common.Constants.DEFAULT_PORT;

public class MulticastWriter {

    private MulticastConnection multicastConnection;
    private boolean open = false;

    class MulticastConnectionListener implements SocketListener {
        @Override
        public void onMessage(String line) {
            /*
             * Messages sent out on a MulticastSocket are also received.
             * We do nothing here because this is just a reader.
             */
        }

        @Override
        public void onClosedStatus(boolean isClosed) {
            if (multicastConnection.debugFlagIsSet(DEBUG_STATUS)) {
                System.out.println("multicast isClosed = " + isClosed);
            }
            open = !isClosed;
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void sendMessage(String msg) {
        multicastConnection.sendMessage(msg);
    }

    public MulticastWriter() {
        this(DEFAULT_SESSION_ADDR, DEFAULT_PORT, DEBUG_NONE);
    }

    public MulticastWriter(String address, int port, int debugFlags) {
        multicastConnection = new MulticastConnection(
                new MulticastConnectionListener(), address, port, debugFlags);
    }
}
