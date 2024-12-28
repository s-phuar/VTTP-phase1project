package VTTPmini.mini_project.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import VTTPmini.mini_project.model.ActivityRatios;
import VTTPmini.mini_project.model.LiquidityRatios;
import VTTPmini.mini_project.model.MiscItems;
import VTTPmini.mini_project.model.ProfitabilityRatios;
import VTTPmini.mini_project.model.SolvencyRatios;
import VTTPmini.mini_project.model.Stock;
import VTTPmini.mini_project.model.ValuationRatios;
import VTTPmini.mini_project.repository.StockRepository;
import VTTPmini.mini_project.utils.Utils;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Service
public class StockService {
    
    @Autowired
    private StockRepository stockRepo;

    public static final String PRICE_URL = "https://api.polygon.io/v2/aggs/ticker";
    public static final String POST_URL = "https://api.sec-api.io";
    public static final String CONVERT_URL = "https://api.sec-api.io/xbrl-to-json";

    @Value("${polygon.api.key}")
    private String apiKeyPrice;

    @Value("${sec.api.key}")
    private String apiKey;

    //returns all tickers under the key(email)
    public Set<String> getAllStocks(String key){
        return stockRepo.getAllStocks(key);
    }

    public void saveStock(String email, Stock stock){
        stockRepo.saveStock(email, stock.getMi().getTicker(), stock);
    }

    public void deleteStock(String email, String ticker){
        stockRepo.deleteStock(email, ticker);
    }

    public Stock getStock(String email, String ticker){
        return stockRepo.getStock(email, ticker);
    }

   public List<Stock> getAllStockObj(String email){
        List<Object> listObj = stockRepo.getAllStockObj(email);
        List<Stock> listStock = listObj.stream() //convert List<Object> to stream
                        .filter(streamElement -> streamElement instanceof Stock) //only allows StreamElements of type String to pass through the stream
                        .map(streamElement -> (Stock) streamElement) //transforms each list element by casting to String
                        .collect(Collectors.toList()); //collects stream results into a list
        return listStock;
    }


    public double getPrice(String ticker){
        String tickerUp = ticker.toUpperCase();
        String url = UriComponentsBuilder  
            .fromUriString(PRICE_URL + "/" + tickerUp + "/prev")
            .toUriString();

        RequestEntity<Void> req = RequestEntity
            .get(url)
            .header("Authorization", "Bearer " + apiKeyPrice)
            .accept(MediaType.APPLICATION_JSON)
            .build();

        try{
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp = template.exchange(req, String.class);
            String payload = resp.getBody();
            JsonObject priceObj = Utils.toJson(payload);
            return Utils.jsonToDouble(priceObj);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting price response");
            return -1;
        }
    }

