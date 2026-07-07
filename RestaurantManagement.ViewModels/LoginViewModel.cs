using System.Windows.Input;
using Commands;
using Services;

namespace ViewModels;

public class LoginViewModel : ViewModelBase
{
    private readonly IAccountService _accountService;
    private readonly IUserSession _session;
    private string _username = string.Empty;
    private string _password = string.Empty;
    private string? _errorMessage;
    private bool _isBusy;

    public LoginViewModel(IAccountService accountService, IUserSession session)
    {
        _accountService = accountService;
        _session = session;
        LoginCommand = new AsyncRelayCommand(LoginAsync, CanLogin);
    }

    public event EventHandler<UserAccount>? LoginSucceeded;

    public string Username
    {
        get => _username;
        set
        {
            var trimmedStartUsername = value.TrimStart();
            if (SetProperty(ref _username, trimmedStartUsername))
            {
                ErrorMessage = null;
                RaiseLoginCanExecuteChanged();
            }
        }
    }

    public string Password
    {
        get => _password;
        set
        {
            if (SetProperty(ref _password, value))
            {
                ErrorMessage = null;
                RaiseLoginCanExecuteChanged();
            }
        }
    }

    public string? ErrorMessage
    {
        get => _errorMessage;
        private set => SetProperty(ref _errorMessage, value);
    }

    public bool IsBusy
    {
        get => _isBusy;
        private set
        {
            if (SetProperty(ref _isBusy, value))
            {
                RaiseLoginCanExecuteChanged();
            }
        }
    }

    public ICommand LoginCommand { get; }

    private bool CanLogin()
        => !IsBusy;

    private async Task LoginAsync()
    {
        try
        {
            IsBusy = true;
            ErrorMessage = null;

            var inputError = ValidateInput();
            if (inputError is not null)
            {
                ErrorMessage = inputError;
                return;
            }

            var loginResult = await _accountService.LoginAsync(Username, Password);
            if (!loginResult.IsSuccess || loginResult.UserAccount is null)
            {
                ErrorMessage = loginResult.ErrorMessage;
                return;
            }

            _session.SignIn(loginResult.UserAccount);
            Password = string.Empty;
            LoginSucceeded?.Invoke(this, loginResult.UserAccount);
        }
        catch (Exception)
        {
            ErrorMessage = "Unable to login right now. Please check the database connection and try again.";
        }
        finally
        {
            IsBusy = false;
        }
    }

    private string? ValidateInput()
    {
        if (string.IsNullOrWhiteSpace(Username))
        {
            return "Please enter username.";
        }

        if (string.IsNullOrWhiteSpace(Password))
        {
            return "Please enter password.";
        }

        if (Username.Trim().Length > LoginRules.MaxUsernameLength)
        {
            return $"Username cannot exceed {LoginRules.MaxUsernameLength} characters.";
        }

        if (Password.Length > LoginRules.MaxPasswordLength)
        {
            return $"Password cannot exceed {LoginRules.MaxPasswordLength} characters.";
        }

        return null;
    }

    private void RaiseLoginCanExecuteChanged()
    {
        if (LoginCommand is AsyncRelayCommand command)
        {
            command.RaiseCanExecuteChanged();
        }
    }
}
