package com.springboot.stockService.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;


@RestController
@RequestMapping("/rest/stock")
@RefreshScope
public class StockResource {


    @Autowired
	@Lazy
    RestTemplate restTemplate;

//	@Value("${microservice.db-service.endpoints.endpoint.uri}")
//	private String endpoint_url;

    YahooFinance yahooFinance;

    public StockResource() {
    	this.yahooFinance=new YahooFinance();
    }


	    @GetMapping("/{userName}")
	    @CircuitBreaker(name="StockService", fallbackMethod = "getDbServiceFallback")
		//@Retry(name="StockService", fallbackMethod = "getDbServiceFallback")
	    public List<Quote> getStock(@PathVariable("userName")
	    final String userName) {
		 ResponseEntity<List<String>> quoteResponse=restTemplate.
				 exchange("http://db-service/rest/db/"+userName,
				 HttpMethod.GET,
				 null,
				 new ParameterizedTypeReference<List<String>>() {
         });

		List<String> quotes=quoteResponse.getBody();
	    return quotes
                .stream()
                .map(quote -> {
                Stock stock=getStockPrice_internal(quote); //reliance
                return new Quote(quote,stock.getCurrency()); //(reliance,100$) //(SBI,200$)
                })
                .collect(Collectors.toList());
	 }

		 public List<Quote> getDbServiceFallback(Exception exception)
		 {
			 System.out.println("getDbServiceFallback methode call");
			 Quote quote1 = new Quote("Dummy","120$");
			 List<Quote> quotes = new ArrayList<Quote>();
			 quotes.add(quote1);
		     return quotes;
		 }

	 private class Quote{
		 private String quote;

		private String price;
		 public Quote(String quote,String price)
		 {
			 this.quote=quote;
			 this.price=price;
		 }
		 public String getQuote() {
				return quote;
			}
			public void setQuote(String quote) {
				this.quote = quote;
			}
			public String getPrice() {
				return price;
			}
			public void setPrice(String price) {
				this.price = price;
			}

	 }


	 private Stock getStockPrice_yahoo(String quote) {
	        try {
	            return YahooFinance.get(quote);
	        } catch (IOException e) {
	            e.printStackTrace();
	            return new Stock(quote);
	        }
	    }


	 private Stock getStockPrice_internal(String quote)
	 {
		 //return	YahooFinance.get(quote);
		 if(quote.equalsIgnoreCase("GOOG"))
		 {
			 Stock stock=new Stock(quote);
			 stock.setCurrency("100$");
			 return stock;
		 } else if(quote.equalsIgnoreCase("RIL"))
		 {
			 Stock stock=new Stock(quote);

			 stock.setCurrency("200$");
			 return stock;
		 }else if(quote.equalsIgnoreCase("AIRTEL"))
		 {
			 Stock stock=new Stock(quote);

			 stock.setCurrency("300$");
			 return stock;
		 }else if(quote.equalsIgnoreCase("JIO"))
		 {
			 Stock stock=new Stock(quote);

			 stock.setCurrency("400$");
			 return stock;
		 }if(quote.equalsIgnoreCase("SUZ"))
		 {
			 Stock stock=new Stock(quote);

			 stock.setCurrency("10$");
			 return stock;
		 }
		 if(quote.equalsIgnoreCase("SBI"))
		 {
			 Stock stock=new Stock(quote);

			 stock.setCurrency("20$");
			 return stock;
		 }

		 if(quote.equalsIgnoreCase("YESBANK"))
		 {
			 Stock stock=new Stock(quote);

			 stock.setCurrency("10$");
			 return stock;
		 }
		 else
			 {
			 Stock stock=new Stock("NA");

			 stock.setCurrency("0$");
			 return stock;
			 }
	 }
}
