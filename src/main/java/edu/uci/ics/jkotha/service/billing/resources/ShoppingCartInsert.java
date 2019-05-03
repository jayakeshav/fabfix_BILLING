package edu.uci.ics.jkotha.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CartInsertUpdateReqModel;
import edu.uci.ics.jkotha.service.billing.support.FunctionsRequired;
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

@Path("/cart/insert")
public class ShoppingCartInsert {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartInsert(@Context HttpHeaders headers, String jsonText) {
        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        ServiceLogger.LOGGER.info("cart/insert page requested:");
        CartInsertUpdateReqModel requestModel;
        BasicResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, CartInsertUpdateReqModel.class);
            String email = requestModel.getEmail();
            String movieId = requestModel.getMovieId();
            int quantity = requestModel.getQuantity();

            //basic checks:
            if (email == null) {
                ServiceLogger.LOGGER.info("Result Code:" + -10);
                responseModel = new BasicResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (email.length() == 0 | email.length() > 50) {
                ServiceLogger.LOGGER.info("Result Code:" + -10);
                responseModel = new BasicResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!FunctionsRequired.isValidEmail(email)) {
                ServiceLogger.LOGGER.info("Result Code:" + -11);
                responseModel = new BasicResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (quantity <= 0) {
                ServiceLogger.LOGGER.info("Result Code:" + 33);
                responseModel = new BasicResponseModel(33);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }

            String statement1 = "insert into carts(email, movieId, quantity) values (?,?,?)";
            PreparedStatement query1 = BillingService.getCon().prepareStatement(statement1);
            query1.setString(1, email);
            query1.setString(2, movieId);
            query1.setInt(3, quantity);
            query1.execute();
            ServiceLogger.LOGGER.info("Result Code:" + 3100);
            responseModel = new BasicResponseModel(3100);
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
            if (e.getErrorCode() == 1062) {
                ServiceLogger.LOGGER.info("Result Code:" + 311);
                responseModel = new BasicResponseModel(311);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("sessionId", sessionId).build();
    }
}
