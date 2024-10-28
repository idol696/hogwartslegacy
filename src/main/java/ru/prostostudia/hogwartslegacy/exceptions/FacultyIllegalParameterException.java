package ru.prostostudia.hogwartslegacy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FacultyIllegalParameterException extends RuntimeException {
    public FacultyIllegalParameterException(String errorParameter) {
        super("ParameterErrorBy" + errorParameter);
    }
}
