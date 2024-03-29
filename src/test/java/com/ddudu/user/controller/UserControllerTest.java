package com.ddudu.user.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ddudu.common.exception.BadRequestException;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.support.ControllerTestSupport;
import com.ddudu.user.domain.FollowingStatus;
import com.ddudu.user.domain.UserSearchType;
import com.ddudu.user.dto.FollowingSearchType;
import com.ddudu.user.dto.SimpleUserDto;
import com.ddudu.user.dto.request.FollowRequest;
import com.ddudu.user.dto.request.SignUpRequest;
import com.ddudu.user.dto.request.UpdateEmailRequest;
import com.ddudu.user.dto.request.UpdateFollowingRequest;
import com.ddudu.user.dto.request.UpdatePasswordRequest;
import com.ddudu.user.dto.request.UpdateProfileRequest;
import com.ddudu.user.dto.response.FollowingResponse;
import com.ddudu.user.dto.response.SignUpResponse;
import com.ddudu.user.dto.response.ToggleOptionResponse;
import com.ddudu.user.dto.response.UpdateEmailResponse;
import com.ddudu.user.dto.response.UpdatePasswordResponse;
import com.ddudu.user.dto.response.UserProfileResponse;
import com.ddudu.user.dto.response.UsersResponse;
import com.ddudu.user.exception.FollowingErrorCode;
import com.ddudu.user.exception.UserErrorCode;
import com.ddudu.user.service.FollowingService;
import com.ddudu.user.service.UserService;
import java.util.List;
import java.util.stream.Stream;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTestSupport {

  static final Faker faker = new Faker();

  @MockBean
  UserService userService;

  @MockBean
  FollowingService followingService;

  String email;
  String password;
  String nickname;

  @BeforeEach
  void setUp() {
    email = faker.internet()
        .emailAddress();
    password = faker.internet()
        .password(8, 40, true, true, true);
    nickname = faker.internet()
        .username();
  }

  @Nested
  class POST_회원가입_API_테스트 {

    static final String PATH = "/api/users";

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

    @Test
    void 회원가입을_성공하면_201_Created를_반환한다() throws Exception {
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
      ResultActions actions = mockMvc.perform(post(PATH)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", is(PATH + "/" + userId)));
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideSignUpRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(String cause, SignUpRequest request, String message)
        throws Exception {
      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 이메일이_존재하면_409_Conflict를_반환한다() throws Exception {
      // given
      SignUpRequest request = new SignUpRequest(null, email, password, nickname, null);

      given(userService.signUp(any(SignUpRequest.class)))
          .willThrow(new DuplicateResourceException(UserErrorCode.DUPLICATE_EMAIL));

      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isConflict())
          .andExpect(jsonPath("$.code", is(UserErrorCode.DUPLICATE_EMAIL.getCode())));
    }

    @Test
    void 선택_아이디가_존재하면_409_Conflict를_반환한다() throws Exception {
      // given
      String username = faker.name()
          .firstName();
      SignUpRequest request = new SignUpRequest(username, email, password, nickname, null);

      given(userService.signUp(any(SignUpRequest.class)))
          .willThrow(new DuplicateResourceException(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME));

      // when
      ResultActions actions = mockMvc.perform(post(PATH)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isConflict())
          .andExpect(jsonPath("$.code", is(UserErrorCode.DUPLICATE_OPTIONAL_USERNAME.getCode())));
    }

  }

  @Nested
  class GET_사용자_단일_조회_API_테스트 {

    static final String PATH = "/api/users/{id}";

    @Test
    void 단일_조회를_성공하고_200_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      UserProfileResponse expected = UserProfileResponse.builder()
          .id(userId)
          .nickname(nickname)
          .build();

      given(userService.findById(anyLong())).willReturn(expected);

      // when
      ResultActions actions = mockMvc.perform(get(PATH, userId));

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
      ResultActions actions = mockMvc.perform(get(PATH, userId));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code").value(UserErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message").value(UserErrorCode.ID_NOT_EXISTING.getMessage()));
    }

  }

  @Nested
  class GET_사용자_검색_API_테스트 {

    @ParameterizedTest
    @ValueSource(strings = {"NICKNAME", "EMAIL", "OPTIONAL_USERNAME"})
    void 사용자_검색을_성공하고_200_OK를_반환한다(String searchType) throws Exception {
      // given
      UserProfileResponse profile = createUserProfile(nickname);
      List<UserProfileResponse> expected = List.of(profile);

      given(userService.search(anyString(), any(UserSearchType.class))).willReturn(expected);

      // when
      ResultActions actions = mockMvc.perform(
          get("/api/users")
              .queryParam("keyword", nickname)
              .queryParam("searchType", searchType)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(expected.size()))
          .andExpect(jsonPath("$[0].id").value(profile.id()))
          .andExpect(jsonPath("$[0].nickname").value(profile.nickname()))
          .andExpect(jsonPath("$[0].introduction").value(profile.introduction()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid search type", "nickname"})
    void 유효하지_않은_검색_유형이_입력된_경우_400_Bad_Request_응답을_반환한다(String searchType) throws Exception {
      // given
      String keyword = faker.lorem()
          .word();

      // when
      ResultActions actions = mockMvc.perform(
          get("/api/users")
              .queryParam("keyword", keyword)
              .queryParam("searchType", searchType)
              .contentType(MediaType.APPLICATION_JSON)
      );

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message")
              .value(containsString("searchType의 형식이 유효하지 않습니다.")));
    }

    private UserProfileResponse createUserProfile(String nickname) {
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);
      String introduction = faker.book()
          .title();

      return new UserProfileResponse(id, nickname, introduction);
    }

  }

  @Nested
  class PATCH_이메일_변경_API_테스트 {

    static final String PATH = "/api/users/{id}/email";

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

    @Test
    void 이메일_변경_성공하면_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong();
      String token = createBearerToken(userId);

      String newEmail = faker.internet()
          .emailAddress();
      UpdateEmailRequest request = new UpdateEmailRequest(newEmail);
      UpdateEmailResponse response = new UpdateEmailResponse(newEmail);

      given(userService.updateEmail(anyLong(), any(UpdateEmailRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.email").value(request.email()));
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
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

  }

  @Nested
  class PATCH_비밀번호_변경_API_테스트 {

    static final String PATH = "/api/users/{id}/password";

    static Stream<Arguments> provideUpdatePasswordRequestAndStrings() {
      String shortPassword = faker.internet()
          .password(3, 7, true, true, true);
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

    @Test
    void 비밀번호_변경_성공하면_200_OK를_반환한다() throws Exception {
      // given
      long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String token = createBearerToken(userId);

      String newPassword = faker.internet()
          .password(8, 40, true, true, true);
      UpdatePasswordRequest request = new UpdatePasswordRequest(newPassword);
      String successMessage = "비밀번호가 성공적으로 변경되었습니다.";
      UpdatePasswordResponse response = new UpdatePasswordResponse(successMessage);

      given(userService.updatePassword(anyLong(), any(UpdatePasswordRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.message").value(successMessage));
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
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

  }

  @Nested
  class PUT_프로필_수정_API_테스트 {

    static final String PATH = "/api/users/{id}/profile";

    String introduction;
    Long userId;
    String token;

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

    @BeforeEach
    void setUp() {
      introduction = faker.book()
          .title();
      userId = faker.random()
          .nextLong();
      token = createBearerToken(userId);
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

      given(userService.updateProfile(anyLong(), any(UpdateProfileRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(put(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(response.id()))
          .andExpect(jsonPath("$.nickname").value(response.nickname()))
          .andExpect(jsonPath("$.introduction").value(response.introduction()));
    }

    @ParameterizedTest(name = "{0}일 때, {2}를 응답한다.")
    @MethodSource("provideUpdateProfileRequestAndStrings")
    void 유효하지_않은_요청이면_400_Bad_Request를_반환한다(
        String cause, UpdateProfileRequest request, String message
    ) throws Exception {
      // when
      ResultActions actions = mockMvc.perform(put(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is(message)));
    }

    @Test
    void 프로필_수정_권한이_없으면_403_Forbidden을_반환한다() throws Exception {
      // given
      UpdateProfileRequest request = new UpdateProfileRequest(nickname, introduction);

      given(userService.updateProfile(anyLong(), any(UpdateProfileRequest.class)))
          .willThrow(new ForbiddenException(UserErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, userId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(UserErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(jsonPath("$.message", is(UserErrorCode.INVALID_AUTHORITY.getMessage())));
    }

  }

  @Nested
  class PATCH_사용자_옵션_토글_API_테스트 {

    static final String PATH = "/api/users/{id}/options";

    long userId;
    String token;

    @BeforeEach
    void setUp() {
      userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      token = createBearerToken(userId);
    }

    @Test
    void 수락한_사람만_팔로우_옵션을_토글하고_200_OK를_반환한다() throws Exception {
      // given
      ToggleOptionResponse response = new ToggleOptionResponse(userId, true);

      given(userService.switchOption(anyLong()))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(userId))
          .andExpect(jsonPath("$.allowFollowsAfterApproval")
              .value(response.allowFollowsAfterApproval()));
    }

    @Test
    void 로그인한_사용자가_요청된_사용자와_다를_경우_403_Forbidden을_반환한다() throws Exception {
      // given
      given(userService.switchOption(anyLong()))
          .willThrow(new ForbiddenException(UserErrorCode.INVALID_AUTHORITY));

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code").value(UserErrorCode.INVALID_AUTHORITY.getCode()))
          .andExpect(
              jsonPath("$.message").value(UserErrorCode.INVALID_AUTHORITY.getMessage()));
    }

    @Test
    void 존재하지_않는_사용자에_대한_요청일_경우_404_Not_Found를_반환한다() throws Exception {
      // given
      given(userService.switchOption(anyLong()))
          .willThrow(new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(patch(PATH, userId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code").value(UserErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message").value(UserErrorCode.ID_NOT_EXISTING.getMessage()));
    }

  }

  @Nested
  class GET_팔로워_팔로이_조회_API_테스트 {

    static final String PATH = "/api/users/{id}/followings";

    Long loginId;
    String token;

    @BeforeEach
    void setUp() {
      loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      token = createBearerToken(loginId);
    }

    @ParameterizedTest(name = "조회 대상: {0}")
    @EnumSource(FollowingSearchType.class)
    void 로그인_사용자의_팔로잉_정보_조회를_성공하고_200_OK를_반환한다(FollowingSearchType searchType)
        throws Exception {
      // given
      long targetId = faker.random()
          .nextLong(Long.MAX_VALUE);
      String anotherNickname = faker.funnyName()
          .name();
      UsersResponse response = new UsersResponse(
          1, List.of(new SimpleUserDto(targetId, anotherNickname)));

      given(userService.findFromFollowings(anyLong(), any(FollowingSearchType.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(get(PATH, loginId)
          .header(AUTHORIZATION, token)
          .param("searchType", searchType.name()));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.counts").value(1))
          .andExpect(jsonPath("$.users.length()").value(1))
          .andExpect(jsonPath("$.users.[0]").value(hasValue(targetId)));
    }

    @ParameterizedTest(name = "조회 대상: {0}")
    @EnumSource(FollowingSearchType.class)
    void 로그인한_사용자와_요청의_사용자가_다르면_403_Forbidden을_반환한다(FollowingSearchType searchType)
        throws Exception {
      // given
      long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ResultActions actions = mockMvc.perform(get(PATH, invalidId)
          .header(AUTHORIZATION, token)
          .param("searchType", searchType.name()));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code").value(UserErrorCode.INVALID_AUTHORITY.getCode()))
          .andExpect(jsonPath("$.message").value(UserErrorCode.INVALID_AUTHORITY.getMessage()));
    }

    @ParameterizedTest(name = "조회 대상: {0}")
    @EnumSource(FollowingSearchType.class)
    void 존재하지_않는_사용자일_경우_404_Not_Found를_반환한다(FollowingSearchType searchType)
        throws Exception {
      // given
      given(userService.findFromFollowings(anyLong(), any(FollowingSearchType.class)))
          .willThrow(new DataNotFoundException(UserErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(get(PATH, loginId)
          .header(AUTHORIZATION, token)
          .param("searchType", searchType.name()));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code").value(UserErrorCode.ID_NOT_EXISTING.getCode()))
          .andExpect(jsonPath("$.message").value(UserErrorCode.ID_NOT_EXISTING.getMessage()));
    }

  }

  @Nested
  class POST_팔로잉_신청_API_테스트 {

    static final String PATH = "/api/users/{id}/followings";

    Long loginId;
    Long followeeId;
    String token;

    @BeforeEach
    void setUp() {
      loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      followeeId = faker.random()
          .nextLong(Long.MAX_VALUE);
      token = createBearerToken(loginId);
    }

    @Test
    void 팔로잉_신청을_성공하고_200_OK를_반환한다() throws Exception {
      // given
      FollowRequest request = new FollowRequest(followeeId);
      FollowingResponse response = FollowingResponse.builder()
          .id(1L)
          .followerId(loginId)
          .followeeId(followeeId)
          .build();

      given(followingService.create(anyLong(), any(FollowRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(post(PATH, loginId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isCreated())
          .andExpect(header().string("location", is("/api/users/" + loginId + "/followings")));
    }

    @Test
    void 로그인한_사용자가_없으면_401_Unauthorized를_반환한다() throws Exception {
      // given
      FollowRequest request = new FollowRequest(followeeId);

      // when
      ResultActions actions = mockMvc.perform(post(PATH, loginId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isUnauthorized())
          .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    void 팔로잉_대상_사용자가_존재하지_않으면_404_Not_Found를_반환한다() throws Exception {
      // given
      FollowRequest request = new FollowRequest(followeeId);

      given(followingService.create(anyLong(), any(FollowRequest.class)))
          .willThrow(new DataNotFoundException(FollowingErrorCode.FOLLOWEE_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(post(PATH, loginId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.FOLLOWEE_NOT_EXISTING.getCode())))
          .andExpect(
              jsonPath("$.message", is(FollowingErrorCode.FOLLOWEE_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class PUT_팔로잉_수정_API_테스트 {

    static final String PATH = "/api/users/{id}/followings/{followingId}";

    long loginId;
    long followingId;
    String token;

    @BeforeEach
    void setUp() {
      loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      followingId = faker.random()
          .nextLong(Long.MAX_VALUE);
      token = createBearerToken(loginId);
    }

    @Test
    void 팔로잉_상태_변경을_성공하고_200_OK를_반환한다() throws Exception {
      // given
      long followeeId = faker.random()
          .nextLong();
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);
      FollowingResponse response = new FollowingResponse(
          followingId, loginId, followeeId, FollowingStatus.FOLLOWING);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willReturn(response);

      // when
      ResultActions actions = mockMvc.perform(put(PATH, loginId, followingId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isOk())
          .andExpect(jsonPath("$.id", is(followingId)))
          .andExpect(jsonPath("$.status", is(FollowingStatus.FOLLOWING.name())));
    }

    @Test
    void 요청시_팔로잉_상태가_누락되면_400_Bad_Request를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(null);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willReturn(new FollowingResponse(followingId, null, null, null));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, loginId, followingId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.[0].code", is(1)))
          .andExpect(jsonPath("$.[0].message", is("요청할 팔로잉 상태는 필수값입니다.")));
    }

    @Test
    void 요청_상태로_수정_시도_시_400_Bad_Request를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.REQUESTED);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willThrow(new BadRequestException(FollowingErrorCode.REQUEST_UNAVAILABLE));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, loginId, followingId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.REQUEST_UNAVAILABLE.getCode())))
          .andExpect(
              jsonPath("$.message", is(FollowingErrorCode.REQUEST_UNAVAILABLE.getMessage())));
    }

    @Test
    void 로그인한_사용자가_없으면_401_Unauthorized를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.REQUESTED);

      // when
      ResultActions actions = mockMvc.perform(put(PATH, loginId, followingId)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isUnauthorized())
          .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    void 로그인한_사용자와_받은_팔로잉이_아니면_403_Forbidden을_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willThrow(new ForbiddenException(FollowingErrorCode.NOT_ENGAGED_USER));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, loginId, followingId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.NOT_ENGAGED_USER.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.NOT_ENGAGED_USER.getMessage())));
    }

    @Test
    void 존재하지_않는_팔로잉_아이디면_404_Not_Found를_반환한다() throws Exception {
      // given
      UpdateFollowingRequest request = new UpdateFollowingRequest(FollowingStatus.FOLLOWING);

      given(followingService.updateStatus(anyLong(), anyLong(), any(UpdateFollowingRequest.class)))
          .willThrow(new DataNotFoundException(FollowingErrorCode.ID_NOT_EXISTING));

      // when
      ResultActions actions = mockMvc.perform(put(PATH, loginId, followingId)
          .header(AUTHORIZATION, token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)));

      // then
      actions.andExpect(status().isNotFound())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.ID_NOT_EXISTING.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.ID_NOT_EXISTING.getMessage())));
    }

  }

  @Nested
  class DELETE_팔로잉_삭제_API_테스트 {

    static final String PATH = "/api/users/{id}/followings/{followingId}";

    long loginId;
    long followingId;
    String token;

    @BeforeEach
    void setUp() {
      loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      followingId = faker.random()
          .nextLong(Long.MAX_VALUE);
      token = createBearerToken(loginId);
    }

    @Test
    void 팔로잉_삭제를_성공하고_204_No_Content를_반환한다() throws Exception {
      // given
      willDoNothing().given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, loginId, followingId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isNoContent());
    }

    @Test
    void 로그인한_사용자가_없으면_401_Unauthorized를_반환한다() throws Exception {
      // given
      willDoNothing().given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, loginId, followingId)
          .contentType(MediaType.APPLICATION_JSON));

      // then
      actions.andExpect(status().isUnauthorized())
          .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"));
    }

    @Test
    void 로그인한_사용자와_요청의_사용자가_다를_경우_403_Forbidden을_반환한다() throws Exception {
      // given
      willDoNothing().given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, followingId, followingId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(UserErrorCode.INVALID_AUTHORITY.getCode())))
          .andExpect(jsonPath("$.message", is(UserErrorCode.INVALID_AUTHORITY.getMessage())));
    }

    @Test
    void 로그인한_사용자와_팔로워가_다를_경우_403_Forbidden을_반환한다() throws Exception {
      // given
      willThrow(new ForbiddenException(FollowingErrorCode.WRONG_OWNER))
          .given(followingService)
          .delete(anyLong(), anyLong());

      // when
      ResultActions actions = mockMvc.perform(delete(PATH, loginId, followingId)
          .header(AUTHORIZATION, token));

      // then
      actions.andExpect(status().isForbidden())
          .andExpect(jsonPath("$.code", is(FollowingErrorCode.WRONG_OWNER.getCode())))
          .andExpect(jsonPath("$.message", is(FollowingErrorCode.WRONG_OWNER.getMessage())));
    }

  }

}
