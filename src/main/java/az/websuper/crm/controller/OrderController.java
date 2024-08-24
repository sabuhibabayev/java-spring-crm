package az.websuper.crm.controller;

import az.websuper.crm.dtos.order.OrderChangeStatus;
import az.websuper.crm.dtos.order.OrderCreateDto;
import az.websuper.crm.dtos.order.OrderDto;
import az.websuper.crm.dtos.order.OrderUpdateDto;
import az.websuper.crm.enums.OrderStatus;
import az.websuper.crm.payloads.ApiResponse;
import az.websuper.crm.payloads.PaginationPayload;
import az.websuper.crm.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("api/order")
public class OrderController {
    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity create(@RequestBody OrderCreateDto orderCreateDto, Principal principal) {
        orderService.createOrder(principal.getName(), orderCreateDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<PaginationPayload<OrderDto>> getAll(Principal principal, Integer pageNumber, Integer pageSize, String sortBy, Optional<OrderStatus> orderStatus) {
        PaginationPayload<OrderDto> response = orderService.getOrders(pageNumber, pageSize, sortBy, principal, orderStatus.get());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/status")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<ApiResponse> changeStatus(@RequestBody OrderChangeStatus orderChangeStatus, Principal principal) {
        ApiResponse response = orderService.changeStatus(principal.getName(), orderChangeStatus);
        if (response.isSuccess()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id ,@RequestBody OrderUpdateDto orderUpdateDto,Principal principal){
        ApiResponse response = orderService.updateOrder(principal.getName(), id,orderUpdateDto);
        return  new ResponseEntity<>(response,HttpStatus.OK);

    }
    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<ApiResponse> remove(@PathVariable Long id,Principal principal){
        ApiResponse response = orderService.removeOrder(principal.getName(), id);
        return  new ResponseEntity(response,HttpStatus.OK);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<OrderUpdateDto> getUpdateOrders(@PathVariable Long id,Principal principal){
        OrderUpdateDto result = orderService.getUpdateOrder(principal.getName(),id);
        return  new ResponseEntity(result,HttpStatus.OK);
    }



}
