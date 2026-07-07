namespace Models;

public class Feedback
{
    public int FeedbackId { get; set; }
    public string Content { get; set; } = string.Empty;
    public int Rating { get; set; }
    public int GuestId { get; set; }
    public Guid AppointmentId { get; set; }

    public Guest? Guest { get; set; }
    public Appointment? Appointment { get; set; }
}
