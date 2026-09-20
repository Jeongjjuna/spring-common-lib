package yjh.ontongsal.common.web.swagger.scanner.auto;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import yjh.ontongsal.common.domain.exception.ResultCode;
import yjh.ontongsal.common.web.swagger.scanner.ClassHierarchyScanner;
import yjh.ontongsal.common.web.swagger.scanner.ResultCodeScanner;

import java.util.*;

public class AutoResultCodeScanner implements ResultCodeScanner {
    private static final Logger log = LoggerFactory.getLogger(AutoResultCodeScanner.class);

    private final Map<String, ResultCodeEntry> resultCodeInfo = new LinkedHashMap<>();
    private Map<String, List<String>> abstractClassInfo = new HashMap<>();
    private Map<String, List<String>> interfaceInfo = new HashMap<>();
    private List<String> resultCodeClassNames = new ArrayList<>();

    @Override
    public void initializeResultCode(String basePackage) {
        this.abstractClassInfo = ClassHierarchyScanner.createAbstractSubclassMap(List.of(basePackage));
        this.interfaceInfo = ClassHierarchyScanner.createInterfaceImplementationMap(List.of(basePackage));

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .scan()
        ) {
            List<String> classNames = scanResult.getClassesImplementing(ResultCode.class.getName()).stream()
                    .filter(ClassInfo::isEnum)
                    .map(ClassInfo::getName)
                    .toList();

            this.resultCodeClassNames = classNames;

            List<ResultCode> resultCodes = classNames.stream()
                    .map(name -> {
                        try {
                            return Class.forName(name);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .flatMap(clazz -> Arrays.stream(clazz.getEnumConstants()))
                    .filter(c -> c instanceof ResultCode)
                    .map(c -> (ResultCode) c)
                    .toList();

            for (ResultCode resultCode : resultCodes) {
                String key = resultCode.getClass().getName() + "." + ((Enum<?>) resultCode).name();
                resultCodeInfo.put(key, new ResultCodeEntry(resultCode.getCode(), resultCode.getMessage()));
            }

            log.info("ResultCode 스캔 완료: {}개 ({} 종류)", resultCodes.size(), classNames.size());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public Operation applyResultCodeToSwagger(Operation operation, HandlerMethod handlerMethod) {
        try {
            AsmResultCodeDetector detector = AsmResultCodeDetector.builder(resultCodeClassNames)
                    .abstractMap(abstractClassInfo)
                    .interfaceMap(interfaceInfo)
                    .build();

            var className = handlerMethod.getBeanType().getName();
            var methodName = handlerMethod.getMethod().getName();
            var detectNames = detector.analyze(className, methodName).thenGetDetectResultCodeNames();

            drawToSwagger(operation, detectNames);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return operation;
    }

//    private void drawToSwagger(Operation operation, Set<String> names) {
//        ApiResponses response = operation.getResponses();
//
//        Map<Integer, List<ExampleHolder>> grouped = names.stream()
//                .map(resultCodeInfo::get)
//                .filter(Objects::nonNull)
//                .map(entry -> ExampleHolder.builder()
//                        .holder(toSwaggerExample(entry.code, entry.message))
//                        .code(entry.code())
//                        .name(entry.message())
//                        .build())
//                .collect(Collectors.groupingBy(ExampleHolder::code));
//
//        grouped.forEach((status, holders) -> {
//            var mediaType = new MediaType();
//            holders.forEach(h -> mediaType.addExamples(h.name(), h.holder()));
//
//            var content = new Content();
//            content.addMediaType("application/json", mediaType);
//
//            var apiResponse = new ApiResponse();
//            apiResponse.setContent(content);
//
//            // String.valueOf(status)
//            response.addApiResponse("HttpStatus : 200(" + status + ")", apiResponse);
//        });
//    }

    private void drawToSwagger(Operation operation, Set<String> names) {
        ApiResponses responses = operation.getResponses();

        MediaType mediaType = new MediaType();

        // 기본 정상 응답
        mediaType.addExamples(
                "SUCCESS",
                toSwaggerExample(1, "SUCCESS")
        );

        // API에서 실제 사용되는 ResultCode
        names.stream()
                .map(resultCodeInfo::get)
                .filter(Objects::nonNull)
                .forEach(entry ->
                        mediaType.addExamples(
                                entry.message(),
                                toSwaggerExample(
                                        entry.code(),
                                        entry.message()
                                )
                        )
                );

        Content content = new Content();
        content.addMediaType("application/json", mediaType);

        ApiResponse apiResponse = new ApiResponse()
                .description("정상적인 API 응답")
                .content(content);

        responses.addApiResponse("200", apiResponse);
    }

    private Example toSwaggerExample(int code, String message) {
        var value = new LinkedHashMap<String, Object>();
        value.put("result_code", code);
        value.put("result_message", message);
        value.put("data", null);
        value.put("trace_id", "550e8400-e29b-41d4-a716-446655440000");

        var example = new Example();
        example.setValue(value);
        return example;
    }

    private record ResultCodeEntry(int code, String message) {
    }
}
