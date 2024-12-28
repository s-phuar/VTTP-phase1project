package VTTPmini.mini_project.model;

public class ActivityRatios {
    private double inventoryTurnover;
    private double daysInventoryOnHand;
    private double receivablesTurnover;
    private double daysSalesOutstanding;
    private double payablesTurnover;
    private double daysPayables;
    private double workingCapitalTurnover;
    private double fixedAssetTurnover;
    private double totalAssetTurnover;

    public double getInventoryTurnover() {return inventoryTurnover;}
    public void setInventoryTurnover(double inventoryTurnover) {this.inventoryTurnover = inventoryTurnover;}
    public double getDaysInventoryOnHand() {return daysInventoryOnHand;}
    public void setDaysInventoryOnHand(double daysInventoryOnHand) {this.daysInventoryOnHand = daysInventoryOnHand;}
    public double getReceivablesTurnover() {return receivablesTurnover;}
    public void setReceivablesTurnover(double receivablesTurnover) {this.receivablesTurnover = receivablesTurnover;}
    public double getDaysSalesOutstanding() {return daysSalesOutstanding;}
    public void setDaysSalesOutstanding(double daysSalesOutstanding) {this.daysSalesOutstanding = daysSalesOutstanding;}
    public double getPayablesTurnover() {return payablesTurnover;}
    public void setPayablesTurnover(double payablesTurnover) {this.payablesTurnover = payablesTurnover;}
    public double getDaysPayables() {return daysPayables;}
    public void setDaysPayables(double daysPayables) {this.daysPayables = daysPayables;}
    public double getWorkingCapitalTurnover() {return workingCapitalTurnover;}
    public void setWorkingCapitalTurnover(double workingCapitalTurnover) {this.workingCapitalTurnover = workingCapitalTurnover;}
    public double getFixedAssetTurnover() {return fixedAssetTurnover;}
    public void setFixedAssetTurnover(double fixedAssetTurnover) {this.fixedAssetTurnover = fixedAssetTurnover;}
    public double getTotalAssetTurnover() {return totalAssetTurnover;}
    public void setTotalAssetTurnover(double totalAssetTurnover) {this.totalAssetTurnover = totalAssetTurnover;}
    
    @Override
    public String toString() {
        return "ActivityRatios [inventoryTurnover=" + inventoryTurnover + ", daysInventoryOnHand=" + daysInventoryOnHand
                + ", receivablesTurnover=" + receivablesTurnover + ", daysSalesOutstanding=" + daysSalesOutstanding
                + ", payablesTurnover=" + payablesTurnover + ", daysPayables=" + daysPayables
                + ", workingCapitalTurnover=" + workingCapitalTurnover + ", fixedAssetTurnover=" + fixedAssetTurnover
                + ", totalAssetTurnover=" + totalAssetTurnover + "]";
    }
    

    



    

    

}
