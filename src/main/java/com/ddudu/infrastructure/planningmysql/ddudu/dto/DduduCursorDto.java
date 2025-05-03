package com.ddudu.infrastructure.planningmysql.ddudu.dto;

import com.ddudu.application.planning.ddudu.dto.SimpleDduduSearchDto;

public record DduduCursorDto(String cursor, SimpleDduduSearchDto ddudu) {

}
