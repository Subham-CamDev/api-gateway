package com.subham.cloud.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;

@Configuration
public class Routes {

  @Bean
  public RouterFunction<ServerResponse> productServiceRoute() {
    return GatewayRouterFunctions.route("product_service")
            .route(RequestPredicates.path("/api/product/**"),
                    HandlerFunctions.http())
            .before(uri("http://localhost:8080"))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCB",
                    URI.create("forward:/fallbackRoute")))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> productServiceSwaggerRoute() {
    return GatewayRouterFunctions.route("product_service_swagger")
            .route(RequestPredicates.path("/aggregate/product-service/**"),
                    HandlerFunctions.http())
            .before(uri("http://localhost:8080"))
            .before(rewritePath(
                    "/aggregate/product-service/(?<segment>.*)",
                    "/${segment}"
            ))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceSwaggerCB",
                    URI.create("forward:/fallbackRoute")))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> orderServiceRoute() {
    return GatewayRouterFunctions.route("order_service")
            .route(RequestPredicates.path("/api/order/**"),
                    HandlerFunctions.http())
            .before(uri("http://localhost:8081"))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCB",
                    URI.create("forward:/fallbackRoute")))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> orderServiceSwaggerRoute() {
    return GatewayRouterFunctions.route("order_service_swagger")
            .route(RequestPredicates.path("/aggregate/order-service/**"),
                    HandlerFunctions.http())
            .before(uri("http://localhost:8081"))
            .before(rewritePath(
                    "/aggregate/order-service/(?<segment>.*)",
                    "/${segment}"
            ))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceSwaggerCB",
                    URI.create("forward:/fallbackRoute")))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> inventoryServiceRoute() {
    return GatewayRouterFunctions.route("inventory_service")
            .route(RequestPredicates.path("/api/inventory/**"),
                    HandlerFunctions.http())
            .before(uri("http://localhost:8082"))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCB",
                    URI.create("forward:/fallbackRoute")))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> inventoryServiceSwaggerRoute() {
    return GatewayRouterFunctions.route("inventory_service_swagger")
            .route(RequestPredicates.path("/aggregate/inventory-service/**"),
                    HandlerFunctions.http())
            .before(uri("http://localhost:8082"))
            .before(rewritePath(
                    "/aggregate/inventory-service/(?<segment>.*)",
                    "/${segment}"
            ))
            .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceSwaggerCB",
                    URI.create("forward:/fallbackRoute")))
            .build();
  }

  @Bean
  public RouterFunction<ServerResponse> fallbackRoute() {
    return GatewayRouterFunctions.route("fallbackRoute")
            .GET("/fallbackRoute", request -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Service Unavailable, Please try again later."))
            .build();
  }
}
