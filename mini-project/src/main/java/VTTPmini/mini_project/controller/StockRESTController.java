package VTTPmini.mini_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import VTTPmini.mini_project.model.Stock;
import VTTPmini.mini_project.model.User;
import VTTPmini.mini_project.service.StockRESTService;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/stocks/dev")
public class StockRESTController {
    
    @Autowired
    private StockRESTService stockRestSvc;

    @GetMapping(path="/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> retrieveStockJsonObject(@PathVariable String ticker, HttpSession sess)
    {
        User user = (User) sess.getAttribute("user");
        if(sess==null || user==null){
            JsonObject userErrorObj = Json.createObjectBuilder()
                .add("error", "User has been not been logged in to authorize access")
                .build();
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(userErrorObj.toString());
        }

        try{
            String tickerUp = ticker.toUpperCase();
            Stock stock = stockRestSvc.getStock(user.getEmail(), tickerUp);
            JsonObject stockJson = Stock.toJson(stock);
            return ResponseEntity.ok(stockJson.toString());
        } catch(NullPointerException e){
            JsonObject errorObj = Json.createObjectBuilder()
            .add("error", "Cannot find ticker: " + ticker)
            .build();

            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorObj.toString());
        }

    }





}
