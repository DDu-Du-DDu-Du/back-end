package com.ddudu.presentation.api.controller;

import com.ddudu.application.dto.repeated_ddudu.requset.CreateRepeatitiveDdudu;
import com.ddudu.presentation.api.annotation.Login;
import com.ddudu.presentation.api.common.dto.response.IdResponse;
import com.ddudu.presentation.api.doc.RepeatitiveDduduControllerDoc;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repeatitive-ddudus")
@RequiredArgsConstructor
public class RepeatitiveDduduController implements RepeatitiveDduduControllerDoc {

  @PostMapping
  public ResponseEntity<IdResponse> create(
      @Login
      Long loginId,
      @RequestBody
      @Valid
      CreateRepeatitiveDdudu request
  ) {

    return null;
  }

}
