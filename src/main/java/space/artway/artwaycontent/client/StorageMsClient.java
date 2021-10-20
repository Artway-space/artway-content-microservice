package space.artway.artwaycontent.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import space.artway.artwaycontent.service.dto.FileDto;

@FeignClient(name = "artway-storage-ms")
public interface StorageMsClient {

    @RequestMapping(method = RequestMethod.POST, value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<FileDto> uploadFile(@RequestPart("file") MultipartFile file);

    @RequestMapping(method = RequestMethod.DELETE, value = "delete?fileId={id}")
    void deleteFile(@PathVariable("id") String fileId);
}
