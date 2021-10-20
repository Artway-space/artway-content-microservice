package space.artway.artwaycontent.service;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ContentType {
    MP3("audio/mpeg"),
    MP4("video/mp4"),
    PNG("image/png");

    private String code;

    @SneakyThrows
    public static ContentType findValueByCode(String code) {
        return Arrays.stream(ContentType.values()).
                filter(contentType -> code.equals(contentType.getCode()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Content " + code + " type not supported"));
    }
}
