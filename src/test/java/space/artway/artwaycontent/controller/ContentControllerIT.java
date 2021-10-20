package space.artway.artwaycontent.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.IOUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import space.artway.artwaycontent.domain.ContentEntity;
import space.artway.artwaycontent.domain.Genre;
import space.artway.artwaycontent.domain.Section;
import space.artway.artwaycontent.repository.ContentRepository;
import space.artway.artwaycontent.repository.GenreRepository;
import space.artway.artwaycontent.repository.SectionRepository;
import space.artway.artwaycontent.service.dto.ContentDto;

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Profile("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@EnableEurekaClient
@AutoConfigureStubRunner(stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        ids = "space.artway:artway-storage-ms:+:stubs:8089")
public class ContentControllerIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ContentRepository contentRepository;

    @Autowired
    SectionRepository sectionRepository;

    @Autowired
    GenreRepository genreRepository;

    static {
        System.setProperty("eureka.client.enabled", "false");
        System.setProperty("spring.cloud.config.failFast", "false");
    }

    @BeforeAll
    void setUp() {
        Section section = new Section();
        section.setName("TEST_SECTION");
        sectionRepository.save(section);

        Genre genre = new Genre();
        genre.setName("TEST_GENRE");
        genreRepository.save(genre);
    }

    @Test
    void uploadFile() throws Exception {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        File file = new File(classloader.getResource("dog.png").getFile());
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("data", "dog.png", "image/png", IOUtils.toByteArray(input));
        ContentDto contentDto = new EasyRandom(new EasyRandomParameters()
                .randomize(FieldPredicates.named("section"), () -> "TEST_SECTION")
                .randomize(FieldPredicates.named("genres"), () -> ImmutableSet.of("TEST_GENRE"))
        ).nextObject(ContentDto.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MockMultipartFile meta = new MockMultipartFile("meta", "", "application/json", objectMapper.writeValueAsBytes(contentDto));

        mvc.perform(multipart("/upload")
                .file(multipartFile)
                .file(meta)
        ).andExpect(status().is(200));

    }

    @Test
    void deleteContent() throws Exception {
        Section section = new EasyRandom().nextObject(Section.class);
        final Section sectionEntity = sectionRepository.save(section);
        ContentEntity content = new EasyRandom(new EasyRandomParameters()
                .randomize(FieldPredicates.named("section"), ()->sectionEntity)
                .excludeField(FieldPredicates.named("genres"))
                .excludeField(FieldPredicates.named("dislikes"))
                .excludeField(FieldPredicates.named("likes"))
                .excludeField(FieldPredicates.named("views"))
        ).nextObject(ContentEntity.class);
        final ContentEntity entity = contentRepository.save(content);

        mvc.perform(delete("/delete/{id}", entity.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(entity.getName()));
    }
}