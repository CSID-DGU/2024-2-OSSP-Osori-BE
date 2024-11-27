package dongguk.osori.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/{prefix}/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@PathVariable String prefix, @RequestBody Map<String, String> requestBody) {
        String fileName = requestBody.get("imageName");
        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.badRequest().body("The 'imageName' field is required.");
        }

        try {
            String presignedUrl = fileService.getPreSignedUrl(prefix, fileName);
            return ResponseEntity.ok(presignedUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate presigned URL: " + e.getMessage());
        }
    }

}
