package edu.uci.ics.jkotha.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CustomerModel;
import edu.uci.ics.jkotha.service.billing.models.CustomerRetrieveResponseModel;
import edu.uci.ics.jkotha.service.billing.models.JustEmailReqModel;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/customer/retrieve")
public class CustomerRetrieve {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response customerUpdate(@Context HttpHeaders headers, String jsonText) {

        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        ServiceLogger.LOGGER.info("Customer/update page requested:");

        JustEmailReqModel requestModel;
        CustomerRetrieveResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, JustEmailReqModel.class);
            String email = requestModel.getEmail();


            String checkStatement = "select * from customers where email = ?";
            PreparedStatement checkQuery = BillingService.getCon().prepareStatement(checkStatement);
            checkQuery.setString(1, email);
            ResultSet rs = checkQuery.executeQuery();
            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Result Code:" + 332);
                responseModel = new CustomerRetrieveResponseModel(332);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();

            }
            CustomerModel customer = new CustomerModel(
                    email,
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("ccId"),
                    rs.getString("address")
            );
            ServiceLogger.LOGGER.info("Result Code:" + 3320);
            responseModel = new CustomerRetrieveResponseModel(3320, customer);
            return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("Result Code:" + -3);
                responseModel = new CustomerRetrieveResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("Result Code:" + -2);
                responseModel = new CustomerRetrieveResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("sessionId", sessionId).build();
    }
}
