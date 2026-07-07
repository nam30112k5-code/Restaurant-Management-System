namespace Models;

public class Guest
{
    public int GuestId { get; set; }
    public string? Username { get; set; }
    public string Password { get; set; } = string.Empty;
    public string? Name { get; set; }
    public string? IdentityNumber { get; set; }
    public string? PhoneNumber { get; set; }
    public DateTime? DateOfBirth { get; set; }
    public string? Gender { get; set; }
    public bool IsActive { get; set; }

    public ICollection<Appointment> Appointments { get; set; } = new List<Appointment>();
    public ICollection<Feedback> Feedbacks { get; set; } = new List<Feedback>();
}
