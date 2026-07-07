using Microsoft.EntityFrameworkCore;
using RestaurantManagement.Data;
using RestaurantManagement.Models;

namespace RestaurantManagement.Services;

public class RmsService : IRmsService
{
    private const string DefaultWalkInPassword = "123";
    private readonly AppDb _dbContext;
    private readonly IPasswordHasher _passwordHasher;

    public RmsService(AppDb dbContext, IPasswordHasher passwordHasher)
    {
        _dbContext = dbContext;
        _passwordHasher = passwordHasher;
    }

    public Task<List<Employee>> GetEmployeesAsync()
        => _dbContext.Employees.AsNoTracking().OrderBy(employee => employee.EmployeeId).ToListAsync();

    public Task<Employee?> GetEmployeeByIdAsync(int employeeId)
        => _dbContext.Employees.AsNoTracking().FirstOrDefaultAsync(employee => employee.EmployeeId == employeeId);

    public async Task AddEmployeeAsync(Employee employee, string password)
    {
        ValidateUsername(employee.Username, 10);
        ValidateRequiredPassword(password);
        ValidatePersonFields(employee.IdentityNumber, employee.PhoneNumber, employee.Gender);
        employee.Password = _passwordHasher.Hash(password);
        employee.Username = employee.Username.Trim();
        _dbContext.Employees.Add(employee);
        await _dbContext.SaveChangesAsync();
    }

    public async Task UpdateEmployeeAsync(Employee employee)
    {
        var existingEmployee = await _dbContext.Employees.FindAsync(employee.EmployeeId)
            ?? throw new InvalidOperationException("Employee not found.");

        existingEmployee.IdentityNumber = employee.IdentityNumber;
        existingEmployee.PhoneNumber = employee.PhoneNumber;
        existingEmployee.DateOfBirth = employee.DateOfBirth;
        existingEmployee.Gender = employee.Gender;
        existingEmployee.RoleId = employee.RoleId;
        existingEmployee.IsActive = employee.IsActive;
        ValidatePersonFields(existingEmployee.IdentityNumber, existingEmployee.PhoneNumber, existingEmployee.Gender);
        await _dbContext.SaveChangesAsync();
    }

    public async Task DeleteEmployeeAsync(int employeeId)
    {
        var employee = await _dbContext.Employees.FindAsync(employeeId)
            ?? throw new InvalidOperationException("Employee not found.");

        var relatedAppointments = await _dbContext.Appointments
            .Where(appointment => appointment.CreateBy == employee.Username)
            .ToListAsync();
        foreach (var appointment in relatedAppointments)
        {
            appointment.CreateBy = null;
        }

        _dbContext.Employees.Remove(employee);
        await _dbContext.SaveChangesAsync();
    }

    public Task<List<Guest>> GetGuestsAsync()
        => _dbContext.Guests.AsNoTracking().OrderBy(guest => guest.GuestId).ToListAsync();

    public Task<Guest?> GetGuestByIdAsync(int guestId)
        => _dbContext.Guests.AsNoTracking().FirstOrDefaultAsync(guest => guest.GuestId == guestId);

    public async Task AddGuestAsync(Guest guest, string password)
    {
        ValidateUsername(guest.Username, 50);
        ValidateRequiredPassword(password);
        ValidatePersonFields(guest.IdentityNumber, guest.PhoneNumber, guest.Gender);
        guest.Password = _passwordHasher.Hash(password);
        guest.Username = guest.Username?.Trim();
        guest.Name = guest.Name?.Trim();
        guest.IsActive = true;
        _dbContext.Guests.Add(guest);
        await _dbContext.SaveChangesAsync();
    }

    public async Task UpdateGuestAsync(Guest guest)
    {
        var existingGuest = await _dbContext.Guests.FindAsync(guest.GuestId)
            ?? throw new InvalidOperationException("Guest not found.");

        ValidatePersonFields(guest.IdentityNumber, guest.PhoneNumber, guest.Gender);
        existingGuest.Name = guest.Name?.Trim();
        existingGuest.IdentityNumber = guest.IdentityNumber;
        existingGuest.PhoneNumber = guest.PhoneNumber;
        existingGuest.DateOfBirth = guest.DateOfBirth;
        existingGuest.Gender = guest.Gender;
        await _dbContext.SaveChangesAsync();
    }

