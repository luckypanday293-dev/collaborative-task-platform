package com.lucky.collabtask;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CollaborativeTaskPlatformIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    TestRestTemplate rest;

    @Test
    void roleBasedAccessAndFilteringWorkEndToEnd() {
        var member = rest.withBasicAuth("member", "member123");
        var admin = rest.withBasicAuth("admin", "admin123");

        ResponseEntity<String> me = member.getForEntity("/api/auth/me", String.class);
        assertThat(me.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(me.getBody()).contains("MEMBER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var createProject = new HttpEntity<>(Map.of(
                "title", "Restricted Project",
                "description", "Created only by admins"), headers);

        assertThat(member.postForEntity("/api/projects", createProject, String.class).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(admin.postForEntity("/api/projects", createProject, String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);

        ResponseEntity<String> filtered = member.getForEntity(
                "/api/assignments?search=dashboard&status=IN_PROGRESS", String.class);
        assertThat(filtered.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(filtered.getBody()).contains("Build project dashboard");
        assertThat(filtered.getBody()).doesNotContain("Verify launch checklist");
    }
}
