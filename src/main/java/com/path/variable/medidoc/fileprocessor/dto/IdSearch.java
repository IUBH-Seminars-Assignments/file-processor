package com.path.variable.medidoc.fileprocessor.dto;

import org.springframework.data.domain.PageRequest;

public record IdSearch(Integer page, Integer pageSize) {

    public PageRequest toPageRequest() {
        return PageRequest.of(page(), pageSize());
    }
}
