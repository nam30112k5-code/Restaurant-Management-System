package model;

public class Table {

    private int tableId;
    private String tableName;
    private boolean isBooked;
    private String currentGuestName;
    private String timeRange;

    public Table() {
    }

    public Table(int tableId, String tableName) {
        this.tableId = tableId;
        this.tableName = tableName;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public String getCurrentGuestName() {
        return currentGuestName;
    }

    public void setCurrentGuestName(String currentGuestName) {
        this.currentGuestName = currentGuestName;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

}
