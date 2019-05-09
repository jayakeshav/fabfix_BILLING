package edu.uci.ics.jkotha.service.billing.resources;


import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SampleClassOne {
    public static final String clientId = "AWXjjGa1F2vbt69nZdDEq3jdbog6PzYWF40nobqYNOx1sexiShlglEhyjuGDgMO5lQLJ--kRft5QyK9Z";
    public static final String clientSecret = "EJLMZzw7SHAwbMqJgS4deA1SJWsGy_m_Ik-xWF-gR7v2oh2W0oJ6SPrbrT0MMvz73OmdhPkec1LyaBxY";

    public static void test() {
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal("1.00");

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
        redirectUrls.setCancelUrl("https://localhost:1000/testCancel");
        redirectUrls.setReturnUrl("https://localhost:1000/testReturn");
        payment.setRedirectUrls(redirectUrls);

        try {
            APIContext apiContext = new APIContext(clientId, clientSecret, "sandbox");
            Payment payment1 = payment.create(apiContext);
//            System.out.println(payment1.toString());
            Iterator<Links> links = payment1.getLinks().iterator();
            while (links.hasNext()) {
                Links link = links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    System.out.println(link.getHref());

                }
            }
            Sale sale = new Sale();

        } catch (PayPalRESTException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(ExceptionUtils.exceptionStackTraceAsString(e));
        }
    }
}
