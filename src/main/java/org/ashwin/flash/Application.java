package org.ashwin.flash;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.ashwin.flash.grpc.FlashDBServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private Server server;
    private static final int PORT = 9090;

    private void start() throws Exception {
        server = ServerBuilder.forPort(PORT)
                .addService(new FlashDBServiceImpl())
                .build()
                .start();

        logger.info("Flash gRPC Server started on port " + PORT);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            if (server != null) {
                server.shutdown();
            }
            logger.info("Server shut down");
        }));

        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) {
        try {
            Application app = new Application();
            app.start();
            logger.info("Flash running");
        } catch (Exception e) {
            logger.error("Error starting server: ", e);
        }
    }
}