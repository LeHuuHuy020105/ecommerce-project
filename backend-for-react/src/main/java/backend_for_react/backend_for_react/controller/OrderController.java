package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Order.ChangeOrderStatusRequest;
import backend_for_react.backend_for_react.controller.request.Order.OrderCreationRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.OrderResponse;
import backend_for_react.backend_for_react.service.impl.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/add")
    public ResponseEntity<Object> createOrder(@RequestBody OrderCreationRequest req){
        Long orderId = orderService.save(req);
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message","order created successfull");
        result.put("data",orderId);
        return new ResponseEntity<>(result,HttpStatus.CREATED);
    }
    @PostMapping("/changestatus/{orderId}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long orderId , @RequestBody ChangeOrderStatusRequest req){
        orderService.changeStatus(orderId,req);
        return new ResponseEntity<>("",HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable Long orderId){
        OrderResponse orderResponse = orderService.getOrderById(orderId);
        return new ApiResponse<OrderResponse>(HttpStatus.OK.value(), "Get order detail" , orderResponse);
    }

}
