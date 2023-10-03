package com.bilgeadam.utility;

import com.bilgeadam.repository.entity.BaseEntity;

public class Constants {
    public static BaseEntity getBaseEntity(){
        return BaseEntity.builder()
                .createat(System.currentTimeMillis())
                .updateat(System.currentTimeMillis())
                .state(true)
                .build();
    }
}
