package com.olx.inventoryManagementSystem.exceptions;

import lombok.*;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
@EqualsAndHashCode
public class ExceptionResponse {

    String errorCode;
    String errorMessage;

}
