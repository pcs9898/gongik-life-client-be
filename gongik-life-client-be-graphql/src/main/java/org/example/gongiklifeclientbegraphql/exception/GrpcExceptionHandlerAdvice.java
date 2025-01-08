//package org.example.gongiklifeclientbegraphql.exception;
//
//import io.grpc.Status;
//import net.devh.boot.grpc.server.advice.GrpcAdvice;
//import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@GrpcAdvice
//public class GrpcExceptionHandlerAdvice {
//
//  private static final Logger log = LoggerFactory.getLogger(GrpcExceptionHandler.class);
//
//  @GrpcExceptionHandler(Exception.class)
//  public Status handleException(Exception e) {
//    log.error("gRPC service error: {}", e.getMessage(), e);
//    return Status.INTERNAL
//        .withDescription(e.getMessage())
//        .withCause(e);
//  }
//}