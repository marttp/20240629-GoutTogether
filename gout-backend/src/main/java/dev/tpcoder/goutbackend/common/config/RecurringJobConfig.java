package dev.tpcoder.goutbackend.common.config;

import java.time.Instant;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.annotations.Recurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import dev.tpcoder.goutbackend.auth.service.TokenService;

@Configuration
public class RecurringJobConfig {

    private static final String CRON_EXPRESSION = "*/2 * * * *";
    
    private final Logger logger = LoggerFactory.getLogger(RecurringJobConfig.class);
    private final TokenService tokenService;

    public RecurringJobConfig(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Recurring(id = "refresh-token-cleanup", cron = CRON_EXPRESSION)
    @Job(name = "Recurring job for cleanup expired refresh token")
    public void cleanupExpiredRefreshToken() {
        var now = Instant.now();
        logger.info("Started cleanup at {}", now);
        tokenService.cleanupRefreshTokenThatNotExpired();
    }
}