    public async Task SetGuestStatusAsync(int guestId, bool isActive)
    {
        var guest = await _dbContext.Guests.FindAsync(guestId)
            ?? throw new InvalidOperationException("Guest not found.");
        guest.IsActive = isActive;
        await _dbContext.SaveChangesAsync();
    }

    public async Task ResetGuestPasswordAsync(int guestId, string newPassword)
    {
        var guest = await _dbContext.Guests.FindAsync(guestId)
            ?? throw new InvalidOperationException("Guest not found.");
        guest.Password = _passwordHasher.Hash(newPassword);
        await _dbContext.SaveChangesAsync();
    }

    public async Task DeleteGuestAsync(int guestId)
    {
        var feedbacks = await _dbContext.Feedbacks.Where(feedback => feedback.GuestId == guestId).ToListAsync();
        var appointments = await _dbContext.Appointments.Where(appointment => appointment.GuestId == guestId).ToListAsync();
        var guest = await _dbContext.Guests.FindAsync(guestId)
            ?? throw new InvalidOperationException("Guest not found.");

        _dbContext.Feedbacks.RemoveRange(feedbacks);
        _dbContext.Appointments.RemoveRange(appointments);
        _dbContext.Guests.Remove(guest);
        await _dbContext.SaveChangesAsync();
    }

    public Task<List<Table>> GetTablesAsync()
        => _dbContext.Tables.AsNoTracking().OrderBy(table => table.TableId).ToListAsync();

    public async Task AddTableAsync(string tableName)
    {
        if (string.IsNullOrWhiteSpace(tableName))
        {
            throw new InvalidOperationException("Table name is required.");
        }

        _dbContext.Tables.Add(new Table { TableName = tableName.Trim() });
        await _dbContext.SaveChangesAsync();
    }

    public async Task UpdateTableAsync(Table table)
    {
        var existingTable = await _dbContext.Tables.FindAsync(table.TableId)
            ?? throw new InvalidOperationException("Table not found.");
        existingTable.TableName = table.TableName.Trim();
        await _dbContext.SaveChangesAsync();
    }

    public async Task DeleteTableAsync(int tableId)
    {
        var table = await _dbContext.Tables.FindAsync(tableId)
            ?? throw new InvalidOperationException("Table not found.");

        var hasBookings = await _dbContext.Appointments.AnyAsync(appointment => appointment.TableId == tableId);
        if (hasBookings)
        {
            throw new InvalidOperationException("Cannot delete a table that already has bookings.");
        }

        _dbContext.Tables.Remove(table);
        await _dbContext.SaveChangesAsync();
    }

    public async Task<List<TableStatus>> GetTableStatusAsync(DateTime date, TimeSpan startTime, TimeSpan endTime)
    {
        var tables = await _dbContext.Tables.AsNoTracking().OrderBy(table => table.TableId).ToListAsync();
        var appointments = await _dbContext.Appointments
            .AsNoTracking()
            .Include(appointment => appointment.Guest)
            .Where(appointment =>
                appointment.Date == date.Date &&
                appointment.Status != "cancelled" &&
                appointment.StartTime.HasValue &&
                appointment.EndTime.HasValue &&
                appointment.StartTime.Value < endTime &&
                appointment.EndTime.Value > startTime)
            .ToListAsync();

        return tables.Select(table =>
        {
            var booking = appointments.FirstOrDefault(appointment => appointment.TableId == table.TableId);
            return new TableStatus
            {
                TableId = table.TableId,
                TableName = table.TableName,
                IsBooked = booking is not null,
                GuestName = booking?.Guest?.Name ?? "-",
                TimeRange = booking is null ? "-" : $"{booking.StartTime:hh\\:mm} - {booking.EndTime:hh\\:mm}"
            };
        }).ToList();
    }

    public Task<List<Appointment>> GetAppointmentsAsync()
        => _dbContext.Appointments
            .AsNoTracking()
            .Include(appointment => appointment.Guest)
            .Include(appointment => appointment.Table)
            .OrderByDescending(appointment => appointment.Date)
            .ThenByDescending(appointment => appointment.StartTime)
            .ToListAsync();

    public Task<List<Appointment>> GetBookingHistoryAsync(int guestId)
        => _dbContext.Appointments
            .AsNoTracking()
            .Include(appointment => appointment.Table)
            .Include(appointment => appointment.Feedbacks)
            .Where(appointment => appointment.GuestId == guestId)
            .OrderByDescending(appointment => appointment.Date)
            .ThenByDescending(appointment => appointment.StartTime)
            .ToListAsync();

