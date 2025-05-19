package org.ashwin.flash.grpc;

import org.ashwin.flash.database.engine.Database;
import org.ashwin.flash.database.service.StorageService;
import org.ashwin.flash.proto.CreateDatabaseRequest;
import org.ashwin.flash.proto.CreateDatabaseResponse;
import org.ashwin.flash.proto.DeleteDatabaseRequest;
import org.ashwin.flash.proto.DeleteDatabaseResponse;
import org.ashwin.flash.proto.FlashDBServiceGrpc;
import org.ashwin.flash.proto.GetRequest;
import org.ashwin.flash.proto.GetResponse;
import org.ashwin.flash.proto.SetRequest;
import org.ashwin.flash.proto.SetResponse;
import org.ashwin.flash.proto.DeleteRequest;
import org.ashwin.flash.proto.DeleteResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class FlashDBServiceImpl extends FlashDBServiceGrpc.FlashDBServiceImplBase {

    @Override
    public void createDatabase(CreateDatabaseRequest request, StreamObserver<CreateDatabaseResponse> responseObserver) {
        String databaseName = request.getDatabase();
        boolean created = StorageService.getInstance().createDatabase(databaseName);

        CreateDatabaseResponse response = CreateDatabaseResponse.newBuilder()
                .setSuccess(created)
                .setMessage(created ?
                        "Database successfully created" :
                        "Database already exists")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteDatabase(DeleteDatabaseRequest request, StreamObserver<DeleteDatabaseResponse> responseObserver) {
        String databaseName = request.getDatabase();
        StorageService.getInstance().deleteDatabase(databaseName);

        DeleteDatabaseResponse response = DeleteDatabaseResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Database successfully deleted")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void get(GetRequest request, StreamObserver<GetResponse> responseObserver) {
        String key = request.getKey();
        String databaseName = request.getDatabase();
        Database database = StorageService.getInstance().getDatabase(databaseName);
        byte[] value = database.get(key);

        GetResponse response;
        if (value != null) {
            response = GetResponse.newBuilder()
                .setFound(true)
                .setValue(com.google.protobuf.ByteString.copyFrom(value))
                .build();
        } else {
            response = GetResponse.newBuilder()
                .setFound(false)
                .build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void set(SetRequest request, StreamObserver<SetResponse> responseObserver) {
        try {
            String key = request.getKey();
            String databaseName = request.getDatabase();
            Database database = StorageService.getInstance().getDatabase(databaseName);
            byte[] value = request.getValue().toByteArray();

            database.put(key, value);

            SetResponse response = SetResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Key-value pair successfully stored")
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            SetResponse response = SetResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Error storing key-value pair: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        String key = request.getKey();
        String databaseName = request.getDatabase();
        Database database = StorageService.getInstance().getDatabase(databaseName);
        boolean removed = database.delete(key);

        DeleteResponse response = DeleteResponse.newBuilder()
            .setSuccess(removed)
            .setMessage(removed ?
                "Key successfully deleted" :
                "Key not found")
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
