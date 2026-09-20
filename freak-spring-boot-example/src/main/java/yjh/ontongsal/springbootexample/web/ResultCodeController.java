package yjh.ontongsal.springbootexample.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yjh.ontongsal.common.domain.exception.BusinessException;
import yjh.ontongsal.common.web.api.ApiController;
import yjh.ontongsal.common.web.api.ApiResponse;
import yjh.ontongsal.springbootexample.ExampleResultCode;

import java.util.List;
import java.util.function.Supplier;

@Tag(name = "[응답 코드 스캔 테스트 API]", description = "응답코드(Result Code 테스트를 위한 API")
@RequestMapping("/result-test")
@RestController
public class ResultCodeController implements ApiController {

    /*
     * 1. 단순 throw
     */
    @Operation(
            summary = "직접 ResultCode throw",
            description = "메서드에서 ResultCode를 직접 BusinessException에 전달하는 가장 기본적인 케이스"
    )
    @GetMapping("/direct")
    public ApiResponse<Void> direct() {
        throw new BusinessException(ExampleResultCode.INTERNAL_SERVER_ERROR);
    }

    /*
     * 2. 여러 ResultCode
     */
    @Operation(
            summary = "여러 ResultCode",
            description = "하나의 API에서 여러 분기에 걸쳐 서로 다른 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/multiple")
    public ApiResponse<Void> multiple(@RequestParam int code) {
        if (code == 400) {
            throw new BusinessException(ExampleResultCode.INVALID_PARAMETER);
        }

        if (code == 404) {
            throw new BusinessException(ExampleResultCode.NOT_FOUNT);
        }

        if (code == 500) {
            throw new BusinessException(ExampleResultCode.INTERNAL_SERVER_ERROR);
        }

        return success();
    }

    /*
     * 3. if 내부
     */
    @Operation(
            summary = "if 내부 ResultCode",
            description = "if 조건문 내부에서 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/if")
    public ApiResponse<Void> ifTest(@RequestParam boolean error) {
        if (error) {
            throw new BusinessException(ExampleResultCode.INTERNAL_SERVER_ERROR);
        }

        return success();
    }

    /*
     * 4. else 내부
     */
    @Operation(
            summary = "if-else 내부 ResultCode",
            description = "if와 else 각각에서 서로 다른 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/else")
    public ApiResponse<Void> elseTest(@RequestParam boolean error) {
        if (error) {
            throw new BusinessException(ExampleResultCode.INTERNAL_SERVER_ERROR);
        } else {
            throw new BusinessException(ExampleResultCode.NOT_FOUNT);
        }
    }

    /*
     * 5. 삼항 연산자
     */
    @Operation(
            summary = "삼항 연산자 ResultCode",
            description = "삼항 연산자를 통해 ResultCode를 선택하는 케이스"
    )
    @GetMapping("/ternary")
    public ApiResponse<Void> ternary(@RequestParam boolean error) {
        ExampleResultCode resultCode = error
                ? ExampleResultCode.INTERNAL_SERVER_ERROR
                : ExampleResultCode.NOT_FOUNT;

        throw new BusinessException(resultCode);
    }

    /*
     * 6. 지역 변수
     */
    @Operation(
            summary = "지역 변수 ResultCode",
            description = "ResultCode를 지역 변수에 할당한 후 예외에 전달하는 케이스"
    )
    @GetMapping("/local-variable")
    public ApiResponse<Void> localVariable() {
        ExampleResultCode resultCode =
                ExampleResultCode.INTERNAL_SERVER_ERROR;

        throw new BusinessException(resultCode);
    }

    /*
     * 7. 메서드 호출을 통한 ResultCode
     */
    @Operation(
            summary = "메서드 호출 ResultCode",
            description = "별도의 메서드에서 반환한 ResultCode를 BusinessException에 전달하는 케이스"
    )
    @GetMapping("/method")
    public ApiResponse<Void> method() {
        throw new BusinessException(getResultCode());
    }

    private ExampleResultCode getResultCode() {
        return ExampleResultCode.INTERNAL_SERVER_ERROR;
    }

    /*
     * 8. 람다 내부
     */
    @Operation(
            summary = "람다 내부 ResultCode",
            description = "람다 표현식 내부에서 ResultCode를 사용하여 예외를 발생시키는 케이스"
    )
    @GetMapping("/lambda")
    public ApiResponse<Void> lambda() {
        Runnable runnable = () -> {
            throw new BusinessException(
                    ExampleResultCode.INTERNAL_SERVER_ERROR
            );
        };

        runnable.run();

        return success();
    }

