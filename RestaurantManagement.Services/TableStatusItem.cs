namespace RestaurantManagement.Services;

public class TableStatusItem
{
    public int TableId { get; set; }
    public string TableName { get; set; } = string.Empty;
    public bool IsBooked { get; set; }
    public string GuestName { get; set; } = "-";
    public string TimeRange { get; set; } = "-";
}
