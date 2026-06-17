package com.dypatil.bfhl.service;

import com.dypatil.bfhl.dto.BfhlRequest;
import com.dypatil.bfhl.dto.BfhlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BfhlServiceImplTest {

    @InjectMocks
    private BfhlServiceImpl bfhlService;

    @Test
    @DisplayName("Example 1: Basic mixed input")
    void testExample1() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("A", "1", "22", "$", "B", "7"));

        BfhlResponse response = bfhlService.processData(request, "REQ-1001");

        assertTrue(response.isSuccess());
        assertEquals("REQ-1001", response.getRequestId());
        assertEquals(List.of("1", "7"), response.getOddNumbers());
        assertEquals(List.of("22"), response.getEvenNumbers());
        assertEquals(List.of("A", "B"), response.getAlphabets());
        assertEquals(List.of("$"), response.getSpecialCharacters());
        assertEquals("30", response.getSum());
        assertEquals("22", response.getLargestNumber());
        assertEquals("1", response.getSmallestNumber());
        assertEquals(2, response.getAlphabetCount());
        assertEquals(3, response.getNumberCount());
        assertEquals(1, response.getSpecialCharacterCount());
        assertFalse(response.isContainsDuplicates());
    }

    @Test
    @DisplayName("Example 2: With alphanumeric strings")
    void testExample2() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("A1B2", "100", "#", "Test123", "Z", "55"));

        BfhlResponse response = bfhlService.processData(request, "REQ-1002");

        assertTrue(response.isSuccess());
        assertEquals("REQ-1002", response.getRequestId());
        assertEquals(List.of("55"), response.getOddNumbers());
        assertEquals(List.of("100"), response.getEvenNumbers());
        assertEquals(7, response.getAlphabetCount());
        assertEquals(List.of("A", "B", "T", "E", "S", "T", "Z"), response.getAlphabets());
        assertEquals(List.of("#"), response.getSpecialCharacters());
        assertEquals("155", response.getSum());
        assertEquals("100", response.getLargestNumber());
        assertEquals("55", response.getSmallestNumber());
        assertEquals(2, response.getNumberCount());
        assertEquals(1, response.getSpecialCharacterCount());
        assertFalse(response.isContainsDuplicates());
    }

    @Test
    @DisplayName("Example 3: With duplicates, nulls, empty strings")
    void testExample3() {
        BfhlRequest request = new BfhlRequest();
        List<String> data = new ArrayList<>();
        data.add("10"); data.add("10"); data.add("A"); data.add("A");
        data.add(""); data.add(null); data.add("&"); data.add("5");
        request.setData(data);

        BfhlResponse response = bfhlService.processData(request, "REQ-1003");

        assertTrue(response.isSuccess());
        assertEquals("REQ-1003", response.getRequestId());
        assertEquals(List.of("5"), response.getOddNumbers());
        assertEquals(List.of("10"), response.getEvenNumbers());
        assertEquals(List.of("A"), response.getAlphabets());
        assertEquals(List.of("&"), response.getSpecialCharacters());
        assertEquals("15", response.getSum());
        assertEquals("10", response.getLargestNumber());
        assertEquals("5", response.getSmallestNumber());
        assertEquals(1, response.getAlphabetCount());
        assertEquals(2, response.getNumberCount());
        assertEquals(1, response.getSpecialCharacterCount());
        assertTrue(response.isContainsDuplicates());
        assertEquals(4, response.getUniqueElementCount());
    }

    @Test
    @DisplayName("Example 4: With negative and decimal numbers")
    void testExample4() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("-10", "25.5", "-100.75", "B", "@", "5", "A9"));

        BfhlResponse response = bfhlService.processData(request, "REQ-1004");

        assertTrue(response.isSuccess());
        assertEquals("REQ-1004", response.getRequestId());
        assertEquals(List.of("5"), response.getOddNumbers());
        assertEquals(List.of("-10"), response.getEvenNumbers());
        assertTrue(response.getAlphabets().contains("B"));
        assertTrue(response.getAlphabets().contains("A"));
        assertEquals(List.of("@"), response.getSpecialCharacters());
        assertEquals("-80.25", response.getSum());
        assertEquals("25.5", response.getLargestNumber());
        assertEquals("-100.75", response.getSmallestNumber());
        assertEquals(2, response.getAlphabetCount());
        assertEquals(4, response.getNumberCount());
        assertEquals(1, response.getSpecialCharacterCount());
        assertFalse(response.isContainsDuplicates());
    }

    @Test
    @DisplayName("Example 5: Complex mixed input")
    void testExample5() {
        BfhlRequest request = new BfhlRequest();
        List<String> data = new ArrayList<>();
        data.add("ABC"); data.add("123"); data.add("A1B2"); data.add("$"); data.add("%");
        data.add("-50"); data.add("0"); data.add("xyz"); data.add(""); data.add(null);
        data.add("999"); data.add("Test99"); data.add("&");
        request.setData(data);

        BfhlResponse response = bfhlService.processData(request, "REQ-1005");

        assertTrue(response.isSuccess());
        assertEquals("REQ-1005", response.getRequestId());
        assertEquals(List.of("123", "999"), response.getOddNumbers());
        assertEquals(List.of("-50", "0"), response.getEvenNumbers());
        assertTrue(response.getAlphabets().contains("ABC"));
        assertTrue(response.getAlphabets().contains("XYZ"));
        assertEquals(List.of("$", "%", "&"), response.getSpecialCharacters());
        assertEquals("1072", response.getSum());
        assertEquals("999", response.getLargestNumber());
        assertEquals("-50", response.getSmallestNumber());
        assertEquals(3, response.getVowelCount());
        assertFalse(response.isContainsDuplicates());
    }

    @Test
    @DisplayName("Null data should not cause NPE")
    void testNullData() {
        BfhlRequest request = new BfhlRequest();
        request.setData(null);

        BfhlResponse response = bfhlService.processData(request, "REQ-NULL");

        assertTrue(response.isSuccess());
        assertEquals(0, response.getNumberCount());
        assertEquals(0, response.getAlphabetCount());
        assertEquals(0, response.getSpecialCharacterCount());
    }

    @Test
    @DisplayName("Empty data should return empty response")
    void testEmptyData() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of());

        BfhlResponse response = bfhlService.processData(request, "REQ-EMPTY");

        assertTrue(response.isSuccess());
        assertEquals(0, response.getNumberCount());
        assertEquals(0, response.getAlphabetCount());
    }

    @Test
    @DisplayName("Whitespace-only strings should be ignored")
    void testWhitespaceOnly() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("   ", "\t", "\n", "A"));

        BfhlResponse response = bfhlService.processData(request, "REQ-WS");

        assertEquals(1, response.getUniqueElementCount());
        assertEquals(List.of("A"), response.getAlphabets());
    }

    @Test
    @DisplayName("Categorize identifies numbers correctly")
    void testCategorizeNumber() {
        assertEquals("number", bfhlService.categorize("123"));
        assertEquals("number", bfhlService.categorize("-50"));
        assertEquals("number", bfhlService.categorize("25.5"));
        assertEquals("number", bfhlService.categorize("-100.75"));
        assertEquals("number", bfhlService.categorize("0"));
        assertEquals("number", bfhlService.categorize("999"));
    }

    @Test
    @DisplayName("Categorize identifies alphabetic strings correctly")
    void testCategorizeAlphabetic() {
        assertEquals("alphabetic", bfhlService.categorize("ABC"));
        assertEquals("alphabetic", bfhlService.categorize("xyz"));
        assertEquals("alphabetic", bfhlService.categorize("A"));
        assertEquals("alphabetic", bfhlService.categorize("Z"));
    }

    @Test
    @DisplayName("Categorize identifies alphanumeric strings correctly")
    void testCategorizeAlphanumeric() {
        assertEquals("alphanumeric", bfhlService.categorize("A1B2"));
        assertEquals("alphanumeric", bfhlService.categorize("Test123"));
        assertEquals("alphanumeric", bfhlService.categorize("Test99"));
        assertEquals("alphanumeric", bfhlService.categorize("A9"));
    }

    @Test
    @DisplayName("Categorize identifies special characters correctly")
    void testCategorizeSpecial() {
        assertEquals("special", bfhlService.categorize("$"));
        assertEquals("special", bfhlService.categorize("#"));
        assertEquals("special", bfhlService.categorize("@"));
        assertEquals("special", bfhlService.categorize("&"));
        assertEquals("special", bfhlService.categorize("%"));
    }

    @Test
    @DisplayName("Processing time should be positive")
    void testProcessingTime() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("1", "2", "3"));

        BfhlResponse response = bfhlService.processData(request, "REQ-TIME");

        assertTrue(response.getProcessingTimeMs() >= 0);
    }

    @Test
    @DisplayName("Sorted numbers should be in ascending order")
    void testSortedNumbers() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("100", "-50", "25.5", "0"));

        BfhlResponse response = bfhlService.processData(request, "REQ-SORT");

        assertEquals(List.of("-50", "0", "25.5", "100"), response.getSortedNumbers());
    }

    @Test
    @DisplayName("Summary should reflect total, valid, invalid counts")
    void testSummary() {
        BfhlRequest request = new BfhlRequest();
        List<String> data = new ArrayList<>();
        data.add("A"); data.add("1"); data.add(""); data.add(null); data.add("B");
        request.setData(data);

        BfhlResponse response = bfhlService.processData(request, "REQ-SUM");

        assertEquals(5, response.getSummary().getTotalElementsReceived());
        assertEquals(3, response.getSummary().getValidElementsProcessed());
        assertEquals(2, response.getSummary().getInvalidElementsIgnored());
    }

    @Test
    @DisplayName("Alphabet frequency should count letter occurrences")
    void testAlphabetFrequency() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("A", "B", "C", "Test99"));

        BfhlResponse response = bfhlService.processData(request, "REQ-FREQ");

        assertEquals(1, response.getAlphabetFrequency().get("A"));
        assertEquals(1, response.getAlphabetFrequency().get("B"));
        assertEquals(1, response.getAlphabetFrequency().get("C"));
        assertEquals(1, response.getAlphabetFrequency().get("E"));
        assertEquals(1, response.getAlphabetFrequency().get("S"));
        assertEquals(2, response.getAlphabetFrequency().get("T"));
    }

    @Test
    @DisplayName("Longest and shortest alphabetic values")
    void testLongestAndShortest() {
        BfhlRequest request = new BfhlRequest();
        request.setData(List.of("ABC", "Z", "Test99"));

        BfhlResponse response = bfhlService.processData(request, "REQ-LEN");

        assertEquals("ABC", response.getLongestAlphabeticValue());
        assertEquals("Z", response.getShortestAlphabeticValue());
    }
}
