package edu.uci.ics.jkotha.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CartDeleteReqModel;
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
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/cart/delete")
public class ShoppingCartDelete {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartDelete(@Context HttpHeaders headers, String jsonText) {
        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("cart/delete page requested:");
        CartDeleteReqModel requestModel;
        BasicResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, CartDeleteReqModel.class);
            String email = requestModel.getEmail();
            String movieId = requestModel.getMovieId();

            //basic checks:
            if (email == null) {
                ServiceLogger.LOGGER.info("Result Code:" + -10);
                responseModel = new BasicResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else if (email.length() == 0 | email.length() > 50) {
                ServiceLogger.LOGGER.info("Result Code:" + -10);
                responseModel = new BasicResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!FunctionsRequired.isValidEmail(email)) {
                ServiceLogger.LOGGER.info("Result Code:" + -11);
                responseModel = new BasicResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            }

            String statement4 = "select * from  carts where email = ? and movieId = ?";
            PreparedStatement query4 = BillingService.getCon().prepareStatement(statement4);
            query4.setString(1, email);
            query4.setString(2, movieId);
            ResultSet rs = query4.executeQuery();
            if (rs.next()) {
                String statement5 = "delete from carts where email=? and movieId = ?";
                PreparedStatement query5 = BillingService.getCon().prepareStatement(statement5);
                query5.setString(1, email);
                query5.setString(2, movieId);
                query5.execute();
                ServiceLogger.LOGGER.info("Result Code:" + 3120);
                responseModel = new BasicResponseModel(3120);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.info("Result Code:" + 312);
                responseModel = new BasicResponseModel(312);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();

            }
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("Result Code:" + -3);
                responseModel = new BasicResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("Result Code:" + -2);
                responseModel = new BasicResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).build();
    }
}
