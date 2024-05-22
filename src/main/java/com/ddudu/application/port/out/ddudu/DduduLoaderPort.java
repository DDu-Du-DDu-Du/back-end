package com.ddudu.application.port.out.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;

public interface DduduLoaderPort {

  Ddudu getDduduOrElseThrow(Long id, String message);

}
