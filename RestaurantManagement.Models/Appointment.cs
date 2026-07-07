namespace RestaurantManagement.Models;

public class Appointment
{
    public Guid AppointmentId { get; set; }
    public int? GuestId { get; set; }
    public int? TableId { get; set; }
    public string? CreateBy { get; set; }
    public TimeSpan? StartTime { get; set; }
    public TimeSpan? EndTime { get; set; }
    public DateTime? Date { get; set; }
    public string? Status { get; set; }

    public Guest? Guest { get; set; }
    public Table? Table { get; set; }
    public ICollection<Feedback> Feedbacks { get; set; } = new List<Feedback>();
}
