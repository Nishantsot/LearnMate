package learm.learn.Controller;

import learm.learn.Dto.AiChatRequest;
import learm.learn.Dto.AiChatResponse;
import learm.learn.Services.AiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student/ai")
public class AiController {

    private final AiService aiService;

    public AiController(
            AiService aiService
    ) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(
            @RequestBody AiChatRequest request
    ) {

        String answer =
                aiService.askAi(
                        request.getMessage()
                );

        return ResponseEntity.ok(
                new AiChatResponse(answer)
        );
    }
}
