package com.sebxx66.yogurtmaker.exception;

/**
 * La operación no es válida en el estado actual del recurso
 * (por ejemplo, refrigerar un lote que aún no termina de incubar).
 * Se responde con 409 Conflict.
 */
public class InvalidStateException extends RuntimeException {

    public InvalidStateException(String message) {
        super(message);
    }
}
