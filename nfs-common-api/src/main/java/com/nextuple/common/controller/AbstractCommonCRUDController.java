package com.nextuple.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class AbstractCommonCRUDController<I, O> {
    protected abstract Logger log();

    public Function<O, ResponseEntity<O>> createResponse(I request) {
        return o -> {
            if (o == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(o);
        };
    }


    protected Function<O, ResponseEntity<O>> updateResponse(I request) {
        return o -> {
            if (o == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(o);
        };
    }

    protected Function<O, ResponseEntity<Void>> deleteResponse(I request) {
        return o -> {
            if (o == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        };
    }

    protected Function<O, ResponseEntity<O>> readResponse(I request) {
        return o -> {
            if (o == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(o);
        };
    }

    protected Function<List<O>, ResponseEntity<List<O>>> readAllResponse(I request) {
        return o -> {
            if (o == null || o.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(o);
        };
    }
}
