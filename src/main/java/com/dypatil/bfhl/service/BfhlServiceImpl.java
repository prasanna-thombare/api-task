package com.dypatil.bfhl.service;

import com.dypatil.bfhl.dto.BfhlRequest;
import com.dypatil.bfhl.dto.BfhlResponse;
import com.dypatil.bfhl.dto.BfhlResponse.Summary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BfhlServiceImpl implements BfhlService {

    private static final Logger log = LoggerFactory.getLogger(BfhlServiceImpl.class);
    private static final Set<String> VOWELS = Set.of("A", "E", "I", "O", "U");

    @Override
    public BfhlResponse processData(BfhlRequest request, String requestId) {
        long startTime = System.currentTimeMillis();

        List<String> rawData = request.getData();
        int totalReceived = rawData != null ? rawData.size() : 0;

        List<String> filtered = new ArrayList<>();
        int invalidCount = 0;
        boolean hasDuplicates = false;
        Set<String> seen = new HashSet<>();

        if (rawData != null) {
            for (String item : rawData) {
                if (item == null || item.trim().isEmpty()) {
                    invalidCount++;
                    continue;
                }
                String trimmed = item.trim();
                if (!seen.add(trimmed)) {
                    hasDuplicates = true;
                    continue;
                }
                filtered.add(trimmed);
            }
        }

        List<String> oddNumbers = new ArrayList<>();
        List<String> evenNumbers = new ArrayList<>();
        List<String> alphabetEntries = new ArrayList<>();
        List<String> specialCharacters = new ArrayList<>();
        List<BigDecimal> allNumbers = new ArrayList<>();
        List<String> allIndividualLetters = new ArrayList<>();
        Map<String, Integer> alphabetFrequency = new LinkedHashMap<>();

        for (String item : filtered) {
            String category = categorize(item);

            switch (category) {
                case "number" -> {
                    BigDecimal num = new BigDecimal(item);
                    allNumbers.add(num);
                    boolean isWhole = num.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
                    if (isWhole && num.remainder(BigDecimal.valueOf(2)).compareTo(BigDecimal.ZERO) == 0) {
                        evenNumbers.add(item);
                    } else if (isWhole) {
                        oddNumbers.add(item);
                    }
                }
                case "alphabetic" -> {
                    String upper = item.toUpperCase();
                    alphabetEntries.add(upper);
                    for (char c : upper.toCharArray()) {
                        String s = String.valueOf(c);
                        allIndividualLetters.add(s);
                        alphabetFrequency.merge(s, 1, Integer::sum);
                    }
                }
                case "alphanumeric" -> {
                    StringBuilder letters = new StringBuilder();
                    for (char c : item.toUpperCase().toCharArray()) {
                        if (Character.isLetter(c)) {
                            letters.append(c);
                        }
                    }
                    String extracted = letters.toString();
                    for (char c : extracted.toCharArray()) {
                        String s = String.valueOf(c);
                        alphabetEntries.add(s);
                        allIndividualLetters.add(s);
                        alphabetFrequency.merge(s, 1, Integer::sum);
                    }
                }
                default -> specialCharacters.add(item);
            }
        }

        BigDecimal sum = allNumbers.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal largest = allNumbers.stream().max(BigDecimal::compareTo).orElse(null);
        BigDecimal smallest = allNumbers.stream().min(BigDecimal::compareTo).orElse(null);

        List<String> sortedNumbers = allNumbers.stream()
                .sorted()
                .map(BigDecimal::toPlainString)
                .collect(Collectors.toList());

        long vowelCount = allIndividualLetters.stream().filter(VOWELS::contains).count();
        long consonantCount = allIndividualLetters.stream().filter(s -> !VOWELS.contains(s)).count();

        String longest = alphabetEntries.stream()
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
        String shortest = alphabetEntries.stream()
                .min(Comparator.comparingInt(String::length))
                .orElse(null);

        BfhlResponse response = new BfhlResponse();
        response.setSuccess(true);
        response.setRequestId(requestId);
        response.setOddNumbers(oddNumbers);
        response.setEvenNumbers(evenNumbers);
        response.setAlphabets(alphabetEntries.isEmpty() ? null : alphabetEntries);
        response.setSpecialCharacters(specialCharacters.isEmpty() ? null : specialCharacters);
        response.setSum(sum.toPlainString());
        response.setLargestNumber(largest != null ? largest.toPlainString() : null);
        response.setSmallestNumber(smallest != null ? smallest.toPlainString() : null);
        response.setAlphabetCount(alphabetEntries.size());
        response.setNumberCount(allNumbers.size());
        response.setSpecialCharacterCount(specialCharacters.size());
        response.setContainsDuplicates(hasDuplicates);
        response.setUniqueElementCount(filtered.size());
        response.setAlphabetFrequency(alphabetFrequency.isEmpty() ? null : alphabetFrequency);
        response.setSortedNumbers(sortedNumbers.isEmpty() ? null : sortedNumbers);
        response.setVowelCount((int) vowelCount);
        response.setConsonantCount((int) consonantCount);
        response.setLongestAlphabeticValue(longest);
        response.setShortestAlphabeticValue(shortest);

        Summary summary = new Summary();
        summary.setTotalElementsReceived(totalReceived);
        summary.setValidElementsProcessed(filtered.size());
        summary.setInvalidElementsIgnored(invalidCount);
        response.setSummary(summary);

        long processingTime = System.currentTimeMillis() - startTime;
        response.setProcessingTimeMs(processingTime);

        log.info("Processed request {}: {} elements, {}ms", requestId, totalReceived, processingTime);

        return response;
    }

    String categorize(String item) {
        if (item == null || item.isEmpty()) {
            return "invalid";
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasOther = false;
        boolean hasSign = false;
        boolean hasDecimal = false;

        for (int i = 0; i < item.length(); i++) {
            char c = item.charAt(i);
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (c == '-' || c == '+') {
                hasSign = true;
            } else if (c == '.') {
                hasDecimal = true;
            } else {
                hasOther = true;
            }
        }

        if (!hasLetter && !hasOther && (hasDigit || (hasSign && hasDigit))) {
            try {
                new BigDecimal(item);
                return "number";
            } catch (NumberFormatException e) {
                return "special";
            }
        }

        if (!hasDigit && !hasOther && hasLetter) {
            return "alphabetic";
        }

        if (hasLetter && hasDigit && !hasOther) {
            return "alphanumeric";
        }

        return "special";
    }
}
