package edu.espe.springlab.dto;

public class InventoryStatsResponse {
    private long total;
    private long available;
    private long unavailable;

    public InventoryStatsResponse(long total, long available, long unavailable) {
        this.total = total;
        this.available = available;
        this.unavailable = unavailable;
    }

    public long getTotal() { return total; }
    public long getAvailable() { return available; }
    public long getUnavailable() { return unavailable; }
}