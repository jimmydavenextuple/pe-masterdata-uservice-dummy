package com.hbc.common.base;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Date;

class BaseEntityTest {

    @DisplayName("Should construct 2 objects one with no args and one with the specified.")
    @Test
    void constructTest() {
        Date createdDate = new Date();
        Date lastModifiedDate = new Date();

        BaseEntity baseEntity = new BaseEntity() {};
        baseEntity.setCreatedDate(createdDate);
        baseEntity.setLastModifiedDate(lastModifiedDate);

        assertEquals(createdDate,baseEntity.getCreatedDate());
        assertEquals(lastModifiedDate,baseEntity.getLastModifiedDate());
    }
}
