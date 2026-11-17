package com.sebxx66.yogurtmaker.exception;

/** Ya existe un recurso con el mismo identificador natural. Se responde con 409 Conflict. */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
