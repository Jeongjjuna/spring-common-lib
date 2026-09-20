package yjh.ontongsal.common.web.swagger.scanner;

import io.swagger.v3.oas.models.Operation;
import org.springframework.web.method.HandlerMethod;

public interface ResultCodeScanner {
    void initializeResultCode(String basePackage);

    Operation applyResultCodeToSwagger(Operation operation, HandlerMethod handlerMethod);
}
