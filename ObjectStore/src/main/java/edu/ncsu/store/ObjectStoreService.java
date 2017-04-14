package edu.ncsu.store;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

import edu.ncsu.chord.ChordDriver;
import edu.ncsu.chord.ChordSession;

/**
 * Created by amit on 1/4/17.
 */
class ObjectStoreService {

  /* Reference to underlying chord node - only if client wants to join as a node */
  private static ChordSession chordSession = null;

  /* Reference to ObjectStore */
  private static ObjectStore store = null;

  static ChordSession getChordSession() {
    return chordSession;
  }

  static ObjectStore getStore() {
    return store;
  }

  private static void initRMI() {
    try {
    /* Set custom SocketFactories for handling RMI timeout */
      RMISocketFactory.setSocketFactory(new RMISocketFactory() {
        public Socket createSocket(String host, int port) throws IOException {
          int timeoutMillis = 10000; /* RMI call waits for 10 seconds */
          Socket socket = new Socket();
          socket.setSoTimeout(timeoutMillis);
          socket.setSoLinger(false, 0);
          socket.connect(new InetSocketAddress(host, port), timeoutMillis);
          return socket;
        }

        public ServerSocket createServerSocket(int port) throws IOException {
          return new ServerSocket(port);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void start() {
    /* Initialize RMI */
    initRMI();

    /* Create object store object and export it for RMI */
    store = new ObjectStore();
    StoreRMIUtils.exportStoreObjectRMI(store);

    /* Creation objectStore before starting chord is very important
    otherwise chord upcalls with start getting NullPointerExceptions.
     */
    /* First start chord node and join network */
    chordSession = ChordDriver.getSession();
    chordSession.registerUpcall(new ChordEventHandler());
    chordSession.join();


    /* create client API object and export it for RMI */
    StoreClientAPIImpl storeClientAPI = new StoreClientAPIImpl();
    StoreRMIUtils.exportClientAPIRMI(storeClientAPI);
  }

  public static void main(String args[]) {
    ObjectStoreService.start();
  }
}
