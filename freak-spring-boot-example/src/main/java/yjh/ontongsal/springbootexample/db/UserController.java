package yjh.ontongsal.springbootexample.db;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import yjh.ontongsal.common.web.api.ApiController;
import yjh.ontongsal.common.web.api.ApiResponse;
import yjh.ontongsal.springbootexample.db.jpa.repository.user.UserEntity;

@Tag(name = "[DB 연동 테스트 API]", description = "Persistence 연동 테스트")
@RequestMapping("/users")
@RestController
public class UserController implements ApiController {
    private final UserServiceCase1 userServiceCase1;
    private final UserServiceCase2 userServiceCase2;

    public UserController(UserServiceCase1 userServiceCase1, UserServiceCase2 userServiceCase2) {
        this.userServiceCase1 = userServiceCase1;
        this.userServiceCase2 = userServiceCase2;
    }

    @Operation(summary = "User 생성")
    @PostMapping
    public ApiResponse<UserEntity> create() {
        return success(userServiceCase1.create());
    }

    @Operation(summary = "User 조회")
    @GetMapping("/{id}")
    public ApiResponse<UserEntity> get(@PathVariable Long id) {
        return success(userServiceCase1.get(id));
    }

    @Operation(summary = "User 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userServiceCase1.delete(id);
        return success();
    }
}
