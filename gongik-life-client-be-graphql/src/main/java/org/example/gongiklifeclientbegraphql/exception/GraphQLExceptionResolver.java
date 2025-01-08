package org.example.gongiklifeclientbegraphql.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {

  @Override
  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    if (ex instanceof BadRequestException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.BAD_REQUEST)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else if (ex instanceof UnauthorizedException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.UNAUTHORIZED)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else if (ex instanceof ForbiddenException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.FORBIDDEN)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();

    } else if (ex instanceof NotFoundException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.NOT_FOUND)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else if (ex instanceof RuntimeException) {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.INTERNAL_ERROR)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    } else {
      return GraphqlErrorBuilder.newError()
          .errorType(ErrorType.INTERNAL_ERROR)
          .message(ex.getMessage())
          .path(env.getExecutionStepInfo().getPath())
          .location(env.getField().getSourceLocation())
          .build();
    }
  }
}

//
// if (ex instanceof CourseNotFoundException) {
//    return GraphqlErrorBuilder.newError()
//                    .errorType(ErrorType.NOT_FOUND)
//                    .message(ex.getMessage())
//    .path(env.getExecutionStepInfo().getPath())
//    .location(env.getField().getSourceLocation())
//    .build();
//        } else if (ex instanceof UnauthorizedException) {
//    return GraphqlErrorBuilder.newError()
//                    .errorType(ErrorType.UNAUTHORIZED)
//                    .message(ex.getMessage())
//    .path(env.getExecutionStepInfo().getPath())
//    .location(env.getField().getSourceLocation())
//    .build();
//        } else {
//            return null;
//            }