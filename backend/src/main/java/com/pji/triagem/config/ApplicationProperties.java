package com.pji.triagem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "triagem")
public class ApplicationProperties {

    private Micro micro;
    private Token token;
    private Kafka kafka;

    @Data
    public static class Kafka {
        private String topicSms;
        private String topicEmail;
    }
    @Data
    public static class Micro {
        private String management;
        private String client;
    }

    @Data
    public static class Token {
        private Access access;
        private Refresh refresh;

        @Data
        public static class Access {
            private long expiration;
        }
        @Data
        public static class Refresh {
            private long expiration;
        }
    }
}
