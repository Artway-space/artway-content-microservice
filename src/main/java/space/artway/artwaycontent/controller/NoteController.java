package space.artway.artwaycontent.controller;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.artway.artwaycontent.service.NoteService;
import space.artway.artwaycontent.service.dto.LikeDto;

@RestController
@RequestMapping("/note/")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PutMapping("like")
    @ResponseStatus(HttpStatus.OK)
    public void setLike(@RequestParam("contentId") Long contentId) throws NotFoundException {
        Long userId = 1L;
        noteService.setLike(userId, contentId);
    }

    @PutMapping("dislike")
    @ResponseStatus(HttpStatus.OK)
    public void setDislike(@RequestParam("contentId") Long contentId) {

    }

    @GetMapping("getDislikes")
    public ResponseEntity<LikeDto> getLikes(@RequestParam("contentId") Long contentId) {
        return ResponseEntity.ok(new LikeDto());
    }
}
