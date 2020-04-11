package com.etnetera.hr.rest.dto.container;


import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * Envelope for input DTOs. Represents JSON request.
 */
public class InputContainer<T> {

    public static final String EMPTY_INPUT_LIST_MESSAGE = "List of inputs can not be empty";

    @Valid
    @NotEmpty(message = EMPTY_INPUT_LIST_MESSAGE)
    private List<T> inputs = new ArrayList<>();

    public List<T> getInputs() {
        return inputs;
    }

    public void setInputs(List<T> inputs) {
        this.inputs = inputs;
    }
}

