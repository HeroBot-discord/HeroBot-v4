package fr.matthieu.herobot.services;

import fr.matthieu.herobot.utilities.ServicesContainer;
import fr.matthieu.herobot.utilities.classes.Service;
import fr.matthieu.herobot.utilities.classes.ServicePriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RconService extends Service {

    private final Logger logger = LoggerFactory.getLogger(RconService.class);
    private ServerSocket server;
    private Thread rconThread;

    public RconService(ServicesContainer container) {
        super(ServicePriority.LOWEST, false, container);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void start() throws IOException {
        server = new ServerSocket(8000);

        rconThread = new Thread(this::rconThread, "RCON-SERVER");
        rconThread.start();
    }

    private void rconThread() {
        logger.info("Now listening for connections for RCON.");
        for (; ; ) {
            try {
                Socket socket = server.accept();
                logger.info("RCON Connection from {}", socket.getInetAddress().toString());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                outputStream.writeUTF("WELCOME");

                Thread clientThread = new Thread(() -> {
                    boolean loggedIn = false;

                    for (; ; ) {
                        try {
                            String line = inputStream.readUTF();

                            if (line.startsWith("LOGIN ")) {
                                String password = line.substring(6);
                                if (password.equalsIgnoreCase("MY PASSWORD")) {
                                    loggedIn = true;
                                    outputStream.writeUTF("SUCCESS");
                                    logger.info("Client successfully logged in");
                                }
                                continue;
                            }
                            if (!loggedIn) continue;

                            if (line.equalsIgnoreCase("SHUTDOWN")) {
                                this.container.shutdown();
                            }

                        } catch (Exception e) {
                            break;
                        }
                    }
                    try {
                        socket.close();
                    } catch (Exception e) {
                    }
                    logger.info("Socket disconnected.");
                    Thread.currentThread().interrupt();
                });
                clientThread.start();
            } catch (IOException exception) {

            }
        }
    }

    @Override
    public void shutdown() throws IOException {
        rconThread.interrupt();
        server.close();
    }

    @Override
    public void kill() {

    }
}
