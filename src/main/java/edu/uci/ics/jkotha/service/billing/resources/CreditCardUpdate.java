package edu.uci.ics.jkotha.service.billing.resources;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CreditCardModel;
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
import java.util.Date;

@Path("/creditcard/update")
public class CreditCardUpdate {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cardUpdate(@Context HttpHeaders headers, String jsonText) {

        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        ServiceLogger.LOGGER.info("Credit-card/update page requested:");

        CreditCardModel requestModel;
        BasicResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, CreditCardModel.class);
            String id = requestModel.getId();
            String firstName = requestModel.getFirstName();
            String lastName = requestModel.getLastName();
            Date expiration = requestModel.getExpiration();

            if (id == null) {
                ServiceLogger.LOGGER.info("Result Code:" + 321);
                responseModel = new BasicResponseModel(321);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!(id.length() <= 20 && id.length() >= 16)) {
                ServiceLogger.LOGGER.info("Result Code:" + 321);
                responseModel = new BasicResponseModel(321);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!id.matches("\\d+")) {
                ServiceLogger.LOGGER.info("Result Code:" + 322);
                responseModel = new BasicResponseModel(322);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!expiration.after(new Date())) {
                ServiceLogger.LOGGER.info("Result Code:" + 323);
                responseModel = new BasicResponseModel(323);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
            String checkStatement = "select id from creditcards where id = ?";
            PreparedStatement checkQuery = BillingService.getCon().prepareStatement(checkStatement);
            checkQuery.setString(1, id);
            ResultSet rs = checkQuery.executeQuery();
            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Result Code:" + 324);
                responseModel = new BasicResponseModel(324);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();

            }
            String statement = "update creditcards set firstName = ?, lastName = ?, expiration = ? where id =?";
            PreparedStatement query = BillingService.getCon().prepareStatement(statement);
            query.setString(4, id);
            query.setString(1, firstName);
            query.setString(2, lastName);
            query.setDate(3, new java.sql.Date(expiration.getTime()));
            query.execute();
            ServiceLogger.LOGGER.info("Result Code:" + 3210);
            responseModel = new BasicResponseModel(3210);
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
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("sessionId", sessionId).build();
    }
}
