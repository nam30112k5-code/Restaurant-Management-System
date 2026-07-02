namespace RestaurantManagement.Models;

public class Employee
{
    public int EmployeeId { get; set; }
    public string Username { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string? IdentityNumber { get; set; }
    public string? PhoneNumber { get; set; }
    public DateTime? DateOfBirth { get; set; }
    public string? Gender { get; set; }
    public bool IsActive { get; set; }
    public int? RoleId { get; set; }

    public Role? Role { get; set; }
}
