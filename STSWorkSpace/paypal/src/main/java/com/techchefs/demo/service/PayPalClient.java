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

	String clientId = "AQnVhKcVO1b7ulvZBVhuerSG5YPoOcHxnsxQkCU5iFWU2JtTiFmJM3grzpW_uUijRgJJpOVrbXD89EQi";
	String clientSecret = "ELgXkjoML9glsUPPiOefKQXyLqcU3PWNSwmEvbqTuvIBC9oSeXIcTDrOQKl-2rpBuwqITDXKgWulMvtB";
	
public Map<String, Object> createPayment(String sum){
    Map<String, Object> response = new HashMap<String, Object>();
    Amount amount = new Amount();
    amount.setCurrency("USD");
    amount.setTotal(sum);
    System.out.println("Amount  "+amount);
    Transaction transaction = new Transaction();
    transaction.setAmount(amount);
    System.out.println("transaction  "+transaction);
    List<Transaction> transactions = new ArrayList<Transaction>();
    transactions.add(transaction);
    System.out.println("LIst Transcation : "+transactions);
    Payer payer = new Payer();
    payer.setPaymentMethod("paypal");
    System.out.println("payer : "+payer);
    Payment payment = new Payment();
    payment.setIntent("sale");
    payment.setPayer(payer);
    payment.setTransactions(transactions);
    System.out.println("payment : "+payment);
    RedirectUrls redirectUrls = new RedirectUrls();
    redirectUrls.setCancelUrl("http://localhost:3000/cancel");
    redirectUrls.setReturnUrl("http://localhost:3000/ConfirmPayment");
    System.out.println("redirectUrls : "+redirectUrls);
    payment.setRedirectUrls(redirectUrls);
    Payment createdPayment;
    try {
        String redirectUrl = "";
        APIContext context = new APIContext(clientId, clientSecret, "sandbox");
        System.out.println("context : "+context);
        createdPayment = payment.create(context);
        
        if(createdPayment!=null){
            List<Links> links = createdPayment.getLinks();
            System.out.println("links : "+links);
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
    System.out.println("response : "+response);
    return response;
}

public Map<String, Object> completePayment(String paymentId,String payerId){
    Map<String, Object> response = new HashMap<>();
	
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