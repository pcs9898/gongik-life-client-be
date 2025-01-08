//package org.example.gongiklifeclientbeinstitutionservice.config;
//
//import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
//import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GrpcServerConfig {
//
//  @Bean
//  public GrpcServerConfigurer grpcServerConfigurer() {
//    return serverBuilder -> {
//      if (serverBuilder instanceof NettyServerBuilder) {
//        ((NettyServerBuilder) serverBuilder).channelType(
//            io.grpc.netty.shaded.io.netty.channel.socket.nio.NioServerSocketChannel.class);
//      }
//    };
//  }
//}