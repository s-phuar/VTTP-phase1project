package VTTPmini.mini_project.model;

import VTTPmini.mini_project.utils.Utils;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Stock {
    private MiscItems mi;
    private ActivityRatios ar;
    private LiquidityRatios lr;
    private ProfitabilityRatios pr;
    private SolvencyRatios sr;
    private ValuationRatios vr;

    public MiscItems getMi() {return mi;}
    public void setMi(MiscItems mi) {this.mi = mi;}
    public ActivityRatios getAr() {return ar;}
    public void setAr(ActivityRatios ar) {this.ar = ar;}
    public LiquidityRatios getLr() {return lr;}
    public void setLr(LiquidityRatios lr) {this.lr = lr;}
    public ProfitabilityRatios getPr() {return pr;}
    public void setPr(ProfitabilityRatios pr) {this.pr = pr;}
    public SolvencyRatios getSr() {return sr;}
    public void setSr(SolvencyRatios sr) {this.sr = sr;}
    public ValuationRatios getVr() {return vr;}
    public void setVr(ValuationRatios vr) {this.vr = vr;}


    public static JsonObject toJson(Stock stock){
        JsonObject micsItems = Json.createObjectBuilder()
            .add("Ticker", stock.mi.getTicker())
            .add("CompanyName", stock.mi.getCompanyName())
            .add("LastClosePrice", stock.mi.getLastPrice())
            .add("DatePulled", Utils.dateToEpochStr(stock.mi.getToday())) //epochmili string
            .add("URL", stock.mi.getHtm())
            .add("FiscalYear", stock.mi.getFiscalYear())
            .add("FiscalQuarter", stock.mi.getQuarter())
            .add("DocumentType", stock.mi.getDocumentType())
            .build();

        JsonObjectBuilder activityRatiosBuilder = Json.createObjectBuilder();
        double inventoryTurnover = stock.ar.getInventoryTurnover();

        if (Double.isInfinite(inventoryTurnover)) {
            activityRatiosBuilder.add("InventoryTurnover", "Infinity");
        } else {
            activityRatiosBuilder.add("InventoryTurnover", inventoryTurnover);
        }
            activityRatiosBuilder
                .add("DaysInventoryOnHand", stock.ar.getDaysInventoryOnHand())
                .add("ReceivablesTurnover", stock.ar.getReceivablesTurnover())
                .add("DaysSalesOutstanding", stock.ar.getDaysSalesOutstanding())
                .add("PayablesTurnover", stock.ar.getPayablesTurnover())
                .add("DaysPayables", stock.ar.getDaysPayables())
                .add("WorkingCapitalTurnover", stock.ar.getWorkingCapitalTurnover())
                .add("FixedAssetTurnover", stock.ar.getFixedAssetTurnover())
                .add("TotalAssetTurnover", stock.ar.getTotalAssetTurnover());
        JsonObject activityRatios = activityRatiosBuilder.build();

        JsonObject liquidityRatios = Json.createObjectBuilder()
            .add("CurrentRatio", stock.lr.getCurrentRatio())
            .add("QuickRatio", stock.lr.getQuickRatio())
            .add("CashRatio", stock.lr.getCashRatio())
            .add("CashConversionCycle", stock.lr.getCashConverionCycle())
            .build();
            
        JsonObject profitabilityRatios = Json.createObjectBuilder()
            .add("GrossProfitMargin", stock.pr.getGrossProfitMargin())
            .add("OperatingProfitMargin", stock.pr.getOperatingProfitMargin())
            .add("PreTaxMargin", stock.pr.getPreTaxMargin())
            .add("NetProfitMargin", stock.pr.getNetProfitMargin())
            .add("OperatingReturnOnAssets", stock.pr.getOperatingROA())
            .add("ReturnOnAssets", stock.pr.getRoAssets())
            .add("ReturnOnCatpial", stock.pr.getRoCapital())
            .add("ReturnOnEquity", stock.pr.getRoTotalEquity())
            .build();
              
        JsonObject solvencyRatios = Json.createObjectBuilder()
            .add("DebtToAssets", stock.sr.getDebtToAssets())
            .add("DebtToCapital", stock.sr.getDebtToCapital())
            .add("DebtToEquity", stock.sr.getDebtToEquity())
            .add("FinancialLeverage", stock.sr.getFinancialLeverage())
            .build();

        JsonObject valuationRatios = Json.createObjectBuilder()
            .add("PriceToEarningsBasic", stock.vr.getPeBasic())
            .add("PriceToEarningsDiluted", stock.vr.getPeDiluted())
            .add("PriceToSales", stock.vr.getPriceToSales())
            .add("DividendPayoutRatio", stock.vr.getDividendPayoutRatio())
            .add("RetentionRate", stock.vr.getRetentionRate())
            .add("SustainableGrowthRate", stock.vr.getSustainableGrowthRate())
            .build();          

        JsonObjectBuilder stockJsonObjBuilder = Json.createObjectBuilder();
        stockJsonObjBuilder = stockJsonObjBuilder
            .add("MiscellaneousItems", micsItems)
            .add("ActivityRatios", activityRatios)
            .add("LiquidityRatios", liquidityRatios)
            .add("ProfitabilityRatios", profitabilityRatios)
            .add("SolvencyRatios", solvencyRatios)
            .add("ValuationRatios", valuationRatios);
        JsonObject stockJsonObj = stockJsonObjBuilder.build();
        return stockJsonObj;

    }
    


}
