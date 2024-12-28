package VTTPmini.mini_project.model;

import java.util.Date;

public class MiscItems {
    private String ticker;
    private String companyName;
    private double lastPrice;
    private Date today;
    private String htm;
    private String fiscalYear;
    private String quarter;
    private String documentType;
    
    public String getTicker() {return ticker;}
    public void setTicker(String ticker) {this.ticker = ticker;}
    public String getCompanyName() {return companyName;}
    public void setCompanyName(String companyName) {this.companyName = companyName;}
    public double getLastPrice() {return lastPrice;}
    public void setLastPrice(double lastPrice) {this.lastPrice = lastPrice;}
    public Date getToday() {return today;}
    public void setToday(Date today) {this.today = today;}
    public String getHtm() {return htm;}
    public void setHtm(String htm) {this.htm = htm;}
    public String getFiscalYear() {return fiscalYear;}
    public void setFiscalYear(String fiscalYear) {this.fiscalYear = fiscalYear;}
    public String getQuarter() {return quarter;}
    public void setQuarter(String quarter) {this.quarter = quarter;}
    public String getDocumentType() {return documentType;}
    public void setDocumentType(String documentType) {this.documentType = documentType;}

    

}
