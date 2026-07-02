using System.Collections.ObjectModel;
using System.Windows.Input;
using RestaurantManagement.Commands;
using RestaurantManagement.Models;
using RestaurantManagement.Services;

namespace RestaurantManagement.ViewModels;

public class ShellViewModel : ViewModelBase
{
    private readonly IRestaurantService _restaurantService;
    private readonly IUserSession _userSession;
    private string _selectedMenuItem = "Dashboard";
    private string? _message;
    private Employee? _selectedEmployee;
    private Guest? _selectedGuest;
    private RestaurantTable? _selectedTable;
    private Appointment? _selectedAppointment;
    private Appointment? _selectedHistory;
    private DateTime _selectedDate = DateTime.Today;
    private string _startTimeText = "18:00";
    private string _endTimeText = "20:00";

    public ShellViewModel(IRestaurantService restaurantService, IUserSession userSession)
    {
        _restaurantService = restaurantService;
        _userSession = userSession;

        MenuItems = new ObservableCollection<string>();
        Employees = new ObservableCollection<Employee>();
        Guests = new ObservableCollection<Guest>();
        Tables = new ObservableCollection<RestaurantTable>();
        TableStatuses = new ObservableCollection<TableStatusItem>();
        Appointments = new ObservableCollection<Appointment>();
        BookingHistory = new ObservableCollection<Appointment>();
        Feedbacks = new ObservableCollection<Feedback>();

        NavigateCommand = new AsyncRelayCommand(NavigateAsync);
        LogoutCommand = new AsyncRelayCommand(LogoutAsync);
        SaveEmployeeCommand = new AsyncRelayCommand(SaveEmployeeAsync);
        DeleteEmployeeCommand = new AsyncRelayCommand(DeleteEmployeeAsync);
        NewEmployeeCommand = new AsyncRelayCommand(NewEmployeeAsync);
        SaveGuestCommand = new AsyncRelayCommand(SaveGuestAsync);
        ToggleGuestStatusCommand = new AsyncRelayCommand(ToggleGuestStatusAsync);
        DeleteGuestCommand = new AsyncRelayCommand(DeleteGuestAsync);
        ResetGuestPasswordCommand = new AsyncRelayCommand(ResetGuestPasswordAsync);
        SaveTableCommand = new AsyncRelayCommand(SaveTableAsync);
        DeleteTableCommand = new AsyncRelayCommand(DeleteTableAsync);
        NewTableCommand = new AsyncRelayCommand(NewTableAsync);
        RefreshTableStatusCommand = new AsyncRelayCommand(LoadTableStatusAsync);
        CreateWalkInBookingCommand = new AsyncRelayCommand(CreateWalkInBookingAsync);
        CreateCustomerBookingCommand = new AsyncRelayCommand(CreateCustomerBookingAsync);
        ConfirmBookingCommand = new AsyncRelayCommand(_ => UpdateSelectedBookingStatusAsync("confirmed"));
        CancelBookingCommand = new AsyncRelayCommand(_ => UpdateSelectedBookingStatusAsync("cancelled"));
        CompleteBookingCommand = new AsyncRelayCommand(_ => UpdateSelectedBookingStatusAsync("completed"));
        CancelHistoryBookingCommand = new AsyncRelayCommand(CancelHistoryBookingAsync);
        SubmitFeedbackCommand = new AsyncRelayCommand(SubmitFeedbackAsync);
        SaveProfileCommand = new AsyncRelayCommand(SaveProfileAsync);
        ChangePasswordCommand = new AsyncRelayCommand(ChangePasswordAsync);
    }

    public event EventHandler? LogoutRequested;

    public AccountMember? CurrentAccount => _userSession.CurrentUser;
    public string WelcomeText => CurrentAccount is null ? "Welcome" : $"Welcome, {CurrentAccount.DisplayName}";
    public bool IsAdmin => CurrentAccount?.AccountType == AccountType.Employee && CurrentAccount.RoleId == RoleIds.Admin;
    public bool IsStaff => CurrentAccount?.AccountType == AccountType.Employee && CurrentAccount.RoleId == RoleIds.Staff;
    public bool IsGuest => CurrentAccount?.AccountType == AccountType.Guest;

