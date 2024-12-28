package VTTPmini.mini_project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import VTTPmini.mini_project.model.ActivityRatios;
import VTTPmini.mini_project.model.LiquidityRatios;
import VTTPmini.mini_project.model.MiscItems;
import VTTPmini.mini_project.model.ProfitabilityRatios;
import VTTPmini.mini_project.model.SolvencyRatios;
import VTTPmini.mini_project.model.Stock;
import VTTPmini.mini_project.model.User;
import VTTPmini.mini_project.model.ValuationRatios;
import VTTPmini.mini_project.service.StockService;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/stocks")
public class StockController {
    
    @Autowired
    private StockService stockSvc;

    @GetMapping({"", "/"})
    public String stockList(HttpSession sess, Model model){

        User user = (User) sess.getAttribute("user");
        if(sess==null || user==null){
            return "redirect:/";
        }

        model.addAttribute("email", user.getEmail());
        // model.addAttribute("stocks", stockSvc.getAllStocks(user.getEmail())); //list of stock tickers(outdated)
        model.addAttribute("stocks", stockSvc.getAllStockObj(user.getEmail())); //list of stock objects
        return "stock-list";
    }

    @GetMapping("/add")
    public String addStock(HttpSession sess, Model model){
        User user = (User) sess.getAttribute("user");
        if(sess==null || user==null){
            return "redirect:/";
        }
        // send empty stock object to view to prevent template parsing error
        Stock stock = new Stock();
        sess.setAttribute("stock", stock);
        model.addAttribute("stock", stock);

        return "stock-information";
    }

    //every start to this mapping is after a query is sent over
    @PostMapping("/query")
    public String stockQuery(@RequestParam String query, HttpSession sess, Model model){

        double price = stockSvc.getPrice(query); //returns -1 if somethign wrong with grabbnig price
        if (price == -1){
            Stock stock = new Stock();
            model.addAttribute("stock", stock);
            model.addAttribute("errorMessage", "This ticker is invalid.");
            return "stock-information";
        }

        List<String> quarterReport = stockSvc.getReports(query);
        if (quarterReport == null || quarterReport.isEmpty() || quarterReport.get(0) == null){
            Stock stock = new Stock();
            model.addAttribute("stock", stock);
            model.addAttribute("errorMessage", "No valid quarter report for this ticker");
            return "stock-information";
        }

        //there should only be an error here if the ticker is invalid, condition has been passed above
        JsonObject reportJson = stockSvc.reportToJson(quarterReport.get(0));

        MiscItems mi = stockSvc.toMiscObj(query, price, quarterReport.get(0), reportJson);
        ActivityRatios ar = stockSvc.toActObj(reportJson);
        LiquidityRatios lr = stockSvc.toLiqObj(reportJson);
        SolvencyRatios sr = stockSvc.toSolObj(reportJson);
        ProfitabilityRatios pr = stockSvc.toProfObj(reportJson);
        ValuationRatios vr = stockSvc.toValObj(price, reportJson);

        Stock stock = new Stock();
        stock.setMi(mi);
        stock.setAr(ar);
        stock.setLr(lr);
        stock.setSr(sr);
        stock.setPr(pr);
        stock.setVr(vr);

        model.addAttribute("stock", stock);
        sess.setAttribute("stock", stock);
        return "stock-information"; //sent to display that stock's information
    }

    @PostMapping("/save")
    public String stockSave(HttpSession sess, Model model){
        User user = (User) sess.getAttribute("user");
        Stock stock = (Stock) sess.getAttribute("stock");

        if(stock == null || stock.getMi() == null || stock.getMi().getTicker() == null){
            sess.setAttribute("stock", new Stock());
            model.addAttribute("stock", stock);
            return "stock-information";
        }


        stockSvc.saveStock(user.getEmail(), stock); 
        return "redirect:/stocks";
    }


    @GetMapping("/{ticker}")
    public String stockInformation(@PathVariable String ticker, HttpSession sess, Model model){
        
        User user = (User) sess.getAttribute("user");
        if(sess==null || user==null){
            return "redirect:/";
        }

        //using ticker, get stock object from redis
        Stock stock = stockSvc.getStock(user.getEmail(), ticker);

        model.addAttribute("stock", stock);
        return "display-stock";
    }

    @PostMapping("/delete/{ticker}")
    public String deleteStock(@PathVariable String ticker, HttpSession sess){
        User user = (User) sess.getAttribute("user");
        stockSvc.deleteStock(user.getEmail(), ticker);
        return "redirect:/stocks";
    }


}
