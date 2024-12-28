package VTTPmini.mini_project.model;

public class LiquidityRatios {
    private double currentRatio;
    private double quickRatio;
    private double cashRatio;
    private double cashConverionCycle;

    public double getCurrentRatio() {return currentRatio;}
    public void setCurrentRatio(double currentRatio) {this.currentRatio = currentRatio;}
    public double getQuickRatio() {return quickRatio;}
    public void setQuickRatio(double quickRatio) {this.quickRatio = quickRatio;}
    public double getCashRatio() {return cashRatio;}
    public void setCashRatio(double cashRatio) {this.cashRatio = cashRatio;}
    public double getCashConverionCycle() {return cashConverionCycle;}
    public void setCashConverionCycle(double cashConverionCycle) {this.cashConverionCycle = cashConverionCycle;}
    
    @Override
    public String toString() {
        return "LiquidityRatios [currentRatio=" + currentRatio + ", quickRatio=" + quickRatio + ", cashRatio="
                + cashRatio + ", cashConverionCycle=" + cashConverionCycle + "]";
    }

    


}
