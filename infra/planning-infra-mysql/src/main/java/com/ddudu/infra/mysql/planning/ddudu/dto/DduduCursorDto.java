package com.ddudu.infra.mysql.planning.ddudu.dto;

import com.ddudu.application.dto.ddudu.SimpleDduduSearchDto;

public record DduduCursorDto(String cursor, SimpleDduduSearchDto ddudu) {

}
