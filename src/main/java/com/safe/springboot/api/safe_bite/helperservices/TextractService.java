package com.safe.springboot.api.safe_bite.helperservices;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.text.Normalizer;

@Service
public class TextractService {
    private final TextractClient textractClient;

    public TextractService(TextractClient textractClient) {
        this.textractClient = textractClient;
    }

    public String extractText(InputStream documentStream) {
        AnalyzeDocumentRequest request = AnalyzeDocumentRequest.builder()
                .document(Document.builder()
                        .bytes(SdkBytes.fromInputStream(documentStream))
                        .build())
                .featureTypes(FeatureType.LAYOUT)
                .build();

        AnalyzeDocumentResponse response = textractClient.analyzeDocument(request);

        List<Block> lines = response.blocks().stream()
                .filter(block -> block.blockType() == BlockType.LINE)
                .toList();

        String fullText = lines.stream()
                .map(Block::text)
                .collect(Collectors.joining(" "));

        return extractIngredientsTextFromOcr(fullText);
    }

    private String extractIngredientsTextFromOcr(String text) {
        String normalizedText = normalize(text);
        int startIndex = normalizedText.indexOf("ingrediente");

        if (startIndex == -1) {
            return "";
        }

        String afterIngredient = normalizedText.substring(startIndex);

        int endIndex = afterIngredient.indexOf(".");
        if (endIndex != -1) {
            return afterIngredient.substring(0, endIndex + 1).trim();
        }

        return afterIngredient.trim();
    }

    private String normalize(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }
}
