package zxcv.asdf.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class CodeFeedbackService {

    private final RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    public String getCodeFeedback(String code) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> entity = new HttpEntity<>(createJsonPayload(code), headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return extractContentFromResponse(response.getBody());
    }
    public String createJsonPayload(String code) {
        JSONObject root = new JSONObject();
        root.put("model", "gpt-4o");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", code));
        messages.put(new JSONObject().put("role", "user").put("content", " 이 소스코드의 평가와 개선점에 대해 알려줘. 읽기 쉽게 줄구분 잘해서 부탁해."));
        root.put("messages", messages);

        return root.toString();
    }
    public String extractContentFromResponse(String jsonResponse) {
        JSONObject root = new JSONObject(jsonResponse);
        JSONArray choices = root.getJSONArray("choices");
        if (!choices.isEmpty()) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String content = message.getString("content");
            return content;
        }
        return "No content found";
    }
}
