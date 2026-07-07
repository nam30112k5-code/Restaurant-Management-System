using System.Collections.ObjectModel;
using System.Windows.Input;
using Commands;
using Models;
using Services;

namespace ViewModels;

public class ShellViewModel : ViewModelBase
{
    private readonly IRmsService _service;
    private readonly IUserSession _session;
    private string _selectedMenuItem = "Dashboard";
    private string? _message;
    private Employee? _selectedEmployee;
    private Guest? _selectedGuest;
    private Table? _selectedTable;
    private Appointment? _selectedAppointment;
    private Appointment? _selectedHistory;
    private DateTime _selectedDate = DateTime.Today;
    private string _startTimeText = "18:00";
    private string _endTimeText = "20:00";
    private string _selectedDishesText = "No dishes selected.";

    public ShellViewModel(IRmsService service, IUserSession session)
    {
        _service = service;
        _session = session;

        MenuItems = new ObservableCollection<string>();
        Employees = new ObservableCollection<Employee>();
        Guests = new ObservableCollection<Guest>();
        Tables = new ObservableCollection<Table>();
        TableStatuses = new ObservableCollection<TableStatus>();
        Appointments = new ObservableCollection<Appointment>();
        PendingBookings = new ObservableCollection<Appointment>();
        BookingHistory = new ObservableCollection<Appointment>();
        Feedbacks = new ObservableCollection<Feedback>();
        Dishes = new ObservableCollection<Dish>(CreateDishes());
        foreach (var dish in Dishes)
        {
            dish.PropertyChanged += (_, _) => UpdateSelectedDishesText();
        }
        UpdateSelectedDishesText();

        NavigateCommand = new AsyncRelayCommand(NavigateAsync);
        ContinueToBookingCommand = new AsyncRelayCommand(ContinueToBookingAsync);
        LogoutCommand = new AsyncRelayCommand(LogoutAsync);
        SaveEmployeeCommand = new AsyncRelayCommand(SaveEmployeeAsync);
        DeleteEmployeeCommand = new AsyncRelayCommand(DeleteEmployeeAsync);
        NewEmployeeCommand = new AsyncRelayCommand(NewEmployeeAsync);
        SaveGuestCommand = new AsyncRelayCommand(SaveGuestAsync);
        NewGuestCommand = new AsyncRelayCommand(NewGuestAsync);
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

    public UserAccount? CurrentAccount => _session.CurrentUser;
    public string WelcomeText => CurrentAccount is null ? "Welcome" : $"Welcome, {CurrentAccount.DisplayName}";
    public bool IsAdmin => CurrentAccount?.AccountType == AccountType.Employee && CurrentAccount.RoleId == RoleIds.Admin;
    public bool IsStaff => CurrentAccount?.AccountType == AccountType.Employee && CurrentAccount.RoleId == RoleIds.Staff;
    public bool IsGuest => CurrentAccount?.AccountType == AccountType.Guest;

    public ObservableCollection<string> MenuItems { get; }
    public ObservableCollection<Employee> Employees { get; }
    public ObservableCollection<Guest> Guests { get; }
    public ObservableCollection<Table> Tables { get; }
    public ObservableCollection<TableStatus> TableStatuses { get; }
    public ObservableCollection<Appointment> Appointments { get; }
    public ObservableCollection<Appointment> PendingBookings { get; }
    public ObservableCollection<Appointment> BookingHistory { get; }
    public ObservableCollection<Feedback> Feedbacks { get; }
    public ObservableCollection<Dish> Dishes { get; }

    public ICommand NavigateCommand { get; }
    public ICommand ContinueToBookingCommand { get; }
    public ICommand LogoutCommand { get; }
    public ICommand SaveEmployeeCommand { get; }
    public ICommand DeleteEmployeeCommand { get; }
    public ICommand NewEmployeeCommand { get; }
    public ICommand SaveGuestCommand { get; }
    public ICommand NewGuestCommand { get; }
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
                    RaiseEmployeeProperties();
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
                    GuestPassword = string.Empty;
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

    public Table? SelectedTable
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

    public string SelectedDishesText
    {
        get => _selectedDishesText;
        private set => SetProperty(ref _selectedDishesText, value);
    }

    public async Task InitializeAsync()
    {
        MenuItems.Clear();
        if (IsAdmin)
        {
            AddMenu("Dashboard", "Bookings", "Table Status", "Employees", "Customers", "Tables", "Feedbacks", "Profile", "Change Password");
        }
        else if (IsStaff)
        {
            AddMenu("Staff Booking", "Bookings", "Table Status", "Profile", "Change Password");
        }
        else
        {
            AddMenu("Menu", "Customer Booking", "Booking History", "Feedback", "Profile", "Change Password");
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
            case "Menu":
                UpdateSelectedDishesText();
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
        Employees.ReplaceWith(await _service.GetEmployeesAsync());
        Guests.ReplaceWith(await _service.GetGuestsAsync());
        Tables.ReplaceWith(await _service.GetTablesAsync());
        Appointments.ReplaceWith(await _service.GetAppointmentsAsync());
        Feedbacks.ReplaceWith(await _service.GetFeedbacksAsync());
        if (TryParseTimes(out var startTime, out var endTime))
        {
            TableStatuses.ReplaceWith(await _service.GetTableStatusAsync(SelectedDate, startTime, endTime));
        }

        RefreshAdminDashboard();
    }

    private async Task LogoutAsync()
    {
        _session.SignOut();
        LogoutRequested?.Invoke(this, EventArgs.Empty);
        await Task.CompletedTask;
    }

    private async Task ContinueToBookingAsync()
    {
        UpdateSelectedDishesText();
        if (!Dishes.Any(dish => dish.IsSelected))
        {
            Message = "Please choose at least one dish before booking a table.";
            return;
        }

        SelectedMenuItem = "Customer Booking";
        await LoadCurrentSectionAsync();
        Message = "Dishes selected. Now choose your table and time.";
    }

    private async Task LoadEmployeesAsync()
    {
        Employees.ReplaceWith(await _service.GetEmployeesAsync());
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
                await _service.AddEmployeeAsync(employee, EmployeePassword);
            }
            else
            {
                await _service.UpdateEmployeeAsync(employee);
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
            await _service.DeleteEmployeeAsync(SelectedEmployee.EmployeeId);
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
        RaiseEmployeeProperties();
        await Task.CompletedTask;
    }

    private async Task LoadGuestsAsync()
    {
        Guests.ReplaceWith(await _service.GetGuestsAsync());
    }

    private async Task SaveGuestAsync()
    {
        await RunSafeAsync(async () =>
        {
            var guest = new Guest
            {
                GuestId = SelectedGuest?.GuestId ?? 0,
                Username = GuestUsername,
                Name = GuestName,
                IdentityNumber = GuestIdentityNumber,
                PhoneNumber = GuestPhoneNumber,
                DateOfBirth = GuestDateOfBirth,
                Gender = GuestGender
            };

            if (guest.GuestId == 0)
            {
                await _service.AddGuestAsync(guest, GuestPassword);
                Message = "Created customer successfully.";
            }
            else
            {
                await _service.UpdateGuestAsync(guest);
                Message = "Updated customer successfully.";
            }

            await LoadGuestsAsync();
        });
    }

    private async Task NewGuestAsync()
    {
        SelectedGuest = null;
        GuestUsername = string.Empty;
        GuestPassword = "123";
        GuestName = string.Empty;
        GuestIdentityNumber = null;
        GuestPhoneNumber = null;
        GuestDateOfBirth = new DateTime(2000, 1, 1);
        GuestGender = "male";
        RaiseGuestProperties();
        await Task.CompletedTask;
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
            await _service.SetGuestStatusAsync(SelectedGuest.GuestId, !SelectedGuest.IsActive);
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
            await _service.DeleteGuestAsync(SelectedGuest.GuestId);
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
            await _service.ResetGuestPasswordAsync(SelectedGuest.GuestId, GuestPassword);
            Message = "Reset password successfully.";
        });
    }

