package az.websuper.crm.services.impls;

import az.websuper.crm.dtos.order.OrderChangeStatus;
import az.websuper.crm.dtos.order.OrderCreateDto;
import az.websuper.crm.dtos.order.OrderDto;
import az.websuper.crm.dtos.order.OrderUpdateDto;
import az.websuper.crm.enums.OrderStatus;
import az.websuper.crm.message.OrderStatusChangeMessage;
import az.websuper.crm.models.Customer;
import az.websuper.crm.models.Order;
import az.websuper.crm.models.User;
import az.websuper.crm.payloads.ApiResponse;
import az.websuper.crm.payloads.PaginationPayload;
import az.websuper.crm.repositories.CustomerRepository;
import az.websuper.crm.repositories.OrderRepository;
import az.websuper.crm.repositories.UserRepository;
import az.websuper.crm.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange notificationFanoutExchange;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ModelMapper modelMapper, CustomerRepository customerRepository, RabbitTemplate rabbitTemplate, FanoutExchange notificationFanoutExchange, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.notificationFanoutExchange = notificationFanoutExchange;
        this.objectMapper = objectMapper;
    }


    @Override
    public PaginationPayload<OrderDto> getOrders(Integer pageNumber, Integer pageSize, String sortBy, Principal principal, OrderStatus orderStatus) {
        Sort sort = null;
        if (!sortBy.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }
        User findUserCompany = userRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> orders = null;
        if (orderStatus != null) {
            orders = orderRepository.findByCompanyIdAndOrderStatus(pageable, findUserCompany.getCompany().getId(), orderStatus);
        } else {
            orders = orderRepository.findByCompanyId(pageable, findUserCompany.getCompany().getId());
        }

        List<Order> result = orders.getContent();
        List<OrderDto> orderDto = result.stream().map(c -> modelMapper.map(c, OrderDto.class)).collect(Collectors.toList());
        PaginationPayload<OrderDto> postResponse = new PaginationPayload<>();
        postResponse.setData(orderDto);
        postResponse.setPageNumber(pageable.getPageNumber());
        postResponse.setPageSize(pageable.getPageSize());
        postResponse.setTotalElements(pageable.getPageSize());
        postResponse.setTotalPages(pageable.getPageSize());
        postResponse.setLastPage(pageable.isPaged());

        return postResponse;
    }

    @Override
    public ApiResponse createOrder(String userEmail, OrderCreateDto orderCreateDto) {
        User findUserCompany = userRepository.findByEmail(userEmail);
        Customer customer = customerRepository.findById(orderCreateDto.getCustomerId()).orElseThrow();
        Order order = modelMapper.map(orderCreateDto, Order.class);
        order.setEmployee(findUserCompany);
        order.setDeliveryDate(new Date());
        order.setPickupDate(new Date());
        order.setCustomer(customer);
        order.setCompany(findUserCompany.getCompany());
        orderRepository.save(order);
        return null;
        //k320Crm Order Status 2 den bashlamaq
    }

    @Override
    public ApiResponse updateOrder(String userEmail, Long id, OrderUpdateDto orderUpdateDto) {
        try {

            Order findOrder = orderRepository.findById(id).orElseThrow();
            User findUserCompany = userRepository.findByEmail(userEmail);
            if (!findUserCompany.getCompany().getName().equals(findOrder.getCompany().getName())) {
                return new ApiResponse("Siz bu Sifarishi yenileye bilmezsiniz!", false);

            }
            findOrder.setNumber(orderUpdateDto.getNumber());
            findOrder.setOrderStatus(orderUpdateDto.getOrderStatus());
            findOrder.setPickupDate(orderUpdateDto.getPickupDate());
            findOrder.setDeliveryDate(orderUpdateDto.getDeliveryDate());
            findOrder.setPickupAddress(orderUpdateDto.getPickupAddress());
            findOrder.setDeliveryAddress(orderUpdateDto.getDeliveryAddress());
            findOrder.setWeight(orderUpdateDto.getWeight());
            findOrder.setPrice(orderUpdateDto.getPrice());
            findOrder.setTax(orderUpdateDto.getTax());
            findOrder.setPrice(orderUpdateDto.getPrice());
            orderRepository.save(findOrder);
            return new ApiResponse("Deyishiklik qeyde alindi", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
        //   Order order = modelMapper.map(orderUpdateDto,Order.class);

    }

    @Override
    public OrderUpdateDto getUpdateOrder(String userEmail, Long id) {
        try {
            Order findOrder = orderRepository.findById(id).orElseThrow();
            User findUserCompany = userRepository.findByEmail(userEmail);
            if (!findUserCompany.getCompany().getName().equalsIgnoreCase(findOrder.getCompany().getName())) {
                return null;
            }
            OrderUpdateDto result = modelMapper.map(findOrder, OrderUpdateDto.class);
            return result;
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public ApiResponse removeOrder(String userEmail, Long id) {
        try {
            Order findOrder = orderRepository.findById(id).orElseThrow();
            User findUserCompany = userRepository.findByEmail(userEmail);
            if (!findUserCompany.getCompany().getName().equals(findOrder.getCompany().getName())) {
                return new ApiResponse("Siz bu sifarishi sile bilmezsiniz!", false);
            }
            findOrder.setDeleted(true);
            orderRepository.save(findOrder);
            return new ApiResponse("Sifarish silindi", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }

    }

    @Override
    public ApiResponse changeStatus(String userEmail, OrderChangeStatus orderChangeStatus) {
        try {
            User findUserCompany = userRepository.findByEmail(userEmail);
            Order order = orderRepository.findByIdAndCompanyId(orderChangeStatus.getId(), findUserCompany.getId());
            order.setOrderStatus(orderChangeStatus.getOrderStatus());
            orderRepository.save(order);
            Customer customer = order.getCustomer();
            OrderStatusChangeMessage orderStatusChangeMessage = new OrderStatusChangeMessage(order.getId(), customer.getName() + " " + customer.getLastname(), customer.getPhoneNumber(), customer.getEmail());
            String message = objectMapper.writeValueAsString(orderStatusChangeMessage);
            rabbitTemplate.convertAndSend(notificationFanoutExchange.getName(), "", message);
            return new ApiResponse("Melumat yenilendi", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }

    }
}
