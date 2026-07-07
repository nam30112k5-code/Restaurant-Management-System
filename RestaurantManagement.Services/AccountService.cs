using Repositories;

namespace Services;

public class AccountService : IAccountService
{
    private readonly IAccountRepository _accountRepository;
    private readonly IPasswordHasher _passwordHasher;

    public AccountService(IAccountRepository accountRepository, IPasswordHasher passwordHasher)
    {
        _accountRepository = accountRepository;
        _passwordHasher = passwordHasher;
    }

    public async Task<AuthResult> LoginAsync(
        string username,
        string password,
        CancellationToken cancellationToken = default)
    {
        if (string.IsNullOrWhiteSpace(username) || string.IsNullOrWhiteSpace(password))
        {
            return AuthResult.Failure("Please enter both username and password.");
        }

        var trimmedUsername = username.Trim();
        if (trimmedUsername.Length > LoginRules.MaxUsernameLength ||
            password.Length > LoginRules.MaxPasswordLength)
        {
            return AuthResult.Failure("Username or password is too long.");
        }

        var employee = await _accountRepository.GetActiveEmployeeByUsernameAsync(trimmedUsername, cancellationToken);

        if (employee is not null && _passwordHasher.Verify(password, employee.Password))
        {
            return AuthResult.Success(new UserAccount(
                employee.EmployeeId,
                employee.Username,
                AccountType.Employee,
                employee.RoleId,
                employee.Username));
        }

        var guest = await _accountRepository.GetActiveGuestByUsernameAsync(trimmedUsername, cancellationToken);

        if (guest is not null && _passwordHasher.Verify(password, guest.Password))
        {
            return AuthResult.Success(new UserAccount(
                guest.GuestId,
                guest.Username ?? string.Empty,
                AccountType.Guest,
                null,
                string.IsNullOrWhiteSpace(guest.Name) ? guest.Username ?? "Guest" : guest.Name));
        }

        return AuthResult.Failure("Invalid username or password.");
    }
}
