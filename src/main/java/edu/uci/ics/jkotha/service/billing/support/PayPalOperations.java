package edu.uci.ics.jkotha.service.billing.support;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.jkotha.service.billing.BillingService;
import edu.uci.ics.jkotha.service.billing.configs.Configs;
import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.OrderPlaceResponseModel;
import edu.uci.ics.jkotha.service.billing.models.TransactionFee;
import edu.uci.ics.jkotha.service.billing.models.TransactionModel;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PayPalOperations {
    public static final String clientId = "AWXjjGa1F2vbt69nZdDEq3jdbog6PzYWF40nobqYNOx1sexiShlglEhyjuGDgMO5lQLJ--kRft5QyK9Z";
    public static final String clientSecret = "EJLMZzw7SHAwbMqJgS4deA1SJWsGy_m_Ik-xWF-gR7v2oh2W0oJ6SPrbrT0MMvz73OmdhPkec1LyaBxY";

    public static OrderPlaceResponseModel makePayment(float total) {
        Configs configs = BillingService.getConfigs();
        String scheme = configs.getScheme();
        String hostName = configs.getHostName();
        int port = configs.getPort();
        String path = configs.getPath();
        URI uri = UriBuilder.fromUri(scheme + "127.0.0.1" + path).port(port).build();
        Amount amount = new Amount();
        DecimalFormat df = new DecimalFormat("0.00");
        amount.setCurrency("USD");
        ServiceLogger.LOGGER.info(df.format(total));
        amount.setTotal("" + df.format(total));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        String returnUrl = uri.toString() + "/order/complete";
        redirectUrls.setCancelUrl(uri.toString() + "/order/cancel");
        redirectUrls.setReturnUrl(returnUrl);
        payment.setRedirectUrls(redirectUrls);

        String redirectURL = null;
        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
            Payment payment1 = payment.create(apiContext);
            Iterator<Links> links = payment1.getLinks().iterator();
            while (links.hasNext()) {
                Links link = links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    redirectURL = link.getHref();
                    break;
                } else {
                    redirectURL = null;
                }
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        ServiceLogger.LOGGER.info(redirectURL);
        return getPlace(redirectURL);
    }

    private static OrderPlaceResponseModel getPlace(String redirectURL) {
        if (redirectURL == null) {
            return new OrderPlaceResponseModel(342, null, null);
        }
        String[] seg = redirectURL.split("=");
        String token = seg[seg.length - 1];
        return new OrderPlaceResponseModel(3400, redirectURL, token);
    }


    public static TransactionModel getTransaction(String transactionId) {
        TransactionModel model = null;
        APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
        try {
            Sale sale = Sale.get(apiContext, transactionId);
            model = new TransactionModel(
                    sale.getId(),
                    sale.getState(),
                    sale.getAmount(),
                    new TransactionFee(sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency()),
                    sale.getCreateTime(),
                    sale.getUpdateTime(),
                    null
            );
        } catch (PayPalRESTException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }
        return model;
    }
}
