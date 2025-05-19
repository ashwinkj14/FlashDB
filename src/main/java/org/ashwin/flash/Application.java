package org.ashwin.flash;

import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.ashwin.flash.grpc.FlashDBServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private Server server;
    private static final int PORT = 9090;

    private void start() throws Exception {
        server = ServerBuilder.forPort(PORT)
                .addService(new FlashDBServiceImpl())
                .intercept(new ServerInterceptor() {
                    @Override
                    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
                            ServerCall<ReqT, RespT> call,
                            Metadata headers,
                            ServerCallHandler<ReqT, RespT> next) {
                        return next.startCall(call, headers);
                    }
                })
                .executor(Executors.newFixedThreadPool(10))
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