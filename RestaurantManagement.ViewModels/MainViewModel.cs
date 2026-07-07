using Services;

namespace ViewModels;

public class MainViewModel : ViewModelBase
{
    private readonly LoginViewModel _loginViewModel;
    private readonly ShellViewModel _shellViewModel;
    private object _currentViewModel;

    public MainViewModel(LoginViewModel loginViewModel, ShellViewModel shellViewModel)
    {
        _loginViewModel = loginViewModel;
        _shellViewModel = shellViewModel;
        loginViewModel.LoginSucceeded += OnLoginSucceeded;
        _shellViewModel.LogoutRequested += OnLogoutRequested;
        _currentViewModel = loginViewModel;
    }

    public object CurrentViewModel
    {
        get => _currentViewModel;
        private set => SetProperty(ref _currentViewModel, value);
    }

    private async void OnLoginSucceeded(object? sender, UserAccount accountMember)
    {
        await _shellViewModel.InitializeAsync();
        CurrentViewModel = _shellViewModel;
    }

    private void OnLogoutRequested(object? sender, EventArgs e)
    {
        CurrentViewModel = _loginViewModel;
    }
}
