package com.sebxx66.yogurtmaker.exception;

/**
 * Error de una regla de negocio (datos que no tienen sentido para el proceso).
 * Se responde con 422 Unprocessable Content.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