    private async Task LoadTablesAsync()
    {
        Tables.ReplaceWith(await _service.GetTablesAsync());
    }

    private async Task SaveTableAsync()
    {
        await RunSafeAsync(async () =>
        {
            if (SelectedTable is null)
            {
                await _service.AddTableAsync(TableName);
            }
            else
            {
                SelectedTable.TableName = TableName;
                await _service.UpdateTableAsync(SelectedTable);
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
            await _service.DeleteTableAsync(SelectedTable.TableId);
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

        TableStatuses.ReplaceWith(await _service.GetTableStatusAsync(SelectedDate, startTime, endTime));
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

            var guestId = await _service.CreateOrGetWalkInGuestAsync(WalkInGuestName, WalkInGuestPhone);
            await _service.CreateBookingAsync(guestId, tableId, SelectedDate, startTime, endTime, "confirmed", CurrentAccount?.Username);
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
            if (!Dishes.Any(dish => dish.IsSelected))
            {
                SelectedMenuItem = "Menu";
                Message = "Please choose dishes before booking a table.";
                return;
            }

            if (await _service.HasPendingBookingAsync(CurrentAccount.Id))
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

            await _service.CreateBookingAsync(CurrentAccount.Id, tableId, SelectedDate, startTime, endTime, "pending", CurrentAccount.Username);
            await LoadBookingHistoryAsync();
            Message = $"Booking created with: {SelectedDishesText}. Please wait for confirmation.";
        });
    }

    private async Task LoadAppointmentsAsync()
    {
        Appointments.ReplaceWith(await _service.GetAppointmentsAsync());
        RefreshAdminDashboard();
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
            await _service.UpdateBookingStatusAsync(SelectedAppointment.AppointmentId, status);
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

        BookingHistory.ReplaceWith(await _service.GetBookingHistoryAsync(CurrentAccount.Id));
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
            await _service.AddFeedbackAsync(CurrentAccount.Id, SelectedHistory.AppointmentId, FeedbackContent, FeedbackRating);
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
            if (SelectedHistory.Status == "completed" || SelectedHistory.Status == "cancelled")
            {
                Message = "Only pending or confirmed bookings can be cancelled.";
                return;
            }

            await _service.CancelBookingAsync(SelectedHistory.AppointmentId);
            await LoadBookingHistoryAsync();
            Message = "Booking cancelled successfully.";
        });
    }

    private async Task LoadFeedbacksAsync()
    {
        Feedbacks.ReplaceWith(await _service.GetFeedbacksAsync());
        RefreshAdminDashboard();
    }

    private async Task SaveProfileAsync()
    {
        if (CurrentAccount is null)
        {
            return;
        }

        await RunSafeAsync(async () =>
        {
            await _service.UpdateProfileAsync(CurrentAccount, GuestName, GuestIdentityNumber, GuestPhoneNumber, GuestDateOfBirth, GuestGender);
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
            var guest = await _service.GetGuestByIdAsync(CurrentAccount.Id);
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
            var employee = await _service.GetEmployeeByIdAsync(CurrentAccount.Id);
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
            var isChanged = await _service.ChangePasswordAsync(CurrentAccount, OldPassword, NewPassword);
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

    private void RaiseEmployeeProperties()
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

    private void UpdateSelectedDishesText()
    {
        var selectedDishes = Dishes.Where(dish => dish.IsSelected).ToList();
        if (selectedDishes.Count == 0)
        {
            SelectedDishesText = "No dishes selected.";
            return;
        }

        var total = selectedDishes.Sum(dish => dish.Price);
        SelectedDishesText = $"{string.Join(", ", selectedDishes.Select(dish => dish.Name))} - total {total:N0} d";
    }

    private void RefreshAdminDashboard()
    {
        PendingBookings.ReplaceWith(Appointments
            .Where(appointment => appointment.Status == "pending")
            .OrderBy(appointment => appointment.Date)
            .ThenBy(appointment => appointment.StartTime)
            .Take(8));
    }

    private static IEnumerable<Dish> CreateDishes()
    {
        yield return new Dish("Lobster Caesar", "Starter", "Fresh greens, grilled lobster, lemon dressing.", 75000);
        yield return new Dish("Macadamia Salad", "Starter", "Lettuce, macadamia, olive oil.", 68000);
        yield return new Dish("Beef Pho Rolls", "Main", "Soft beef, herbs, special dipping sauce.", 90000);
        yield return new Dish("Banana Blossom Salad", "Main", "Crisp, light, sweet and sour balance.", 125000);
        yield return new Dish("Pandan Chicken", "Main", "Grilled chicken with sesame sauce.", 168000);
        yield return new Dish("Butter Prawn", "Seafood", "Large prawn, garlic butter, micro greens.", 245000);
        yield return new Dish("Pan Seared Salmon", "Seafood", "Salmon, asparagus, green pepper sauce.", 220000);
        yield return new Dish("Seafood Pasta", "Main", "Pasta, clam, shrimp, tomato sauce.", 155000);
        yield return new Dish("Caramel Flan", "Dessert", "Smooth custard with rich caramel.", 45000);
        yield return new Dish("Fruit Tea", "Drink", "Iced tea with seasonal fruit.", 39000);
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
