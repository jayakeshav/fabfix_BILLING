package edu.uci.ics.jkotha.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CustomerModel;
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
import java.sql.SQLException;


@Path("/customer/insert")
public class CustomerInsert {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response customerInsert(@Context HttpHeaders headers, String jsonText) {

        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        ServiceLogger.LOGGER.info("Customer/insert page requested:");

        CustomerModel requestModel;
        BasicResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, CustomerModel.class);
            String ccId = requestModel.getCcId();
            String firstName = requestModel.getFirstName();
            String lastName = requestModel.getLastName();
            String address = requestModel.getAddress();
            String email = requestModel.getEmail();


            if (ccId == null) {
                ServiceLogger.LOGGER.info("Result Code:" + 321);
                responseModel = new BasicResponseModel(321);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!(ccId.length() <= 20 && ccId.length() >= 16)) {
                ServiceLogger.LOGGER.info("Result Code:" + 321);
                responseModel = new BasicResponseModel(321);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!ccId.matches("\\d+")) {
                ServiceLogger.LOGGER.info("Result Code:" + 322);
                responseModel = new BasicResponseModel(322);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }

            String statement = "insert into customers(email, firstName, lastName, ccId, address) value (?,?,?,?,?)";
            PreparedStatement query = BillingService.getCon().prepareStatement(statement);
            query.setString(1, email);
            query.setString(2, firstName);
            query.setString(3, lastName);
            query.setString(4, ccId);
            query.setString(5, address);
            query.execute();
            ServiceLogger.LOGGER.info("Result Code:" + 3300);
            responseModel = new BasicResponseModel(3300);
            return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("Result Code:" + -3);
                responseModel = new BasicResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("Result Code:" + -2);
                responseModel = new BasicResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e.getErrorCode() == 1216) {
                ServiceLogger.LOGGER.info("Result Code:" + 331);
                responseModel = new BasicResponseModel(331);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (e.getErrorCode() == 1062) {
                ServiceLogger.LOGGER.info("Result Code:" + 333);
                responseModel = new BasicResponseModel(333);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("sessionId", sessionId).build();
    }
}
