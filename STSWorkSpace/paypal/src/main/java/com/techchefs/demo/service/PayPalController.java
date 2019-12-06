package com.techchefs.demo.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/paypal")
public class PayPalController {
    @Autowired
    private PayPalClient payPalClient;
   
    

    @PostMapping(value = "/make/payment")
    public Map<String, Object> makePayment(@RequestParam("sum") String sum){
    	System.err.println("Inside first api");
        return payPalClient.createPayment(sum);
    }
    @PostMapping(value = "/complete/payment")
    public Map<String, Object> completePayment(@RequestParam String paymentId, @RequestParam String payerId){
        return payPalClient.completePayment(paymentId,payerId);
    }
}
