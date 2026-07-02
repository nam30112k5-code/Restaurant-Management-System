namespace RestaurantManagement.Models;

public class RestaurantTable
{
    public int TableId { get; set; }
    public string TableName { get; set; } = string.Empty;

    public ICollection<Appointment> Appointments { get; set; } = new List<Appointment>();
}
