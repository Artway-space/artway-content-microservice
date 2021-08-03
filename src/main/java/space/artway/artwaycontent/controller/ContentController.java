package space.artway.artwaycontent.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.artway.artwaycontent.service.ContentService;
import space.artway.artwaycontent.service.dto.ContentDto;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @GetMapping("getAllByAuthorId")
    public ResponseEntity<Collection<ContentDto>> getAllAuthorContent(@RequestParam Long authorId){
        List<ContentDto> allAuthorContent = contentService.getAllAuthorContent(authorId);
        return ResponseEntity.ok(allAuthorContent);
    }

    @GetMapping("get")
    public ResponseEntity<ContentDto> getContentByNameAndAuthorId(@RequestParam String name, Long authorId) throws NotFoundException {
        ContentDto contentByNameAndAuthorId = contentService.getContentByNameAndAuthorId(name, authorId);
        return ResponseEntity.ok(contentByNameAndAuthorId);
    }

}
