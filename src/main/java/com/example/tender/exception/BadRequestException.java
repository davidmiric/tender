package com.example.tender.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@AllArgsConstructor
@Getter
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -2874261308195452417L;
    private String message;

    public BadRequestException() {
        super();
    }

}
