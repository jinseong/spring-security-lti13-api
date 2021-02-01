package uk.ac.ox.ctl.lti13.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class CanvasAPIController {

	@GetMapping("/courses")
	public Object courses() {
		Map<String, Object> result = new HashMap<String, Object>();
		String apiUrl = "https://jsjeon.lineedu.kr/api/v1"; // Canvas API url
		String oauthToken = "T6AsESHqR30kt6WG2MqDdzzmR2C5ZI6msMSy6wPloYp9316RN7q26brLJHEnHo5b"; // 발급받은 access_token
		int userId = 1;
		
		try {
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setConnectTimeout(3000); // connect 타임아웃을 3초로 설정
			factory.setReadTimeout(3000); // read 타임아웃을 3초로 설정
			
			RestTemplate restTemplate = new RestTemplate(factory);
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + oauthToken); // HTTP 헤더에 access_token 추가
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponents uri = UriComponentsBuilder.fromHttpUrl(apiUrl + "/users/" + userId + "/courses").build(); // 요청할 Canvas API의 최종 URI
			
			// API 요청
			ResponseEntity<String> resultMap = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
			
			result.put("statusCode", resultMap.getStatusCodeValue()); // HTTP status code. 화면 출력엔 포함 X
			result.put("body", resultMap.getBody());
		} catch (RestClientException e) {
			e.printStackTrace();
		}
		
		return result.get("body");
	}
}
