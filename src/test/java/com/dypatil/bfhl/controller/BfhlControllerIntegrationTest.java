package com.dypatil.bfhl.controller;

import com.dypatil.bfhl.dto.BfhlRequest;
import com.dypatil.bfhl.dto.BfhlResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BfhlControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testPostBfhlWithValidData() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("A", "1", "22", "$", "B", "7"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", "REQ-1001");

        HttpEntity<BfhlRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<BfhlResponse> response = restTemplate.exchange(
                "/bfhl", HttpMethod.POST, entity, BfhlResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BfhlResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("REQ-1001", body.getRequestId());
    }

    @Test
    void testPostBfhlWithoutRequestId() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("A", "1"));

        HttpEntity<BfhlRequest> entity = new HttpEntity<>(request);

        ResponseEntity<BfhlResponse> response = restTemplate.exchange(
                "/bfhl", HttpMethod.POST, entity, BfhlResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BfhlResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("N/A", body.getRequestId());
    }

    @Test
    void testPostBfhlWithNullData() {
        BfhlRequest request = new BfhlRequest();
        request.setData(null);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", "REQ-NULL");

        HttpEntity<BfhlRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/bfhl", HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostBfhlProcessingTime() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("1", "2", "3", "A", "B", "$"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", "REQ-TIME");

        HttpEntity<BfhlRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<BfhlResponse> response = restTemplate.exchange(
                "/bfhl", HttpMethod.POST, entity, BfhlResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        BfhlResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.getProcessingTimeMs() >= 0);
    }
}
