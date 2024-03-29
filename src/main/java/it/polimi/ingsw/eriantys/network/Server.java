package it.polimi.ingsw.eriantys.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import static it.polimi.ingsw.eriantys.loggers.Loggers.serverLogger;

/**
 * A wrapper around {@link ServerSocket java.net.ServerSocket} for accepting incoming clients.
 */
public class Server implements Runnable {
  public static final int DEFAULT_PORT = 1234;

  /**
   * The number of errors that can occur before the server is restarted.
   */
  private static final int ACCEPT_RESTART_THRESHOLD = 5;

  int port;
  private ServerSocket serverSocket;
  private final BlockingQueue<MessageQueueEntry> messageQueue;

  /**
   * Creates a new server.
   *
   * @param port         The server port
   * @param messageQueue The shared queue where received messages will be added
   */
  public Server(int port, BlockingQueue<MessageQueueEntry> messageQueue) {
    this.port = port;
    this.messageQueue = messageQueue;
  }

  /**
   * Initializes the server socket.
   */
  public void init() throws IOException {
    if (serverSocket != null) {
      try {
        serverSocket.close();
      } catch (IOException ignored) {
      }
    }
    serverSocket = new ServerSocket(port);
    serverLogger.info("Server socket up on '{}'", serverSocket.getLocalSocketAddress());
  }

  /**
   * Accepts a new client connection.
   *
   * @return The accepted client
   */
  public Client accept() throws IOException {
    serverLogger.debug("Listening for incoming connections");
    Socket clientSocket = serverSocket.accept();
    serverLogger.debug("Accepted incoming client: {}", clientSocket.getRemoteSocketAddress());
    return new Client(clientSocket, messageQueue);
  }

  /**
   * Runs the server accept loop.
   * Accepts a new client and creates a new listener thread for it.
   * <p>
   * This method is supposed to be run on its own thread.
   */
  @Override
  public void run() {
    serverLogger.debug("Starting thread '{}'", Thread.currentThread().getName());
    int threadSeqNumber = 1;
    int errorCount = 0;
    while (true) {
      try {
        Client newClient = accept();
        // Reset the error count after every successful accept
        errorCount = 0;
        // Launch the network listening thread for the new client
        Thread socketThread = new Thread(newClient, "sock-" + threadSeqNumber++);
        socketThread.setDaemon(true);
        socketThread.start();
      } catch (IOException e) {
        serverLogger.error("An error occurred while accepting:", e);
        if (++errorCount > ACCEPT_RESTART_THRESHOLD) {
          try {
            init();
            serverLogger.warn("The server encountered a fatal error and was restarted");
          } catch (IOException ex) {
            serverLogger.error("The server encountered a fatal error and could not be restarted:", ex);
            break;
          }
        }
      }
    }
    serverLogger.debug("Stopping thread '{}'", Thread.currentThread().getName());
  }
}
