namespace RestaurantManagement.Models;

public class Table
{
    public int TableId { get; set; }
    public string TableName { get; set; } = string.Empty;

    public ICollection<Appointment> Appointments { get; set; } = new List<Appointment>();
}
