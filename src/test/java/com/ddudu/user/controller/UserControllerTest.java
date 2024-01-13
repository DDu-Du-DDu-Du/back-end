package com.ddudu.user.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.config.JwtConfig;
import com.ddudu.config.WebSecurityConfig;
import com.ddudu.support.TestProperties;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UserResponse;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Stream;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
@Import({WebSecurityConfig.class, TestProperties.class, JwtConfig.class})
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserControllerTest {

  static final Faker faker = new Faker();
  static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
      .build();
  static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
      .claim("auth", Authority.NORMAL);

  @MockBean
  UserService userService;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  JwtEncoder jwtEncoder;

  String email;
  String password;
  String nickname;

  @BeforeEach
  void setUp() {
    email = faker.internet()
        .emailAddress();
    password = faker.internet()
        .password(8, 40, true, true, true);
    nickname = faker.funnyName()
        .name();
  }

  @Nested
  class POST_회원가입_API_테스트 {

    static Stream<Arguments> provideSignUpRequestAndStrings() {
      String email = faker.internet()
          .emailAddress();
      String password = faker.internet()
          .password(8, 40, true, true, true);
      String nickname = faker.funnyName()
          .name();
      String over20 = "s".repeat(21);
      String wrongEmail = faker.internet()
          .username();
      String shortPassword = faker.internet()
          .password(3, 7, true, true, true);
      String weakPassword = "password";
      String over50 = faker.howIMetYourMother()
          .quote()
          .repeat(2);
      String username = faker.aws()
          .region();
      String intro = faker.artist()
          .name();

      return Stream.of(
          Arguments.of(
              "선택 아이디가 " + over20, new SignUpRequest(over20, email, password, nickname, intro),
              "아이디는 최대 20자 입니다."
          ),
          Arguments.of(
              "이메일이 null", new SignUpRequest(username, null, password, nickname, intro),
              "이메일이 입력되지 않았습니다."
          ),
          Arguments.of(
              "이메일이 공백", new SignUpRequest(username, "", password, nickname, intro),
              "이메일이 입력되지 않았습니다."
          ),
          Arguments.of(
              "이메일이 " + wrongEmail,
              new SignUpRequest(username, wrongEmail, password, nickname, intro),
              "올바른 이메일 형식이 아닙니다."
          ),
          Arguments.of(
              "비밀번호가 null", new SignUpRequest(username, email, null, nickname, intro),
              "비밀번호가 입력되지 않았습니다."
          ),
          Arguments.of(
              "비밀번호가 " + shortPassword,
              new SignUpRequest(username, email, shortPassword, nickname, intro),
              "비밀번호는 8자리 이상의 영문, 숫자, 특수문자로 구성되어야 합니다."
          ),
          Arguments.of(
              "비밀번호가 " + weakPassword,
              new SignUpRequest(username, email, weakPassword, nickname, intro),
              "비밀번호는 8자리 이상의 영문, 숫자, 특수문자로 구성되어야 합니다."
          ),
          Arguments.of(
              "닉네임이 null", new SignUpRequest(username, email, password, null, intro),
              "닉네임이 입력되지 않았습니다."
          ),
          Arguments.of(
              "닉네임이 공백", new SignUpRequest(username, email, password, "", intro), "닉네임이 입력되지 않았습니다."
          ),
          Arguments.of(
              "닉네임이 공백", new SignUpRequest(username, email, password, " ", intro),
              "닉네임이 입력되지 않았습니다."
          ),
          Arguments.of(
              "닉네임이 " + over20, new SignUpRequest(username, email, password, over20, intro),
              "닉네임은 최대 20자 입니다."
          ),
          Arguments.of(
              "자기소개가 " + over50, new SignUpRequest(username, email, password, nickname, over50),
              "자기소개는 최대 50자 입니다."
          )
      );
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideSignUpRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(String cause, SignUpRequest request, String message)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 이메일이_존재하면_400_Bad_Request를_반환한다() throws Exception {
      // given
      SignUpRequest request = new SignUpRequest(null, email, password, nickname, null);

      given(userService.signUp(any(SignUpRequest.class)))
          .willThrow(new DuplicateResourceException(UserErrorCode.DUPLICATE_EMAIL));

      // when
      ResultActions actions = mockMvc.perform(post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code", is(UserErrorCode.DUPLICATE_EMAIL.getCode())));
    }

    @Test
    void 선택_아이디가_존재하면_400_Bad_Request를_반환한다() throws Exception {
      // given
      String username = faker.name()
          .firstName();
      SignUpRequest request = new SignUpRequest(username, email, password, nickname, null);

      given(userService.signUp(any(SignUpRequest.class)))
          .willThrow(new DuplicateResourceException(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME));

      // when
      ResultActions actions = mockMvc.perform(post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code", is(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME.getCode())));
    }

    @Test
    void 회원가입을_성공하면_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      SignUpRequest request = new SignUpRequest(null, email, password, nickname, null);
      SignUpResponse response = SignUpResponse.builder()
          .id(userId)
          .email(email)
          .nickname(nickname)
          .build();

      given(userService.signUp(any(SignUpRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post("/api/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", is("/api/users/" + userId)));
    }

  }

  @Nested
  class GET_사용자_단일_조회_API_테스트 {

    @Test
    void 단일_조회를_성공하고_200_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      UserResponse expected = UserResponse.builder()
          .id(userId)
          .email(email)
          .nickname(nickname)
          .build();

      given(userService.findById(anyLong())).willReturn(expected);

      // when
      ResultActions actions = mockMvc.perform(get("/api/users/{id}", userId));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    void 사용자가_없으면_단일_조회를_실패하고_404_Not_Found를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong();

      given(userService.findById(anyLong())).willThrow(
          new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get("/api/users/{id}", userId));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code").value(UserErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message").value(UserErrorCode.ID_NOT_EXISTING.getMessage()));
    }

  }

  @Nested
  class GET_사용자_검색_API_테스트 {

    String introduction;
    String keyword;

    @BeforeEach
    void setUp() {
      introduction = faker.book()
          .title();
      keyword = faker.lorem()
          .word();
    }

    @Test
    void 사용자_검색을_성공하고_200_OK를_반환한다() throws Exception {
      // given
      String nickname = faker.funnyName()
          .name();
      UserProfileResponse profile = createUserProfile(nickname);
      List<UserProfileResponse> expected = List.of(profile);

      given(userService.findAllByKeyword(anyString())).willReturn(expected);

      // when
      ResultActions actions = mockMvc.perform(
          get("/api/users")
              .queryParam("keyword", keyword)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(expected.size()))
          .andExpect(jsonPath("$[0].id").value(profile.id()))
          .andExpect(jsonPath("$[0].nickname").value(profile.nickname()))
          .andExpect(jsonPath("$[0].introduction").value(profile.introduction()));
    }

    private UserProfileResponse createUserProfile(String nickname) {
      Long id = faker.random()
          .nextLong();

      return new UserProfileResponse(id, nickname, introduction);
    }

  }

  @Nested
  class PATCH_이메일_변경_API_테스트 {

    static Stream<Arguments> provideUpdateEmailRequestAndStrings() {
      String wrongEmail = faker.internet()
          .username();

      return Stream.of(
          Arguments.of(
              "이메일이 null", new UpdateEmailRequest(null),
              "이메일이 입력되지 않았습니다."
          ),
          Arguments.of(
              "이메일이 공백", new UpdateEmailRequest(""),
              "이메일이 입력되지 않았습니다."
          ),
          Arguments.of(
              "이메일이 " + wrongEmail,
              new UpdateEmailRequest(wrongEmail),
              "올바른 이메일 형식이 아닙니다."
          )
      );
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideUpdateEmailRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(
        String cause, UpdateEmailRequest request, String message
    )
        throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String token = createBearerToken(userId);

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/email", userId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 이메일_변경_성공하면_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong();
      String token = createBearerToken(userId);

      String newEmail = faker.internet()
          .emailAddress();
      UpdateEmailRequest request = new UpdateEmailRequest(newEmail);
      UserResponse response = UserResponse.builder()
          .id(userId)
          .email(request.email())
          .nickname(nickname)
          .build();

      given(userService.updateEmail(anyLong(), anyLong(), any(UpdateEmailRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/email", userId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(request.email()));
    }

  }

  @Nested
  class PATCH_비밀번호_변경_API_테스트 {

    static Stream<Arguments> provideUpdatePasswordRequestAndStrings() {
      String shortPassword = faker.internet()
          .password(2, 7, true, true, true);
      String weakPassword = "password";

      return Stream.of(
          Arguments.of(
              "비밀번호가 null", new UpdatePasswordRequest(null),
              "비밀번호가 입력되지 않았습니다."
          ),
          Arguments.of(
              "비밀번호가 " + shortPassword,
              new UpdatePasswordRequest(shortPassword),
              "비밀번호는 8자리 이상의 영문, 숫자, 특수문자로 구성되어야 합니다."
          ),
          Arguments.of(
              "비밀번호가 " + weakPassword,
              new UpdatePasswordRequest(weakPassword),
              "비밀번호는 8자리 이상의 영문, 숫자, 특수문자로 구성되어야 합니다."
          )
      );
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideUpdatePasswordRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(
        String cause, UpdatePasswordRequest request, String message
    )
        throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String token = createBearerToken(userId);

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/password", userId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 비밀번호_변경_성공하면_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String token = createBearerToken(userId);

      String newPassword = faker.internet()
          .password(8, 40, true, true, true);
      UpdatePasswordRequest request = new UpdatePasswordRequest(newPassword);
      String successMessage = "비밀번호가 성공적으로 변경되었습니다.";
      UpdatePasswordResponse response = new UpdatePasswordResponse(successMessage);

      given(userService.updatePassword(anyLong(), anyLong(), any(UpdatePasswordRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/password", userId)
          .header("Authorization", token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value(successMessage));
    }

  }

  @Nested
  class PUT_프로필_수정_API_테스트 {

    String introduction;
    Long userId;

    @BeforeEach
    void setUp() {
      introduction = faker.book()
          .title();
      userId = faker.random()
          .nextLong();
    }

    static Stream<Arguments> provideUpdateProfileRequestAndStrings() {
      String nickname = faker.funnyName()
          .name();
      String introduction = faker.book()
          .title();
      String over20 = "s".repeat(21);
      String over50 = faker.howIMetYourMother()
          .quote()
          .repeat(2);

      return Stream.of(
          Arguments.of(
              "닉네임이 null", new UpdateProfileRequest(null, introduction),
              "닉네임이 입력되지 않았습니다."
          ),
          Arguments.of(
              "닉네임이 공백", new UpdateProfileRequest("", introduction), "닉네임이 입력되지 않았습니다."
          ),
          Arguments.of(
              "닉네임이 " + over20, new UpdateProfileRequest(over20, introduction),
              "닉네임은 최대 20자 입니다."
          ),
          Arguments.of(
              "자기소개가 " + over50, new UpdateProfileRequest(nickname, over50),
              "자기소개는 최대 50자 입니다."
          )
      );
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideUpdateProfileRequestAndStrings")
    void 유효하지_않은_요청이면_Bad_Request를_반환한다(
        String cause, UpdateProfileRequest request, String message
    ) throws Exception {
      // when
      ResultActions actions = mockMvc.perform(
          put("/api/users/{id}/profile", userId)
              .header("Authorization", createBearerToken(userId))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 프로필_수정_권한이_없으면_Forbidden을_반환한다() throws Exception {
      // given
      long invalidId = faker.random()
          .nextLong();
      UpdateProfileRequest request = new UpdateProfileRequest(nickname, introduction);

      given(userService.updateProfile(anyLong(), anyLong(), any(UpdateProfileRequest.class)))
          .willThrow(new ForbiddenException(UserErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions actions = mockMvc.perform(
          put("/api/users/{id}/profile", userId)
              .header("Authorization", createBearerToken(invalidId))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(UserErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(jsonPath("$.message", is(UserErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    @Test
    void 프로필_수정을_성공하면_OK를_반환한다() throws Exception {
      // given
      UpdateProfileRequest request = new UpdateProfileRequest(nickname, introduction);
      UserProfileResponse response = UserProfileResponse.builder()
          .id(userId)
          .nickname(nickname)
          .introduction(introduction)
          .build();

      given(userService.updateProfile(anyLong(), anyLong(), any(UpdateProfileRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(
          put("/api/users/{id}/profile", userId)
              .header("Authorization", createBearerToken(userId))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.nickname").value(response.nickname()))
          .andExpect(jsonPath("$.introduction").value(response.introduction()));
    }

  }

  @Nested
  class PATCH_사용자_옵션_토글_API_테스트 {

    long userId;

    @BeforeEach
    void setUp() {
      userId = faker.random()
          .nextLong(Long.MAX_VALUE);
    }

    @Test
    void 수락한_사람만_팔로우_옵션을_토글하고_200_OK를_반환한다() throws Exception {
      // given
      String token = createBearerToken(userId);
      ToggleOptionResponse response = new ToggleOptionResponse(userId, true);

      given(userService.switchOption(anyLong(), anyLong()))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/options", userId)
          .header("Authorization", token));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(userId))
          .andExpect(jsonPath("$.allowFollowsAfterApproval")
              .value(response.allowFollowsAfterApproval()));
    }

    @Test
    void 로그인한_사용자가_요청된_사용자와_다를_경우_403_Forbidden을_반환한다() throws Exception {
      // given
      long randomId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String token = createBearerToken(randomId);

      given(userService.switchOption(anyLong(), anyLong()))
          .willThrow(new ForbiddenException(UserErrorCode.INVALID_AUTHENTICATION));

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/options", userId)
          .header("Authorization", token));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code").value(UserErrorCode.INVALID_AUTHENTICATION.getCode()))
          .andExpect(
              jsonPath("$.message").value(UserErrorCode.INVALID_AUTHENTICATION.getMessage()));
    }

    @Test
    void 존재하지_않는_사용자에_대한_요청일_경우_404_Not_Found를_반환한다() throws Exception {
      // given
      String token = createBearerToken(userId);

      given(userService.switchOption(anyLong(), anyLong()))
          .willThrow(new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(patch("/api/users/{id}/options", userId)
          .header("Authorization", token));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code").value(UserErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message").value(UserErrorCode.ID_NOT_EXISTING.getMessage()));
    }

  }

  private String createBearerToken(long userId) {
    JwtClaimsSet claims = claimSet.claim("user", userId)
        .build();
    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
    return "Bearer " + jwt.getTokenValue();
  }

}
