namespace RestaurantManagement.Services;

public interface IAccountService
{
    Task<AuthenticationResult> LoginAsync(string username, string password, CancellationToken cancellationToken = default);
}
