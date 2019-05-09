package edu.uci.ics.jkotha.service.billing.resources;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.support.PayPalOperations;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path("/order/complete")
public class OrderComplete {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response completePage(@Context HttpHeaders headers,
                                 @QueryParam("paymentId") String paymentId,
                                 @QueryParam("token") String token,
                                 @QueryParam("PayerID") String PayerID) {
        ServiceLogger.LOGGER.info("order/complete page requested");
        String email = headers.getHeaderString("email");
        String sessionId = headers.getHeaderString("sessionID");
        BasicResponseModel responseModel;

        if (token == null) {
            responseModel = new BasicResponseModel(3421);
            ServiceLogger.LOGGER.info("Result code:" + 3421);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).entity(responseModel).build();
        }

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(PayerID);

        try {
            APIContext apiContext = new APIContext(PayPalOperations.clientId, PayPalOperations.clientSecret, "sandbox");
            Payment createdPayment = payment.execute(apiContext, paymentExecution);
            String transactionId = createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();

            String updateString = "update transactions set transactionId =? where token = ?";
            PreparedStatement updateStatement = BillingService.getCon().prepareStatement(updateString);
            updateStatement.setString(1, transactionId);
            updateStatement.setString(2, token);
            updateStatement.executeUpdate();

            responseModel = new BasicResponseModel(3420);
            ServiceLogger.LOGGER.info("Result code:" + 3420);
            return Response.status(Response.Status.OK).header("email", email).header("sessionId", sessionId).entity(responseModel).build();

        } catch (PayPalRESTException | SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("email", email).header("sessionId", sessionId).build();
    }

}
