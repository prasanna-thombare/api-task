package com.dypatil.bfhl.controller;

import com.dypatil.bfhl.dto.BfhlRequest;
import com.dypatil.bfhl.dto.BfhlResponse;
import com.dypatil.bfhl.service.BfhlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bfhl")
public class BfhlController {

    private final BfhlService bfhlService;

    public BfhlController(BfhlService bfhlService) {
        this.bfhlService = bfhlService;
    }

    @PostMapping
    public ResponseEntity<BfhlResponse> processData(
            @Valid @RequestBody BfhlRequest request,
            @RequestHeader(value = "X-Request-Id", required = false, defaultValue = "N/A") String requestId) {
        BfhlResponse response = bfhlService.processData(request, requestId);
        return ResponseEntity.ok(response);
    }
}
