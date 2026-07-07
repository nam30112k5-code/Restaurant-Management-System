using RestaurantManagement.Models;

namespace RestaurantManagement.Services;

public interface IRmsService
{
    Task<List<Employee>> GetEmployeesAsync();
    Task<Employee?> GetEmployeeByIdAsync(int employeeId);
    Task AddEmployeeAsync(Employee employee, string password);
    Task UpdateEmployeeAsync(Employee employee);
    Task DeleteEmployeeAsync(int employeeId);

    Task<List<Guest>> GetGuestsAsync();
    Task<Guest?> GetGuestByIdAsync(int guestId);
    Task AddGuestAsync(Guest guest, string password);
    Task UpdateGuestAsync(Guest guest);
    Task SetGuestStatusAsync(int guestId, bool isActive);
    Task ResetGuestPasswordAsync(int guestId, string newPassword);
    Task DeleteGuestAsync(int guestId);

    Task<List<Table>> GetTablesAsync();
    Task AddTableAsync(string tableName);
    Task UpdateTableAsync(Table table);
    Task DeleteTableAsync(int tableId);
    Task<List<TableStatus>> GetTableStatusAsync(DateTime date, TimeSpan startTime, TimeSpan endTime);

    Task<List<Appointment>> GetAppointmentsAsync();
    Task<List<Appointment>> GetBookingHistoryAsync(int guestId);
    Task<bool> HasPendingBookingAsync(int guestId);
    Task CreateBookingAsync(int guestId, int tableId, DateTime date, TimeSpan startTime, TimeSpan endTime, string status, string? createdBy);
    Task<int> CreateOrGetWalkInGuestAsync(string name, string phoneNumber);
    Task UpdateBookingStatusAsync(Guid appointmentId, string status);
    Task CancelBookingAsync(Guid appointmentId);

    Task<List<Feedback>> GetFeedbacksAsync();
    Task AddFeedbackAsync(int guestId, Guid appointmentId, string content, int rating);

    Task UpdateProfileAsync(UserAccount accountMember, string? name, string? identityNumber, string? phoneNumber, DateTime? dateOfBirth, string? gender);
    Task<bool> ChangePasswordAsync(UserAccount accountMember, string oldPassword, string newPassword);
}
