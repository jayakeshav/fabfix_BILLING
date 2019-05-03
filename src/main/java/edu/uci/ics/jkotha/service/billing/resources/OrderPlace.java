package edu.uci.ics.jkotha.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
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


@Path("/order/place")
public class OrderPlace {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response orderPlace(@Context HttpHeaders headers, String jsonText) {

        String emailHeader = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionId");
        ServiceLogger.LOGGER.info("Order/place page requested:");

        JustEmailReqModel requestModel;
        BasicResponseModel responseModel;
        try {
            ObjectMapper mapper = new ObjectMapper();
            requestModel = mapper.readValue(jsonText, JustEmailReqModel.class);
            String email = requestModel.getEmail();

            String statement = "select * from customers where email=?";
            PreparedStatement query = BillingService.getCon().prepareStatement(statement);
            query.setString(1, email);
            ResultSet rs = query.executeQuery();
            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Result Code:" + 332);
                responseModel = new BasicResponseModel(332);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
            String statement1 = "select movieId,quantity from carts where email=?";
            PreparedStatement query1 = BillingService.getCon().prepareStatement(statement1);
            query1.setString(1, email);
            rs = query1.executeQuery();
            if (!rs.next()) {
                ServiceLogger.LOGGER.info("Result Code:" + 341);
                responseModel = new BasicResponseModel(341);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
            rs.previous();
            while (rs.next()) {
                String insertStatement = "insert into sales(email, movieId, quantity, saleDate) values (?,?,?,current_date())";
                PreparedStatement insertQuery = BillingService.getCon().prepareStatement(insertStatement);
                insertQuery.setString(1, email);
                insertQuery.setString(2, rs.getString("movieId"));
                insertQuery.setInt(3, rs.getInt("quantity"));
                insertQuery.execute();
            }
            ServiceLogger.LOGGER.info("Result Code:" + 3400);
            responseModel = new BasicResponseModel(3400);
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
                ServiceLogger.LOGGER.info("Result Code:" + 332);
                responseModel = new BasicResponseModel(332);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", emailHeader).header("sessionId", sessionId).build();
    }
}