    public ObservableCollection<string> MenuItems { get; }
    public ObservableCollection<Employee> Employees { get; }
    public ObservableCollection<Guest> Guests { get; }
    public ObservableCollection<RestaurantTable> Tables { get; }
    public ObservableCollection<TableStatusItem> TableStatuses { get; }
    public ObservableCollection<Appointment> Appointments { get; }
    public ObservableCollection<Appointment> BookingHistory { get; }
    public ObservableCollection<Feedback> Feedbacks { get; }

    public ICommand NavigateCommand { get; }
    public ICommand LogoutCommand { get; }
    public ICommand SaveEmployeeCommand { get; }
    public ICommand DeleteEmployeeCommand { get; }
    public ICommand NewEmployeeCommand { get; }
    public ICommand SaveGuestCommand { get; }
    public ICommand ToggleGuestStatusCommand { get; }
    public ICommand DeleteGuestCommand { get; }
    public ICommand ResetGuestPasswordCommand { get; }
    public ICommand SaveTableCommand { get; }
    public ICommand DeleteTableCommand { get; }
    public ICommand NewTableCommand { get; }
    public ICommand RefreshTableStatusCommand { get; }
    public ICommand CreateWalkInBookingCommand { get; }
    public ICommand CreateCustomerBookingCommand { get; }
    public ICommand ConfirmBookingCommand { get; }
    public ICommand CancelBookingCommand { get; }
    public ICommand CompleteBookingCommand { get; }
    public ICommand CancelHistoryBookingCommand { get; }
    public ICommand SubmitFeedbackCommand { get; }
    public ICommand SaveProfileCommand { get; }
    public ICommand ChangePasswordCommand { get; }

    public string SelectedMenuItem
    {
        get => _selectedMenuItem;
        set => SetProperty(ref _selectedMenuItem, value);
    }

    public string? Message
    {
        get => _message;
        set => SetProperty(ref _message, value);
    }

    public Employee? SelectedEmployee
    {
        get => _selectedEmployee;
        set
        {
            if (SetProperty(ref _selectedEmployee, value))
            {
                if (value is not null)
                {
                    EmployeeUsername = value.Username;
                    EmployeeIdentityNumber = value.IdentityNumber;
                    EmployeePhoneNumber = value.PhoneNumber;
                    EmployeeDateOfBirth = value.DateOfBirth;
                    EmployeeGender = value.Gender ?? "male";
                    EmployeeRoleId = value.RoleId ?? RoleIds.Staff;
                    EmployeeIsActive = value.IsActive;
                    RaiseFormProperties();
                }
            }
        }
    }

    public Guest? SelectedGuest
    {
        get => _selectedGuest;
        set
        {
            if (SetProperty(ref _selectedGuest, value))
            {
                if (value is not null)
                {
                    GuestUsername = value.Username;
                    GuestName = value.Name;
                    GuestIdentityNumber = value.IdentityNumber;
                    GuestPhoneNumber = value.PhoneNumber;
                    GuestDateOfBirth = value.DateOfBirth;
                    GuestGender = value.Gender ?? "male";
                    RaiseGuestProperties();
                }
            }
        }
    }

    public RestaurantTable? SelectedTable
    {
        get => _selectedTable;
        set
        {
            if (SetProperty(ref _selectedTable, value) && value is not null)
            {
                TableName = value.TableName;
                OnPropertyChanged(nameof(TableName));
            }
        }
    }

    public Appointment? SelectedAppointment
    {
        get => _selectedAppointment;
        set => SetProperty(ref _selectedAppointment, value);
    }

    public Appointment? SelectedHistory
    {
        get => _selectedHistory;
        set => SetProperty(ref _selectedHistory, value);
    }

