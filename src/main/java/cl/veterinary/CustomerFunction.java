package cl.veterinary;

import cl.veterinary.model.Customer;
import cl.veterinary.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.Optional;


public class CustomerFunction {

    private static final ApplicationContext context =
            new SpringApplicationBuilder(SpringBootAzureApp.class).run();

    private final CustomerService customerService =
            context.getBean(CustomerService.class); // usa la interfaz

    @FunctionName("findAllCustomer")
    public HttpResponseMessage findAllCustomer(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext executionContext) {

        executionContext.getLogger().info("Procesando solicitud listCustomer...");

        try {
            var customers = customerService.findAll();
            return request.createResponseBuilder(HttpStatus.OK).body(customers).build();
        } catch (Exception e) {
            executionContext.getLogger().severe("Error al obtener clientes: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al obtener los clientes")
                    .build();
        }
    }

    @FunctionName("saveCustomer")
    public HttpResponseMessage saveCustomer(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "saveCustomer")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud saveCustomer...");

        try {

            String requestBody = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            Customer customer = mapper.readValue(requestBody, Customer.class);

            Customer saved = customerService.saveCustomer(customer);

            return request.createResponseBuilder(HttpStatus.CREATED)
                    .body(saved)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error al guardar cliente: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar cliente")
                    .build();
        }
    }
}

