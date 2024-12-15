package ru.prostostudia.hogwartslegacy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileUploadErrorException extends RuntimeException {
    public FileUploadErrorException() {
        super("FileUploadException");
    }
}
