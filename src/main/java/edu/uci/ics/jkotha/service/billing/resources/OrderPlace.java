package edu.uci.ics.jkotha.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.JustEmailReqModel;
import edu.uci.ics.jkotha.service.billing.models.OrderPlaceResponseModel;
import edu.uci.ics.jkotha.service.billing.support.FunctionsRequired;
import edu.uci.ics.jkotha.service.billing.support.PayPalOperations;
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
import java.sql.CallableStatement;
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
        OrderPlaceResponseModel responseModel1;
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

            String sum = "select sum(all quantity*unit_price*discount) as total from (" +
                    "              select movieId, quantity from carts where email = ? " +
                    "                ) as a" +
                    ",movie_prices as mp where mp.movieId = a.movieId;";
            PreparedStatement sumQuery = BillingService.getCon().prepareStatement(sum);
            sumQuery.setString(1, email);
            rs = sumQuery.executeQuery();
            rs.next();
            float total = rs.getFloat("total");
            if (total == 0.0) {
                ServiceLogger.LOGGER.info("Result Code:" + 341);
                responseModel = new BasicResponseModel(341);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel).build();
            }
            ServiceLogger.LOGGER.info("total:" + total);
            responseModel1 = PayPalOperations.makePayment(total);
            if (responseModel1.getResultCode() == 342) {
                ServiceLogger.LOGGER.info("Result Code:" + 342);
                return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel1).build();
            }
            String token = responseModel1.getToken();
            String statement1 = "select movieId,quantity from carts where email=?";
            PreparedStatement query1 = BillingService.getCon().prepareStatement(statement1);
            query1.setString(1, email);
            rs = query1.executeQuery();

            //call insert_sales_transactions(email,quantity,movieId,token);
            String call = "call insert_sales_transactions(?,?,?,?)";
            CallableStatement callStatement = BillingService.getCon().prepareCall(call);

            while (rs.next()) {
                callStatement.setString(1, email);
                callStatement.setString(3, rs.getString("movieId"));
                callStatement.setInt(2, rs.getInt("quantity"));
                callStatement.setString(4, token);
                callStatement.execute();
            }

            FunctionsRequired.clearCart(email);
            ServiceLogger.LOGGER.info("Result Code:" + 3400);
//            responseModel = new BasicResponseModel(3400);
            return Response.status(Response.Status.OK).header("email", emailHeader).header("sessionId", sessionId).entity(responseModel1).build();
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
