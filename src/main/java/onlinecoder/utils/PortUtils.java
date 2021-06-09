package onlinecoder.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtils {
  public static int findRandomAvailablePort() throws IOException {
    try (ServerSocket socket = new ServerSocket(0)) {
      return socket.getLocalPort();
    }
  }
}