    public DateTime SelectedDate
    {
        get => _selectedDate;
        set => SetProperty(ref _selectedDate, value);
    }

    public string StartTimeText
    {
        get => _startTimeText;
        set => SetProperty(ref _startTimeText, value);
    }

    public string EndTimeText
    {
        get => _endTimeText;
        set => SetProperty(ref _endTimeText, value);
    }

    public string EmployeeUsername { get; set; } = string.Empty;
    public string EmployeePassword { get; set; } = "123";
    public string? EmployeeIdentityNumber { get; set; }
    public string? EmployeePhoneNumber { get; set; }
    public DateTime? EmployeeDateOfBirth { get; set; } = new(1995, 1, 1);
    public string EmployeeGender { get; set; } = "male";
    public int EmployeeRoleId { get; set; } = RoleIds.Staff;
    public bool EmployeeIsActive { get; set; } = true;

    public string? GuestUsername { get; set; }
    public string GuestPassword { get; set; } = "123";
    public string? GuestName { get; set; }
    public string? GuestIdentityNumber { get; set; }
    public string? GuestPhoneNumber { get; set; }
    public DateTime? GuestDateOfBirth { get; set; } = new(2000, 1, 1);
    public string GuestGender { get; set; } = "male";

    public string TableName { get; set; } = string.Empty;
    public string WalkInGuestName { get; set; } = string.Empty;
    public string WalkInGuestPhone { get; set; } = string.Empty;
    public int BookingTableId { get; set; }
    public string FeedbackContent { get; set; } = string.Empty;
    public int FeedbackRating { get; set; } = 5;
    public string OldPassword { get; set; } = string.Empty;
    public string NewPassword { get; set; } = string.Empty;

    public async Task InitializeAsync()
    {
        MenuItems.Clear();
        if (IsAdmin)
        {
            AddMenu("Dashboard", "Employees", "Customers", "Tables", "Bookings", "Feedbacks", "Profile", "Change Password");
        }
        else if (IsStaff)
        {
            AddMenu("Staff Booking", "Bookings", "Table Status", "Profile", "Change Password");
        }
        else
        {
            AddMenu("Customer Booking", "Booking History", "Feedback", "Profile", "Change Password");
        }

        SelectedMenuItem = MenuItems.FirstOrDefault() ?? "Dashboard";
        await LoadCurrentSectionAsync();
    }

    private void AddMenu(params string[] items)
    {
        foreach (var item in items)
        {
            MenuItems.Add(item);
        }
    }

    private async Task NavigateAsync(object? parameter)
    {
        if (parameter is string menuItem)
        {
            SelectedMenuItem = menuItem;
        }

        await LoadCurrentSectionAsync();
    }

    private async Task LoadCurrentSectionAsync()
    {
        Message = null;
        switch (SelectedMenuItem)
        {
            case "Employees":
                await LoadEmployeesAsync();
                break;
            case "Customers":
                await LoadGuestsAsync();
                break;
            case "Tables":
            case "Table Status":
            case "Staff Booking":
            case "Customer Booking":
                await LoadTablesAsync();
                await LoadTableStatusAsync();
                break;
            case "Bookings":
                await LoadAppointmentsAsync();
                break;
            case "Booking History":
            case "Feedback":
                await LoadBookingHistoryAsync();
                break;
            case "Feedbacks":
                await LoadFeedbacksAsync();
                break;
            case "Dashboard":
                await LoadDashboardAsync();
                break;
            case "Profile":
                await LoadProfileAsync();
                break;
        }
    }

    private async Task LoadDashboardAsync()
    {
        Employees.ReplaceWith(await _restaurantService.GetEmployeesAsync());
        Appointments.ReplaceWith(await _restaurantService.GetAppointmentsAsync());
        Feedbacks.ReplaceWith(await _restaurantService.GetFeedbacksAsync());
    }

    private async Task LogoutAsync()
    {
        _userSession.SignOut();
        LogoutRequested?.Invoke(this, EventArgs.Empty);
        await Task.CompletedTask;
    }

