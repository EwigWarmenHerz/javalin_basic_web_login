package org.david.domain.models;

public record ResponseModel<T>(T response, Errors errors) {}
