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
import com.microsoft.azure.functions.annotation.BindingName;
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

    @FunctionName("findCustomerById")
    public HttpResponseMessage findCustomerById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "findCustomerById/{id}") // ID como parte de la ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Buscando cliente por ID: " + id);

        try {
            Long customerId = Long.parseLong(id);
            Optional<Customer> customer = customerService.findCustomerById(customerId);

            if (customer.isPresent()) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body(customer.get())
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Cliente con ID " + id + " no encontrado.")
                        .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al buscar cliente: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al buscar el cliente.")
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


    @FunctionName("updateCustomer")
    public HttpResponseMessage updateCustomer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "updateCustomer/{id}") // id por ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud updateCustomer con ID: " + id);

        try {
            Long customerId = Long.parseLong(id);

            // Parsear el JSON recibido
            String requestBody = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            Customer updatedData = mapper.readValue(requestBody, Customer.class);

            // Buscar si el cliente existe
            Optional<Customer> existingOpt = customerService.findCustomerById(customerId);
            if (existingOpt.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Cliente con ID " + id + " no encontrado.")
                        .build();
            }

            Customer existing = existingOpt.get();

            // Actualizar campos
            existing.setId(customerId);
            existing.setNombre(updatedData.getNombre());
            existing.setTelefono(updatedData.getTelefono());
            existing.setEmail(updatedData.getEmail());
            existing.setDireccion(updatedData.getDireccion());

            // Guardar cambios
            Customer updated = customerService.updateCustomer(existing);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(updated)
                    .build();

        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al actualizar cliente: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar cliente.")
                    .build();
        }
    }

    @FunctionName("deleteCustomer")
    public HttpResponseMessage deleteCustomer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "deleteCustomer/{id}") // ID por ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud deleteCustomer con ID: " + id);

        try {
            Long customerId = Long.parseLong(id);

            // Buscar si existe
            Optional<Customer> existing = customerService.findCustomerById(customerId);
            if (existing.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Cliente con ID " + id + " no encontrado.")
                        .build();
            }

            // Eliminar
            customerService.deleteCustomer(customerId);

            return request.createResponseBuilder(HttpStatus.OK).build(); // 204 vacío

        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al eliminar cliente: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar cliente.")
                    .build();
        }
    }

}