    public Task<bool> HasPendingBookingAsync(int guestId)
        => _dbContext.Appointments.AnyAsync(appointment => appointment.GuestId == guestId && appointment.Status == "pending");

    public async Task CreateBookingAsync(
        int guestId,
        int tableId,
        DateTime date,
        TimeSpan startTime,
        TimeSpan endTime,
        string status,
        string? createdBy)
    {
        if (startTime >= endTime)
        {
            throw new InvalidOperationException("End time must be after start time.");
        }

        if (date.Date.Add(startTime) < DateTime.Now)
        {
            throw new InvalidOperationException("Cannot book a table in the past.");
        }

        var guestExists = await _dbContext.Guests.AnyAsync(guest => guest.GuestId == guestId && guest.IsActive);
        if (!guestExists)
        {
            throw new InvalidOperationException("Active guest not found.");
        }

        var tableExists = await _dbContext.Tables.AnyAsync(table => table.TableId == tableId);
        if (!tableExists)
        {
            throw new InvalidOperationException("Table not found.");
        }

        var isTableBooked = await _dbContext.Appointments.AnyAsync(appointment =>
            appointment.TableId == tableId &&
            appointment.Date == date.Date &&
            appointment.Status != "cancelled" &&
            appointment.StartTime.HasValue &&
            appointment.EndTime.HasValue &&
            appointment.StartTime.Value < endTime &&
            appointment.EndTime.Value > startTime);

        if (isTableBooked)
        {
            throw new InvalidOperationException("This table is already booked in the selected time range.");
        }

        _dbContext.Appointments.Add(new Appointment
        {
            GuestId = guestId,
            TableId = tableId,
            Date = date.Date,
            StartTime = startTime,
            EndTime = endTime,
            Status = status,
            CreateBy = createdBy
        });
        await _dbContext.SaveChangesAsync();
    }

    public async Task<int> CreateOrGetWalkInGuestAsync(string name, string phoneNumber)
    {
        if (string.IsNullOrWhiteSpace(name) || string.IsNullOrWhiteSpace(phoneNumber))
        {
            throw new InvalidOperationException("Guest name and phone are required.");
        }

        var trimmedPhone = phoneNumber.Trim();
        ValidatePhoneNumber(trimmedPhone);
        var existingGuest = await _dbContext.Guests.FirstOrDefaultAsync(guest => guest.PhoneNumber == trimmedPhone);
        if (existingGuest is not null)
        {
            return existingGuest.GuestId;
        }

        var guest = new Guest
        {
            Username = $"G{trimmedPhone}",
            Password = _passwordHasher.Hash(DefaultWalkInPassword),
            Name = name.Trim(),
            PhoneNumber = trimmedPhone,
            IdentityNumber = trimmedPhone.Length <= 12 ? trimmedPhone : trimmedPhone[..12],
            DateOfBirth = new DateTime(2000, 1, 1),
            Gender = "male",
            IsActive = true
        };

        _dbContext.Guests.Add(guest);
        await _dbContext.SaveChangesAsync();
        return guest.GuestId;
    }

    public async Task UpdateBookingStatusAsync(Guid appointmentId, string status)
    {
        var appointment = await _dbContext.Appointments.FindAsync(appointmentId)
            ?? throw new InvalidOperationException("Booking not found.");
        if (appointment.Status == "completed" && status != "completed")
        {
            throw new InvalidOperationException("Completed bookings cannot be changed.");
        }

        if (appointment.Status == "cancelled" && status != "cancelled")
        {
            throw new InvalidOperationException("Cancelled bookings cannot be changed.");
        }

        appointment.Status = status;
        await _dbContext.SaveChangesAsync();
    }

    public Task CancelBookingAsync(Guid appointmentId)
        => UpdateBookingStatusAsync(appointmentId, "cancelled");

    public Task<List<Feedback>> GetFeedbacksAsync()
        => _dbContext.Feedbacks
            .AsNoTracking()
            .Include(feedback => feedback.Guest)
            .Include(feedback => feedback.Appointment)
            .ThenInclude(appointment => appointment!.Table)
            .OrderByDescending(feedback => feedback.FeedbackId)
            .ToListAsync();

