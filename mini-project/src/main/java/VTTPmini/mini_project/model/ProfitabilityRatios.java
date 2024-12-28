package VTTPmini.mini_project.model;

public class ProfitabilityRatios {

    private double grossProfitMargin;
    private double operatingProfitMargin;
    private double preTaxMargin;
    private double netProfitMargin;
    private double operatingROA;
    private double roAssets;
    private double roCapital;
    private double roTotalEquity;

    public double getGrossProfitMargin() {return grossProfitMargin;}
    public void setGrossProfitMargin(double grossProfitMargin) {this.grossProfitMargin = grossProfitMargin;}
    public double getOperatingProfitMargin() {return operatingProfitMargin;}
    public void setOperatingProfitMargin(double operatingProfitMargin) {this.operatingProfitMargin = operatingProfitMargin;}
    public double getPreTaxMargin() {return preTaxMargin;}
    public void setPreTaxMargin(double preTaxMargin) {this.preTaxMargin = preTaxMargin;}
    public double getNetProfitMargin() {return netProfitMargin;}
    public void setNetProfitMargin(double netProfitMargin) {this.netProfitMargin = netProfitMargin;}
    public double getOperatingROA() {return operatingROA;}
    public void setOperatingROA(double operatingROA) {this.operatingROA = operatingROA;}
    public double getRoAssets() {return roAssets;}
    public void setRoAssets(double roAssets) {this.roAssets = roAssets;}
    public double getRoCapital() {return roCapital;}
    public void setRoCapital(double roCapital) {this.roCapital = roCapital;}
    public double getRoTotalEquity() {return roTotalEquity;}
    public void setRoTotalEquity(double roTotalEquity) {this.roTotalEquity = roTotalEquity;}
    
    @Override
    public String toString() {
        return "ProfitabilityRatios [grossProfitMargin=" + grossProfitMargin + ", operatingProfitMargin="
                + operatingProfitMargin + ", preTaxMargin=" + preTaxMargin + ", netProfitMargin=" + netProfitMargin
                + ", operatingROA=" + operatingROA + ", roAssets=" + roAssets + ", roCapital=" + roCapital
                + ", roTotalEquity=" + roTotalEquity + "]";
    }



    

    
}
