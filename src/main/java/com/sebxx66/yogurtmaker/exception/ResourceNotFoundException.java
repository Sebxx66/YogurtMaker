package com.sebxx66.yogurtmaker.exception;

/** El recurso pedido no existe. Se responde con 404 Not Found. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " con id " + id + " no existe");
    }
}
