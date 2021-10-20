package space.artway.artwaycontent.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.service.ContentService;
import space.artway.artwaycontent.service.dto.ContentDto;
import space.artway.artwaycontent.service.dto.ShortContentDto;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @GetMapping("getAllByAuthorId")
    public ResponseEntity<Collection<ContentDto>> getAllAuthorContent(HttpServletRequest request, @RequestParam Long authorId) {
        List<ContentDto> allAuthorContent = contentService.getAllAuthorContent(authorId);
        return ResponseEntity.ok(allAuthorContent);
    }

    @GetMapping("get")
    public ResponseEntity<ContentDto> getContentByNameAndAuthorId(@RequestParam String name, Long authorId) throws NotFoundException {
        ContentDto contentByNameAndAuthorId = contentService.getContentByNameAndAuthorId(name, authorId);
        return ResponseEntity.ok(contentByNameAndAuthorId);
    }

    @PostMapping(value = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentDto> uploadFile(@RequestPart(value = "meta") ContentDto content, @RequestPart(value = "data") MultipartFile file) {
        final ContentDto contentDto = contentService.saveContent(file, content);
        return ResponseEntity.ok(contentDto);
    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<ShortContentDto> deleteContent(@PathVariable Long id) throws NotFoundException {
        final ShortContentDto shortContentDto = contentService.putContentInTrashBin(id);
        return ResponseEntity.ok(shortContentDto);
    }

}
