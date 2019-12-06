package com.techchefs.demo.service;

import java.util.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;


@Service
public class PayPalClient{

	String clientId = "AdK0Dipx1A89-4YcqImHi1bDfmsyHlRNLJ8oKtIPoYx2yerI4XIIqGmFkPUOh03tCQPwNO5MsgIasqGM";
	String clientSecret = "EO0sXInbwl_fF_NFdnToXxmCahMN8zayMCMxAQZwxhDfkQfMlvcB1x27J-Sd3VLGJK7UtDByKT-MUEwN";
	
public Map<String, Object> createPayment(String sum){
    Map<String, Object> response = new HashMap<String, Object>();
    Amount amount = new Amount();
    amount.setCurrency("USD");
    amount.setTotal(sum);
    Transaction transaction = new Transaction();
    transaction.setAmount(amount);
    List<Transaction> transactions = new ArrayList<Transaction>();
    transactions.add(transaction);

    Payer payer = new Payer();
    payer.setPaymentMethod("paypal");

    Payment payment = new Payment();
    payment.setIntent("sale");
    payment.setPayer(payer);
    payment.setTransactions(transactions);

    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl("http://localhost:4200/cancel");
    redirectUrls.setReturnUrl("http://localhost:4200/");
    payment.setRedirectUrls(redirectUrls);
    Payment createdPayment;
    try {
        String redirectUrl = "";
        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        createdPayment = payment.create(context);
        if(createdPayment!=null){
            List<Links> links = createdPayment.getLinks();
            for (Links link:links) {
                if(link.getRel().equals("approval_url")){
                    redirectUrl = link.getHref();
                    break;
                }
            }
            response.put("status", "success");
            response.put("redirect_url", redirectUrl);
        }
    } catch (PayPalRESTException e) {
        System.out.println("Error happened during payment creation!");
    }
    return response;
}

public Map<String, Object> completePayment(String paymentId,String payerId){
    Map<String, Object> response = new HashMap();
    Payment payment = new Payment(); 
    payment.setId(paymentId);

    PaymentExecution paymentExecution = new PaymentExecution();
    paymentExecution.setPayerId(payerId);
    try {
        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        Payment createdPayment = payment.execute(context, paymentExecution);
        if(createdPayment!=null){
            response.put("status", "success");
            response.put("payment", createdPayment.toString());
        }
    } catch (PayPalRESTException e) {
        System.err.println(e.getDetails());
    }
    return response;
}
}

