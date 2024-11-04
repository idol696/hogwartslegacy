package ru.prostostudia.hogwartslegacy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FacultyContainStudentException extends RuntimeException {
    public FacultyContainStudentException() {
        super("FacultyNotEmpty");
    }
}
