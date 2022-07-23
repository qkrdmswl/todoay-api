package com.todoay.api.domain.auth.controller;

import com.todoay.api.domain.auth.dto.*;
import com.todoay.api.domain.auth.service.AuthService;
import com.todoay.api.domain.profile.service.ProfileService;
import com.todoay.api.global.exception.ErrorResponse;
import com.todoay.api.global.exception.ValidErrorResponse;
import com.todoay.api.global.jwt.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final ProfileService profileService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(
            summary = "회원가입을 한다.",
            description = "이메일,패스워드,닉네임을 입력하여 회원가입 진행. 각 항목별로 양식을 지키지 않을 경우 오류 발생.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "올바른 이메일/패스워드/닉네임 양식을 입력하지 않음.", content = @Content(schema = @Schema(implementation = ValidErrorResponse.class)))
            }
    )
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signup(@RequestBody @Validated AuthSaveDto authSaveDto) {  // validated하고 설정하면 그 중에 몇개만 골라서 검사 해줌. valid는 다 함
        authService.save(authSaveDto);
        // save까지 authService interface에 구현?
        return ResponseEntity.noContent().build();
    }

    // login 할 때는 jwt로 반환하기로
    @Operation(
            summary = "로그인을 한다.",
            description = "가입된 이메일과 패스워드로 로그인을 진행. 입력한 이메일과 패스워드가 가입된 이메일, 패스워드와 다르거나 없는 경우 오류 발생.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "일치하는 회원정보가 없음.", content = @Content(schema = @Schema(implementation = ValidErrorResponse.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Validated LoginRequestDto loginRequestDto) {
        authService.login(loginRequestDto);

        String accessToken = jwtTokenProvider.createAccessToken(loginRequestDto.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(loginRequestDto.getEmail());
        return ResponseEntity.status(201).body(new LoginResponseDto(accessToken,refreshToken));
    }

    @Operation(
            summary = "계정의 비밀번호를 변경한다",
            description ="토큰을 통해 얻은 email에 해당하는 계정의 비밀번호를 수정한다. 토큰이 존재하지 않거나, 입력한 비밀번호가 양식을 지키지 않는다면 오류가 발생한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "올바른 비밀번호 양식을 입력하지 않음",content = @Content(schema = @Schema(implementation = ValidErrorResponse.class))) ,
                    @ApiResponse(responseCode = "401", description = "JWT 토큰 에러 ",content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Validated AuthUpdatePasswordReqeustDto dto) {

        String loginId = jwtTokenProvider.getLoginId(); // 로그인 못한 상태에서 비밀번호 변경할 때엔 어떤 header를 쓰는지 정해야 할듯??
        authService.updateAuthPassword(loginId,dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "계정을 삭제한다",
            description = "토큰을 통해 얻은 email에 해당하는 계정의 상태를 삭제함으로 변경한다. 토큰에 관련되어 문제가 생길 경우 오류를 발생한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "401", description = "JWT 토큰 에러" ,content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @DeleteMapping("/my")
    public ResponseEntity<Void> deleteAccount() {
        String loginId = jwtTokenProvider.getLoginId();
        authService.deleteAuth(loginId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "닉네임 중복검사",
            description = "body를 통해 전달받은 nickname이 이미 저장된 닉네임인지 검사한다. 입력한 닉네임이 유효성 검사를 통과하지 못하거나, 입력한 닉네임이 이미 존재할 경우 오류를 발생한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "1. 유효성 검사 실패\t\n2. 이미 존재하는 닉네임 입력", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping("/nickname-duplicate-check")
    public ResponseEntity<Void> nicknameDuplicateCheck(@RequestBody @Validated NicknameDuplicateReqeustDto dto) {
        profileService.nicknameDuplicateCheck(dto.getNickname());
        return ResponseEntity.noContent().build();
    }



}
