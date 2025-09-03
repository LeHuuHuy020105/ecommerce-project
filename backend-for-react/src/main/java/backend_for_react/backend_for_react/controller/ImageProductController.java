package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.ImageProduct.ImageProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.ImageProduct.ImageProductDeleteRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.ImageProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image_product")
@RequiredArgsConstructor
@Slf4j(topic = "IMAGE - PRODUCT -CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageProductController {
    ImageProductService imageProductService;
    @PostMapping("/add")
    public void createImageProduct(@RequestBody ImageProductCreationRequest request) {
        imageProductService.addImageProduct(request);
    }
    @PostMapping("/delete")
    public void deleteImageProduct(@RequestBody ImageProductDeleteRequest request) {
        imageProductService.deleteImageProduct(request);
    }
}