    //return list of report htms based of 1 ticker. Below has been coded to only return 1 htm for simplicatity
    public List<String> getReports(String ticker){
        String tickerUp = ticker.toUpperCase();

        //building json request payload
        JsonObject jObjPayload = Json.createObjectBuilder()
            .add("query", "formType:\"10-Q\" AND ticker:" + tickerUp)
            .add("from", "0")
            .add("size", "1") //value can be changed to return more than 1 report
            .build();

        RequestEntity<String> req = RequestEntity
            .post(POST_URL)
            .header("Authorization", apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(jObjPayload.toString(), String.class);

        try{
            RestTemplate template = new RestTemplate();
			ResponseEntity<String> resp = template.exchange(req, String.class);
			String payload = resp.getBody();
			return Utils.toList(payload);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting report response");
            return Collections.emptyList(); //returns empty list if theres an issue
        }
    }

    //report received as a htm, to convert to jsonobject
    public JsonObject reportToJson(String htm){
        String url = UriComponentsBuilder  
            .fromUriString(CONVERT_URL)
            .queryParam("htm-url", htm)
            .toUriString();

        RequestEntity<Void> req = RequestEntity
            .get(url)
            .header("Authorization", apiKey)
            .accept(MediaType.APPLICATION_JSON)
            .build();

        try{
            RestTemplate template = new RestTemplate();
            ResponseEntity<byte[]> resp = template.exchange(req, byte[].class);
            byte[] responseBody = resp.getBody();
            
            //payload has been gzip compressed by api provider
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(responseBody);
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String jsonString = stringBuilder.toString();
            return Utils.toJson(jsonString);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting Json response");
            return null;
        }
    }

    public MiscItems toMiscObj(String ticker, double price, String htm, JsonObject reportJson){
        MiscItems mi = new MiscItems();
        String tickerUp = ticker.toUpperCase();
        JsonObject coverPage = Utils.getCover(reportJson);
        mi.setTicker(tickerUp);
        mi.setCompanyName(coverPage.getString("EntityRegistrantName"));
        mi.setLastPrice(price);
        mi.setToday(new Date());
        mi.setHtm(htm);
        mi.setFiscalYear(coverPage.getString("DocumentFiscalYearFocus"));
        mi.setQuarter(coverPage.getString("DocumentFiscalPeriodFocus"));
        mi.setDocumentType(coverPage.getString("DocumentType"));

        return mi;
    }

    public ActivityRatios toActObj(JsonObject reportJson){
        ActivityRatios ar = new ActivityRatios();

        JsonObject incomeStatement = Utils.getIncomeStatement(reportJson);
        JsonObject balanceSheet = Utils.getBalanceSheet(reportJson);

        //revenue and cogs more common to have different names in a report jsonobject
        long revenues;
        revenues = Utils.getTotalValue(incomeStatement, "Revenues");
        if (revenues == 0) {
            revenues = Utils.getTotalValue(incomeStatement, "RevenueFromContractWithCustomerExcludingAssessedTax");
        }

        long cogs;
        cogs = Utils.getTotalValue(incomeStatement, "CostOfRevenue");
        if (cogs == 0) {
            cogs = Utils.getTotalValue(incomeStatement, "CostOfGoodsAndServicesSold");
        }

        long currInventory = Utils.getValue(balanceSheet, "InventoryNet", 0);
        long priorInventory = Utils.getValue(balanceSheet, "InventoryNet", 1);
        long avgInventory = (currInventory + priorInventory) / 2;
        double inventoryTurnover = (double) cogs / avgInventory;

        long currReceivables = Utils.getValue(balanceSheet, "AccountsReceivableNetCurrent", 0);
        long priorReceivables = Utils.getValue(balanceSheet, "AccountsReceivableNetCurrent", 1);
        long avgReceivables = (currReceivables + priorReceivables) / 2;
        double receivablesTurnover = (double) revenues / avgReceivables;

        long currPayables;
        currPayables = Utils.getValue(balanceSheet, "AccountsPayableCurrent", 0);
        if(currPayables == 0){
            currPayables = Utils.getValue(balanceSheet, "AccountsPayableTradeCurrent", 0);
        }

        long priorPayables;
        priorPayables = Utils.getValue(balanceSheet, "AccountsPayableCurrent", 1);
        if(priorPayables == 0){
            priorPayables = Utils.getValue(balanceSheet, "AccountsPayableTradeCurrent", 1);
        }
        
        long avgPayables = (currPayables + priorPayables) / 2;
        double payablesTurnover = (double) cogs / avgPayables;

        long currWorkingCapital = Utils.getValue(balanceSheet, "AssetsCurrent", 0) - Utils.getValue(balanceSheet, "LiabilitiesCurrent", 0);
        long priorWorkingCapital = Utils.getValue(balanceSheet, "AssetsCurrent", 1) - Utils.getValue(balanceSheet, "LiabilitiesCurrent", 1);
        long avgWorkingCapital = (currWorkingCapital + priorWorkingCapital) / 2;
        double workingCapitalTurnover = (double) revenues / avgWorkingCapital;

        long currFixedAssets;
        currFixedAssets = Utils.getValue(balanceSheet, "PropertyPlantAndEquipmentNet", 0);
        if(currFixedAssets == 0){
            currFixedAssets = Utils.getValue(balanceSheet, "PropertyPlantAndEquipmentAndFinanceLeaseRightOfUseAssetAfterAccumulatedDepreciationAndAmortization", 0);
        }

        long priorFixedAssets;
        priorFixedAssets = Utils.getValue(balanceSheet, "PropertyPlantAndEquipmentNet", 1);
        if(priorFixedAssets == 0){
            priorFixedAssets = Utils.getValue(balanceSheet, "PropertyPlantAndEquipmentAndFinanceLeaseRightOfUseAssetAfterAccumulatedDepreciationAndAmortization", 1);
        }


        long avgFixedAssets = (currFixedAssets + priorFixedAssets) / 2;
        double fixedAssetTurnover = (double) revenues / avgFixedAssets;

        long currTotalAssets = Utils.getValue(balanceSheet, "Assets", 0);
        long priorTotalAssets = Utils.getValue(balanceSheet, "Assets", 1);
        long avgTotalAssets = (currTotalAssets + priorTotalAssets) / 2;
        double totalAssetTurnover = (double) revenues / avgTotalAssets;

        ar.setInventoryTurnover(inventoryTurnover);
        ar.setDaysInventoryOnHand(365/inventoryTurnover);
        ar.setReceivablesTurnover(receivablesTurnover);
        ar.setDaysSalesOutstanding(365/receivablesTurnover);
        ar.setPayablesTurnover(payablesTurnover);
        ar.setDaysPayables(365/payablesTurnover);
        ar.setWorkingCapitalTurnover(workingCapitalTurnover);
        ar.setFixedAssetTurnover(fixedAssetTurnover);
        ar.setTotalAssetTurnover(totalAssetTurnover);

        return ar;
    }

    public LiquidityRatios toLiqObj(JsonObject reportJson){
        ActivityRatios ar = toActObj(reportJson); //borrow ratios from ar
        LiquidityRatios lr = new LiquidityRatios();

        JsonObject balanceSheet = Utils.getBalanceSheet(reportJson);

        long currentAssets = Utils.getValue(balanceSheet, "AssetsCurrent", 0);
        long currentLiabilities = Utils.getValue(balanceSheet, "LiabilitiesCurrent", 0);
        double currentRatio = (double) currentAssets / currentLiabilities;

        long cash = Utils.getValue(balanceSheet, "CashAndCashEquivalentsAtCarryingValue", 0);

        long marketableSec;
        marketableSec = Utils.getValue(balanceSheet, "MarketableSecuritiesCurrent", 0);
        if (marketableSec == 0) {
            marketableSec = Utils.getValue(balanceSheet, "ShortTermInvestments", 0);
        }
     
        long accReceivables = Utils.getValue(balanceSheet, "AccountsReceivableNetCurrent", 0);

        double quickRatioTemp = (double) cash + marketableSec + accReceivables;
        double cashRatioTemp = (double) cash + marketableSec;

        double quickRatio = (double) quickRatioTemp / currentLiabilities;
        double cashRatio = (double) cashRatioTemp / currentLiabilities;

        double cashConverionCycle = (double) ar.getDaysInventoryOnHand() + ar.getDaysSalesOutstanding() - ar.getDaysPayables();

        lr.setCurrentRatio(currentRatio);
        lr.setQuickRatio(quickRatio);
        lr.setCashRatio(cashRatio);
        lr.setCashConverionCycle(cashConverionCycle);

        return lr;
    }

    public SolvencyRatios toSolObj(JsonObject reportJson) {
        SolvencyRatios sr = new SolvencyRatios();

        JsonObject balanceSheet = Utils.getBalanceSheet(reportJson);

        long currTotalAssets = Utils.getValue(balanceSheet, "Assets", 0);

        long shortTermDebt;
        shortTermDebt = Utils.getValue(balanceSheet, "LongTermDebtCurrent", 0);
        if(shortTermDebt == 0){
            shortTermDebt = Utils.getValue(balanceSheet, "LongTermDebtAndFinanceLeasesCurrent", 0);
        }

        long longTermDebt;
        longTermDebt = Utils.getValue(balanceSheet, "LongTermDebtNoncurrent", 0);
        if(longTermDebt == 0){
            longTermDebt = Utils.getValue(balanceSheet, "LongTermDebtAndFinanceLeasesNoncurrent", 0);
        }
        if(longTermDebt == 0){
            longTermDebt = Utils.getValue(balanceSheet, "LongTermDebtAndCapitalLeaseObligations", 0);
        }


        long currShEquity = Utils.getValue(balanceSheet, "StockholdersEquity", 0);

        double interestBearingDebt = (double) shortTermDebt + longTermDebt;
        double totalCapital = (double) interestBearingDebt + currShEquity;
        double debtToAssets = (double) interestBearingDebt / currTotalAssets;
        double debtToCapital = (double) interestBearingDebt / totalCapital;
        double debtToEquity = (double) interestBearingDebt / currShEquity;

        long priorShEquity = Utils.getValue(balanceSheet, "StockholdersEquity", 1);
        long avgShEquity = (currShEquity + priorShEquity) / 2;
        long priorTotalAssets = Utils.getValue(balanceSheet, "Assets", 1);
        long avgTotalAssets = (currTotalAssets + priorTotalAssets) / 2;
        double financialLeverage = (double) avgTotalAssets / avgShEquity;

        sr.setDebtToAssets(debtToAssets);
        sr.setDebtToCapital(debtToCapital);
        sr.setDebtToEquity(debtToEquity);
        sr.setFinancialLeverage(financialLeverage);

        return sr;
    }


    public ProfitabilityRatios toProfObj(JsonObject reportJson){
        ProfitabilityRatios pr = new ProfitabilityRatios();

        JsonObject incomeStatement = Utils.getIncomeStatement(reportJson);
        JsonObject balanceSheet = Utils.getBalanceSheet(reportJson);

        long revenues;
        revenues = Utils.getTotalValue(incomeStatement, "Revenues");
        if (revenues == 0) {
            revenues = Utils.getTotalValue(incomeStatement, "RevenueFromContractWithCustomerExcludingAssessedTax");
        }

        long cogs;
        cogs = Utils.getTotalValue(incomeStatement, "CostOfRevenue");
        if (cogs == 0) {
            cogs = Utils.getTotalValue(incomeStatement, "CostOfGoodsAndServicesSold");
        }

        long grossProfit;
        grossProfit = Utils.getValue(incomeStatement, "GrossProfit", 0);
        if(grossProfit == 0){
            grossProfit = revenues - cogs;
        }

        long operatingProfit = Utils.getValue(incomeStatement, "OperatingIncomeLoss", 0);
        long preTaxProfit; 
        preTaxProfit = Utils.getValue(incomeStatement, "IncomeLossFromContinuingOperationsBeforeIncomeTaxesExtraordinaryItemsNoncontrollingInterest", 0);
        if (preTaxProfit == 0) {
            preTaxProfit = Utils.getValue(incomeStatement, "IncomeLossFromContinuingOperationsBeforeIncomeTaxesMinorityInterestAndIncomeLossFromEquityMethodInvestments", 0);            
        }
        
        
        long netProfit = Utils.getValue(incomeStatement, "NetIncomeLoss", 0);
        
        double grossProfitMargin = (double) grossProfit / revenues;
        double operatingProfitMargin = (double) operatingProfit / revenues;
        double preTaxMargin = (double) preTaxProfit / revenues;
        double netProfitMargin= (double) netProfit / revenues;

        long currTotalAssets = Utils.getValue(balanceSheet, "Assets", 0);
        long priorTotalAssets = Utils.getValue(balanceSheet, "Assets", 1);
        long avgTotalAssets = (currTotalAssets + priorTotalAssets) / 2;
        double operatingReturnOnAssets = (double) operatingProfit / avgTotalAssets;
        double returnOnAssets = (double) netProfit / avgTotalAssets;

        long shortTermDebt;
        shortTermDebt = Utils.getValue(balanceSheet, "LongTermDebtCurrent", 0);
        if(shortTermDebt == 0){
            shortTermDebt = Utils.getValue(balanceSheet, "LongTermDebtAndFinanceLeasesCurrent", 0);
        }

        long longTermDebt;
        longTermDebt = Utils.getValue(balanceSheet, "LongTermDebtNoncurrent", 0);
        if(longTermDebt == 0){
            longTermDebt = Utils.getValue(balanceSheet, "LongTermDebtAndFinanceLeasesNoncurrent", 0);
        }
        if(longTermDebt == 0){
            longTermDebt = Utils.getValue(balanceSheet, "LongTermDebtAndCapitalLeaseObligations", 0);
        }
        long currShEquity = Utils.getValue(balanceSheet, "StockholdersEquity", 0);
        long priorShEquity = Utils.getValue(balanceSheet, "StockholdersEquity", 1);
        long avgShEquity = (currShEquity + priorShEquity) / 2;
        double returnOnCapital = (double) operatingProfit / (shortTermDebt + longTermDebt + currShEquity);
        double returnOnEquity = (double) netProfit / avgShEquity;

        pr.setGrossProfitMargin(grossProfitMargin);
        pr.setOperatingProfitMargin(operatingProfitMargin);
        pr.setPreTaxMargin(preTaxMargin);
        pr.setNetProfitMargin(netProfitMargin);
        pr.setOperatingROA(operatingReturnOnAssets);
        pr.setRoAssets(returnOnAssets);
        pr.setRoCapital(returnOnCapital);
        pr.setRoTotalEquity(returnOnEquity);

        return pr;
    }


    public ValuationRatios toValObj(double price, JsonObject reportJson){
        ProfitabilityRatios pr = toProfObj(reportJson); //borrow ratios from pr
        ValuationRatios vr = new ValuationRatios();

        JsonObject incomeStatement = Utils.getIncomeStatement(reportJson);
        JsonObject SHEquity = Utils.getStatementSHEquity(reportJson);
        JsonObject netIncomeDetails = Utils.getNetIncomeDetails(reportJson);

        long revenues;
        revenues = Utils.getTotalValue(incomeStatement, "Revenues");
        if (revenues == 0) {
            revenues = Utils.getTotalValue(incomeStatement, "RevenueFromContractWithCustomerExcludingAssessedTax");
        }

        long dividends;
        dividends = Utils.getValue(SHEquity, "Dividends", 0);
        if (dividends == 0) {
            dividends = Utils.getValue(SHEquity, "DividendsCommonStockCash", 0);
        }
        if (dividends == 0) {
            dividends = Utils.getValue(SHEquity, "DividendsCommonStock", 0);
        }

        double EpsBasic = Utils.getDecimalValue(incomeStatement, "EarningsPerShareBasic", 0);
        double EpsDiluted = Utils.getDecimalValue(incomeStatement, "EarningsPerShareDiluted", 0);
        long shareCountBasic;
        shareCountBasic = Utils.getValue(incomeStatement, "WeightedAverageNumberOfSharesOutstandingBasic", 0);
        if(shareCountBasic == 0){
            shareCountBasic = Utils.getValue(netIncomeDetails, "WeightedAverageNumberOfSharesOutstandingBasic", 0);
        }


        double PeBasic = price / EpsBasic;
        double PeDiluted = price / EpsDiluted;
        double revPerShare = (double) revenues / shareCountBasic;
        double divPerShare = (double) dividends / shareCountBasic;
        double priceToSales = price / revPerShare;
        double divPayoutRatio = divPerShare / EpsBasic;
        double retentionRate = 1 - divPayoutRatio;
        double sustainableGrowthRate = retentionRate * pr.getRoTotalEquity();

        vr.setPeBasic(PeBasic);
        vr.setPeDiluted(PeDiluted);
        vr.setPriceToSales(priceToSales);
        vr.setDividendPayoutRatio(divPayoutRatio);
        vr.setRetentionRate(retentionRate);
        vr.setSustainableGrowthRate(sustainableGrowthRate);

        return vr;
    }
}
