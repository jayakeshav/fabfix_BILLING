package edu.uci.ics.jkotha.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.*;
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

@Path("/cart/retrieve")
public class ShoppingCartRetrieve {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartRetrieve(@Context HttpHeaders headers, String jsonText) {
        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        String transactionId = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("cart/retrieve page requested:");
        JustEmailReqModel requestModel;
        CartRetrieveResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, JustEmailReqModel.class);
            String email = requestModel.getEmail();

            //basic checks:
            if (email == null) {
                ServiceLogger.LOGGER.info("Result Code:" + -10);
                responseModel = new CartRetrieveResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else if (email.length() == 0 | email.length() > 50) {
                ServiceLogger.LOGGER.info("Result Code:" + -10);
                responseModel = new CartRetrieveResponseModel(-10);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!FunctionsRequired.isValidEmail(email)) {
                ServiceLogger.LOGGER.info("Result Code:" + -11);
                responseModel = new CartRetrieveResponseModel(-11);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            }

            String statement6 = "select * from  carts where email = ?";
            PreparedStatement query6 = BillingService.getCon().prepareStatement(statement6);
            query6.setString(1, email);
            ResultSet rs = query6.executeQuery();
            if (rs.next()) {
                String statement7 = "select p.title,p.discount,p.price,m.movieId,m.quantity,m.email from prices as p," +
                        "(select movieId,quantity,email from carts where email=?)" +
                        "as m where p.movieId=m.movieId;";
                PreparedStatement query7 = BillingService.getCon().prepareStatement(statement7);
                query7.setString(1, email);
                rs = query7.executeQuery();
                CartRetrieveItemModel[] items = FunctionsRequired.getCartItems(rs);
                ServiceLogger.LOGGER.info("Result Code:" + 3130);
                responseModel = new CartRetrieveResponseModel(3130, items);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.info("Result Code:" + 312);
                responseModel = new CartRetrieveResponseModel(312);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();

            }
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("Result Code:" + -3);
                responseModel = new CartRetrieveResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("Result Code:" + -2);
                responseModel = new CartRetrieveResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("transactionID", transactionId).header("sessionId", sessionId).build();
    }
}