    /*
     * 9. 람다에서 ResultCode 참조
     */
    @Operation(
            summary = "람다를 통한 ResultCode",
            description = "람다에서 ResultCode를 반환하고 그 결과를 BusinessException에 전달하는 케이스"
    )
    @GetMapping("/lambda-value")
    public ApiResponse<Void> lambdaValue() {
        Supplier<ExampleResultCode> supplier =
                () -> ExampleResultCode.INTERNAL_SERVER_ERROR;

        throw new BusinessException(supplier.get());
    }

    /*
     * 10. Stream 내부
     */
    @Operation(
            summary = "Stream 내부 ResultCode",
            description = "Stream의 forEach 람다 내부에서 여러 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/stream")
    public ApiResponse<Void> stream() {
        List<ExampleResultCode> codes = List.of(
                ExampleResultCode.NOT_FOUNT,
                ExampleResultCode.INTERNAL_SERVER_ERROR
        );

        codes.stream()
                .forEach(code -> {
                    throw new BusinessException(code);
                });

        return success();
    }

    /*
     * 11. 익명 클래스
     */
    @Operation(
            summary = "익명 클래스 내부 ResultCode",
            description = "익명 클래스의 메서드 내부에서 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/anonymous")
    public ApiResponse<Void> anonymous() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                throw new BusinessException(
                        ExampleResultCode.INTERNAL_SERVER_ERROR
                );
            }
        };

        runnable.run();

        return success();
    }

    /*
     * 12. static 메서드 내부
     */
    @Operation(
            summary = "static 메서드 호출 ResultCode",
            description = "별도의 static 메서드를 호출하여 ResultCode를 반환받는 케이스"
    )
    @GetMapping("/static-method")
    public ApiResponse<Void> staticMethod() {
        throw new BusinessException(
                ResultCodeProvider.internalServerError()
        );
    }

    static class ResultCodeProvider {

        static ExampleResultCode internalServerError() {
            return ExampleResultCode.INTERNAL_SERVER_ERROR;
        }
    }

    /*
     * 13. switch
     */
    @Operation(
            summary = "switch 내부 ResultCode",
            description = "switch의 여러 case에서 서로 다른 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/switch")
    public ApiResponse<Void> switchTest(@RequestParam int code) {
        switch (code) {
            case 400:
                throw new BusinessException(
                        ExampleResultCode.INVALID_PARAMETER
                );

            case 404:
                throw new BusinessException(
                        ExampleResultCode.NOT_FOUNT
                );

            case 500:
                throw new BusinessException(
                        ExampleResultCode.INTERNAL_SERVER_ERROR
                );

            default:
                return success();
        }
    }

    /*
     * 14. try-catch
     */
    @Operation(
            summary = "try-catch 내부 ResultCode",
            description = "catch 블록에서 ResultCode를 사용하여 BusinessException을 발생시키는 케이스"
    )
    @GetMapping("/try-catch")
    public ApiResponse<Void> tryCatch() {
        try {
            throw new BusinessException(ExampleResultCode.FORBIDDEN);
        } catch (RuntimeException e) {
            throw new BusinessException(ExampleResultCode.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * 15. finally
     */
    @Operation(
            summary = "finally",
            description = "finally 블록이 포함된 메서드. ResultCode가 사용되지 않는 케이스"
    )
    @GetMapping("/finally")
    public ApiResponse<Void> finallyTest() {
        try {
            return success();
        } finally {
            log();
        }
    }

    private void log() {
        System.out.println("finally");
    }

    /*
     * 16. 다른 메서드 호출
     */
    @Operation(
            summary = "다른 메서드 호출 ResultCode",
            description = "Controller 메서드에서 별도의 메서드를 호출하고 해당 메서드에서 ResultCode를 사용하는 케이스"
    )
    @GetMapping("/delegation")
    public ApiResponse<Void> delegation() {
        return execute();
    }

    private ApiResponse<Void> execute() {
        throw new BusinessException(
                ExampleResultCode.INTERNAL_SERVER_ERROR
        );
    }

    /*
     * 17. 상속/인터페이스 구현체 메서드 호출
     */
    @Operation(
            summary = "인터페이스 구현체 ResultCode",
            description = "인터페이스 타입을 통해 구현체의 메서드를 호출하고 ResultCode를 반환받는 케이스"
    )
    @GetMapping("/interface")
    public ApiResponse<Void> interfaceTest() {
        ResultCodeProviderInterface provider =
                new InternalServerErrorProvider();

        throw new BusinessException(provider.getResultCode());
    }

    interface ResultCodeProviderInterface {

        ExampleResultCode getResultCode();
    }

    static class InternalServerErrorProvider
            implements ResultCodeProviderInterface {

        @Override
        public ExampleResultCode getResultCode() {
            return ExampleResultCode.INTERNAL_SERVER_ERROR;
        }
    }
}