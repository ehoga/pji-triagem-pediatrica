package com.pji.triagem.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaResponseDTO <T> {
    private String type;           // ex: "record.notification"
    private String version;        // ex: "v1"
    private String correlationId;  // tracing
    private Instant occurredAt;
    private T payload;
}
