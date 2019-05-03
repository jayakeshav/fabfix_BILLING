package edu.uci.ics.jkotha.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CardRetrieveResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CreditCardModel;
import edu.uci.ics.jkotha.service.billing.models.JustId;
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

@Path("/creditcard/retrieve")
public class CreditCardRetrieve {


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cardRetrieve(@Context HttpHeaders headers, String jsonText) {

        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        ServiceLogger.LOGGER.info("Credit-card/retrieve page requested:");

        JustId requestModel;
        CardRetrieveResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, JustId.class);
            String id = requestModel.getId();

            if (id == null) {
                ServiceLogger.LOGGER.info("Result Code:" + 321);
                responseModel = new CardRetrieveResponseModel(321);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!(id.length() <= 20 && id.length() >= 16)) {
                ServiceLogger.LOGGER.info("Result Code:" + 321);
                responseModel = new CardRetrieveResponseModel(321);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (!id.matches("\\d+")) {
                ServiceLogger.LOGGER.info("Result Code:" + 322);
                responseModel = new CardRetrieveResponseModel(322);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
            String statement = "select firstName, lastName, expiration from creditcards where id =?";
            PreparedStatement query = BillingService.getCon().prepareStatement(statement);
            query.setString(1, id);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                CreditCardModel cc = new CreditCardModel(
                        id,
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        new Date(rs.getDate("expiration").getTime())
                );
                ServiceLogger.LOGGER.info("Result Code:" + 3230);
                responseModel = new CardRetrieveResponseModel(3230, cc);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else {
                ServiceLogger.LOGGER.info("Result Code:" + 324);
                responseModel = new CardRetrieveResponseModel(324);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();

            }
        } catch (IOException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            if (e instanceof JsonParseException) {
                ServiceLogger.LOGGER.info("Result Code:" + -3);
                responseModel = new CardRetrieveResponseModel(-3);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            } else if (e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.info("Result Code:" + -2);
                responseModel = new CardRetrieveResponseModel(-2);
                return Response.status(Response.Status.BAD_REQUEST).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("sessionId", sessionId).build();
    }
}