    private async Task LoadEmployeesAsync()
    {
        Employees.ReplaceWith(await _restaurantService.GetEmployeesAsync());
    }

    private async Task SaveEmployeeAsync()
    {
        await RunSafeAsync(async () =>
        {
            var employee = new Employee
            {
                EmployeeId = SelectedEmployee?.EmployeeId ?? 0,
                Username = EmployeeUsername,
                IdentityNumber = EmployeeIdentityNumber,
                PhoneNumber = EmployeePhoneNumber,
                DateOfBirth = EmployeeDateOfBirth,
                Gender = EmployeeGender,
                RoleId = EmployeeRoleId,
                IsActive = EmployeeIsActive
            };

            if (employee.EmployeeId == 0)
            {
                await _restaurantService.AddEmployeeAsync(employee, EmployeePassword);
            }
            else
            {
                await _restaurantService.UpdateEmployeeAsync(employee);
            }

            await LoadEmployeesAsync();
            Message = "Saved employee successfully.";
        });
    }

    private async Task DeleteEmployeeAsync()
    {
        if (SelectedEmployee is null)
        {
            Message = "Please select an employee.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.DeleteEmployeeAsync(SelectedEmployee.EmployeeId);
            await LoadEmployeesAsync();
            Message = "Deleted employee successfully.";
        });
    }

    private async Task NewEmployeeAsync()
    {
        SelectedEmployee = null;
        EmployeeUsername = string.Empty;
        EmployeePassword = "123";
        EmployeeIdentityNumber = null;
        EmployeePhoneNumber = null;
        EmployeeDateOfBirth = new DateTime(1995, 1, 1);
        EmployeeGender = "male";
        EmployeeRoleId = RoleIds.Staff;
        EmployeeIsActive = true;
        RaiseFormProperties();
        await Task.CompletedTask;
    }

    private async Task LoadGuestsAsync()
    {
        Guests.ReplaceWith(await _restaurantService.GetGuestsAsync());
    }

    private async Task SaveGuestAsync()
    {
        await RunSafeAsync(async () =>
        {
            var guest = new Guest
            {
                Username = GuestUsername,
                Name = GuestName,
                IdentityNumber = GuestIdentityNumber,
                PhoneNumber = GuestPhoneNumber,
                DateOfBirth = GuestDateOfBirth,
                Gender = GuestGender
            };
            await _restaurantService.AddGuestAsync(guest, GuestPassword);
            await LoadGuestsAsync();
            Message = "Created customer successfully.";
        });
    }

    private async Task ToggleGuestStatusAsync()
    {
        if (SelectedGuest is null)
        {
            Message = "Please select a customer.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.SetGuestStatusAsync(SelectedGuest.GuestId, !SelectedGuest.IsActive);
            await LoadGuestsAsync();
            Message = "Updated customer status.";
        });
    }

    private async Task DeleteGuestAsync()
    {
        if (SelectedGuest is null)
        {
            Message = "Please select a customer.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.DeleteGuestAsync(SelectedGuest.GuestId);
            await LoadGuestsAsync();
            Message = "Deleted customer successfully.";
        });
    }

    private async Task ResetGuestPasswordAsync()
    {
        if (SelectedGuest is null)
        {
            Message = "Please select a customer.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.ResetGuestPasswordAsync(SelectedGuest.GuestId, GuestPassword);
            Message = "Reset password successfully.";
        });
    }

    private async Task LoadTablesAsync()
    {
        Tables.ReplaceWith(await _restaurantService.GetTablesAsync());
    }

    private async Task SaveTableAsync()
    {
        await RunSafeAsync(async () =>
        {
            if (SelectedTable is null)
            {
                await _restaurantService.AddTableAsync(TableName);
            }
            else
            {
                SelectedTable.TableName = TableName;
                await _restaurantService.UpdateTableAsync(SelectedTable);
            }

            await LoadTablesAsync();
            await LoadTableStatusAsync();
            Message = "Saved table successfully.";
        });
    }

    private async Task DeleteTableAsync()
    {
        if (SelectedTable is null)
        {
            Message = "Please select a table.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.DeleteTableAsync(SelectedTable.TableId);
            await LoadTablesAsync();
            await LoadTableStatusAsync();
            Message = "Deleted table successfully.";
        });
    }

    private async Task NewTableAsync()
    {
        SelectedTable = null;
        TableName = string.Empty;
        OnPropertyChanged(nameof(TableName));
        await Task.CompletedTask;
    }

    private async Task LoadTableStatusAsync()
    {
        if (!TryParseTimes(out var startTime, out var endTime))
        {
            return;
        }

        TableStatuses.ReplaceWith(await _restaurantService.GetTableStatusAsync(SelectedDate, startTime, endTime));
    }

    private async Task CreateWalkInBookingAsync()
    {
        if (!TryParseTimes(out var startTime, out var endTime))
        {
            return;
        }

        await RunSafeAsync(async () =>
        {
            var tableId = BookingTableId == 0 ? TableStatuses.FirstOrDefault(table => !table.IsBooked)?.TableId ?? 0 : BookingTableId;
            if (tableId == 0)
            {
                Message = "Please select an available table.";
                return;
            }

            var guestId = await _restaurantService.CreateOrGetWalkInGuestAsync(WalkInGuestName, WalkInGuestPhone);
            await _restaurantService.CreateBookingAsync(guestId, tableId, SelectedDate, startTime, endTime, "confirmed", CurrentAccount?.Username);
            await LoadTableStatusAsync();
            Message = "Created walk-in booking successfully.";
        });
    }

    private async Task CreateCustomerBookingAsync()
    {
        if (CurrentAccount is null || !TryParseTimes(out var startTime, out var endTime))
        {
            return;
        }

        await RunSafeAsync(async () =>
        {
            if (await _restaurantService.HasPendingBookingAsync(CurrentAccount.Id))
            {
                Message = "You already have a pending booking.";
                return;
            }

            var tableId = BookingTableId == 0 ? TableStatuses.FirstOrDefault(table => !table.IsBooked)?.TableId ?? 0 : BookingTableId;
            if (tableId == 0)
            {
                Message = "Please select an available table.";
                return;
            }

            await _restaurantService.CreateBookingAsync(CurrentAccount.Id, tableId, SelectedDate, startTime, endTime, "pending", CurrentAccount.Username);
            await LoadBookingHistoryAsync();
            Message = "Booking created. Please wait for confirmation.";
        });
    }

    private async Task LoadAppointmentsAsync()
    {
        Appointments.ReplaceWith(await _restaurantService.GetAppointmentsAsync());
    }

    private async Task UpdateSelectedBookingStatusAsync(string status)
    {
        if (SelectedAppointment is null)
        {
            Message = "Please select a booking.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.UpdateBookingStatusAsync(SelectedAppointment.AppointmentId, status);
            await LoadAppointmentsAsync();
            Message = $"Booking marked as {status}.";
        });
    }

    private async Task LoadBookingHistoryAsync()
    {
        if (CurrentAccount is null)
        {
            return;
        }

        BookingHistory.ReplaceWith(await _restaurantService.GetBookingHistoryAsync(CurrentAccount.Id));
    }

    private async Task SubmitFeedbackAsync()
    {
        if (CurrentAccount is null || SelectedHistory is null)
        {
            Message = "Please select a booking from history.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.AddFeedbackAsync(CurrentAccount.Id, SelectedHistory.AppointmentId, FeedbackContent, FeedbackRating);
            await LoadBookingHistoryAsync();
            Message = "Feedback submitted successfully.";
        });
    }

    private async Task CancelHistoryBookingAsync()
    {
        if (SelectedHistory is null)
        {
            Message = "Please select a booking from history.";
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.CancelBookingAsync(SelectedHistory.AppointmentId);
            await LoadBookingHistoryAsync();
            Message = "Booking cancelled successfully.";
        });
    }

    private async Task LoadFeedbacksAsync()
    {
        Feedbacks.ReplaceWith(await _restaurantService.GetFeedbacksAsync());
    }

    private async Task SaveProfileAsync()
    {
        if (CurrentAccount is null)
        {
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _restaurantService.UpdateProfileAsync(CurrentAccount, GuestName, GuestIdentityNumber, GuestPhoneNumber, GuestDateOfBirth, GuestGender);
            Message = "Profile updated successfully.";
        });
    }

    private async Task LoadProfileAsync()
    {
        if (CurrentAccount is null)
        {
            return;
        }

        if (CurrentAccount.AccountType == AccountType.Guest)
        {
            var guest = await _restaurantService.GetGuestByIdAsync(CurrentAccount.Id);
            if (guest is null)
            {
                return;
            }

            GuestName = guest.Name;
            GuestIdentityNumber = guest.IdentityNumber;
            GuestPhoneNumber = guest.PhoneNumber;
            GuestDateOfBirth = guest.DateOfBirth;
            GuestGender = guest.Gender ?? "male";
            RaiseGuestProperties();
        }
        else
        {
            var employee = await _restaurantService.GetEmployeeByIdAsync(CurrentAccount.Id);
            if (employee is null)
            {
                return;
            }

            GuestName = employee.Username;
            GuestIdentityNumber = employee.IdentityNumber;
            GuestPhoneNumber = employee.PhoneNumber;
            GuestDateOfBirth = employee.DateOfBirth;
            GuestGender = employee.Gender ?? "male";
            RaiseGuestProperties();
        }
    }

    private async Task ChangePasswordAsync()
    {
        if (CurrentAccount is null)
        {
            return;
        }

        await RunSafeAsync(async () =>
        {
            var isChanged = await _restaurantService.ChangePasswordAsync(CurrentAccount, OldPassword, NewPassword);
            Message = isChanged ? "Password changed successfully." : "Old password is incorrect.";
        });
    }

    private bool TryParseTimes(out TimeSpan startTime, out TimeSpan endTime)
    {
        startTime = TimeSpan.Zero;
        endTime = TimeSpan.Zero;

        if (!TimeSpan.TryParse(StartTimeText, out startTime) || !TimeSpan.TryParse(EndTimeText, out endTime))
        {
            Message = "Time must use HH:mm format.";
            return false;
        }

        if (startTime >= endTime)
        {
            Message = "End time must be after start time.";
            return false;
        }

        return true;
    }

    private async Task RunSafeAsync(Func<Task> action)
    {
        try
        {
            Message = null;
            await action();
        }
        catch (Exception ex)
        {
            Message = ex.Message;
        }
    }

    private void RaiseFormProperties()
    {
        OnPropertyChanged(nameof(EmployeeUsername));
        OnPropertyChanged(nameof(EmployeePassword));
        OnPropertyChanged(nameof(EmployeeIdentityNumber));
        OnPropertyChanged(nameof(EmployeePhoneNumber));
        OnPropertyChanged(nameof(EmployeeDateOfBirth));
        OnPropertyChanged(nameof(EmployeeGender));
        OnPropertyChanged(nameof(EmployeeRoleId));
        OnPropertyChanged(nameof(EmployeeIsActive));
    }

    private void RaiseGuestProperties()
    {
        OnPropertyChanged(nameof(GuestUsername));
        OnPropertyChanged(nameof(GuestPassword));
        OnPropertyChanged(nameof(GuestName));
        OnPropertyChanged(nameof(GuestIdentityNumber));
        OnPropertyChanged(nameof(GuestPhoneNumber));
        OnPropertyChanged(nameof(GuestDateOfBirth));
        OnPropertyChanged(nameof(GuestGender));
    }
}

public static class ObservableCollectionExtensions
{
    public static void ReplaceWith<T>(this ObservableCollection<T> collection, IEnumerable<T> items)
    {
        collection.Clear();
        foreach (var item in items)
        {
            collection.Add(item);
        }
    }
}
