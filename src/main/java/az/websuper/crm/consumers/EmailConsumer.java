package az.websuper.crm.consumers;

import az.websuper.crm.message.OrderStatusChangeMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConsumer {


    private final ObjectMapper objectMapper;

    public EmailConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        //M1 den sonra baxilmali
    }

    @RabbitListener(queues = "email-queue")
    public void handlerMessage(String message) throws JsonProcessingException {
        OrderStatusChangeMessage result  = objectMapper.readValue(message,OrderStatusChangeMessage.class);
        System.out.println("Email "+ result.getEmail()+" adresine gonderildi.");
    }
}