    public async Task AddFeedbackAsync(int guestId, Guid appointmentId, string content, int rating)
    {
        if (rating is < 1 or > 5)
        {
            throw new InvalidOperationException("Rating must be from 1 to 5.");
        }

        if (string.IsNullOrWhiteSpace(content))
        {
            throw new InvalidOperationException("Feedback content is required.");
        }

        var appointment = await _dbContext.Appointments
            .AsNoTracking()
            .FirstOrDefaultAsync(item => item.AppointmentId == appointmentId && item.GuestId == guestId)
            ?? throw new InvalidOperationException("Booking not found.");

        if (appointment.Status != "completed")
        {
            throw new InvalidOperationException("Only completed bookings can receive feedback.");
        }

        var alreadyRated = await _dbContext.Feedbacks.AnyAsync(feedback => feedback.AppointmentId == appointmentId);
        if (alreadyRated)
        {
            throw new InvalidOperationException("This booking already has feedback.");
        }

        _dbContext.Feedbacks.Add(new Feedback
        {
            GuestId = guestId,
            AppointmentId = appointmentId,
            Content = content.Trim(),
            Rating = rating
        });
        await _dbContext.SaveChangesAsync();
    }

    public async Task UpdateProfileAsync(
        UserAccount accountMember,
        string? name,
        string? identityNumber,
        string? phoneNumber,
        DateTime? dateOfBirth,
        string? gender)
    {
        if (accountMember.AccountType == AccountType.Guest)
        {
            var guest = await _dbContext.Guests.FindAsync(accountMember.Id)
                ?? throw new InvalidOperationException("Guest not found.");
            ValidatePersonFields(identityNumber, phoneNumber, gender);
            guest.Name = name;
            guest.IdentityNumber = identityNumber;
            guest.PhoneNumber = phoneNumber;
            guest.DateOfBirth = dateOfBirth;
            guest.Gender = gender;
        }
        else
        {
            var employee = await _dbContext.Employees.FindAsync(accountMember.Id)
                ?? throw new InvalidOperationException("Employee not found.");
            ValidatePersonFields(identityNumber, phoneNumber, gender);
            employee.IdentityNumber = identityNumber;
            employee.PhoneNumber = phoneNumber;
            employee.DateOfBirth = dateOfBirth;
            employee.Gender = gender;
        }

        await _dbContext.SaveChangesAsync();
    }

    public async Task<bool> ChangePasswordAsync(UserAccount accountMember, string oldPassword, string newPassword)
    {
        ValidateRequiredPassword(newPassword);

        if (accountMember.AccountType == AccountType.Guest)
        {
            var guest = await _dbContext.Guests.FindAsync(accountMember.Id)
                ?? throw new InvalidOperationException("Guest not found.");
            if (!_passwordHasher.Verify(oldPassword, guest.Password))
            {
                return false;
            }

            guest.Password = _passwordHasher.Hash(newPassword);
        }
        else
        {
            var employee = await _dbContext.Employees.FindAsync(accountMember.Id)
                ?? throw new InvalidOperationException("Employee not found.");
            if (!_passwordHasher.Verify(oldPassword, employee.Password))
            {
                return false;
            }

            employee.Password = _passwordHasher.Hash(newPassword);
        }

        await _dbContext.SaveChangesAsync();
        return true;
    }

    private static void ValidateUsername(string? username, int maxLength)
    {
        if (string.IsNullOrWhiteSpace(username))
        {
            throw new InvalidOperationException("Username is required.");
        }

        if (username.Length > maxLength)
        {
            throw new InvalidOperationException($"Username cannot exceed {maxLength} characters.");
        }
    }

    private static void ValidateRequiredPassword(string? password)
    {
        if (string.IsNullOrWhiteSpace(password))
        {
            throw new InvalidOperationException("Password is required.");
        }
    }

    private static void ValidatePersonFields(string? identityNumber, string? phoneNumber, string? gender)
    {
        if (!string.IsNullOrWhiteSpace(identityNumber) && identityNumber.Trim().Length > 12)
        {
            throw new InvalidOperationException("Identity number cannot exceed 12 characters.");
        }

        if (!string.IsNullOrWhiteSpace(phoneNumber))
        {
            ValidatePhoneNumber(phoneNumber);
        }

        if (!string.IsNullOrWhiteSpace(gender) && gender.Trim().Length > 6)
        {
            throw new InvalidOperationException("Gender cannot exceed 6 characters.");
        }
    }

    private static void ValidatePhoneNumber(string phoneNumber)
    {
        var trimmedPhone = phoneNumber.Trim();
        if (trimmedPhone.Length > 10 || trimmedPhone.Any(character => !char.IsDigit(character)))
        {
            throw new InvalidOperationException("Phone number must contain up to 10 digits.");
        }
    }
}
