package VTTPmini.mini_project.model;

public class SolvencyRatios {
    private double debtToAssets;
    private double debtToCapital;
    private double debtToEquity;
    private double financialLeverage;

    public double getDebtToAssets() {return debtToAssets;}
    public void setDebtToAssets(double debtToAssets) {this.debtToAssets = debtToAssets;}
    public double getDebtToCapital() {return debtToCapital;}
    public void setDebtToCapital(double debtToCapital) {this.debtToCapital = debtToCapital;}
    public double getDebtToEquity() {return debtToEquity;}
    public void setDebtToEquity(double debtToEquity) {this.debtToEquity = debtToEquity;}
    public double getFinancialLeverage() {return financialLeverage;}
    public void setFinancialLeverage(double financialLeverage) {this.financialLeverage = financialLeverage;}
    
    @Override
    public String toString() {
        return "SolvencyRatios [debtToAssets=" + debtToAssets + ", debtToCapital=" + debtToCapital + ", debtToEquity="
                + debtToEquity + ", financialLeverage=" + financialLeverage + "]";
    }

    


}
