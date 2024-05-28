package com.ddudu.infrastructure.persistence.dto;

import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;

public record DduduCursorDto(String cursor, SimpleDduduSearchDto ddudu) {

}
