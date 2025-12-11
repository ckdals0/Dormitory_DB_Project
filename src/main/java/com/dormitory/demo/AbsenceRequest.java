package com.dormitory.demo;

public class AbsenceRequest {
    private String startDate;
    private String returnDate;
    private String reason;

    // Getter, Setter
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}