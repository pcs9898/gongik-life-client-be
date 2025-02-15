//package org.example.gongiklifeclientbegraphql.directive;
//
//import graphql.GraphQLException;
//import graphql.schema.DataFetcher;
//import graphql.schema.DataFetcherFactories;
//import graphql.schema.GraphQLDirective;
//import graphql.schema.GraphQLFieldDefinition;
//import graphql.schema.GraphQLInputObjectField;
//import graphql.schema.GraphQLObjectType;
//import graphql.schema.idl.SchemaDirectiveWiring;
//import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
//import org.springframework.stereotype.Component;
//
//@Component
//public class IntRangeDirective implements SchemaDirectiveWiring {
//
//  @Override
//  public GraphQLInputObjectField onInputObjectField(
//      SchemaDirectiveWiringEnvironment<GraphQLInputObjectField> environment) {
//    GraphQLFieldDefinition fieldDefinition = environment.getFieldDefinition();
//    GraphQLObjectType parentType = (GraphQLObjectType) environment.getFieldsContainer();
//    GraphQLDirective directive = environment.getDirective();
//
//    Integer min = (Integer) directive.getArgument("min").getArgumentDefaultValue().getValue();
//    Integer max = (Integer) directive.getArgument("max").getArgumentDefaultValue().getValue();
//
//    // 새로운 DataFetcher 생성
//    DataFetcher<?> originalFetcher = environment.getFieldDataFetcher();
//    DataFetcher<?> validatingFetcher = DataFetcherFactories.wrapDataFetcher(
//        originalFetcher,
//        (dataFetchingEnvironment, value) -> {
//          if (value instanceof Integer) {
//            int intValue = (Integer) value;
//            if (intValue < min || intValue > max) {
//              throw new GraphQLException(
//                  String.format("Value %d must be between %d and %d", intValue, min, max)
//              );
//            }
//          }
//          return value;
//        }
//    );
//
//    // CodeRegistry에 새로운 DataFetcher 등록
//    environment.getCodeRegistry().dataFetcher(
//        (GraphQLObjectType) environment.getFieldsContainer(),
//        environment.getFieldDefinition(),
//        validatingFetcher
//    );
//
//    return field;
//  }
//}
