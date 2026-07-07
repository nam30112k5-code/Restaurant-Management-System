namespace RestaurantManagement.Services;

public interface IAccountService
{
    Task<AuthResult> LoginAsync(string username, string password, CancellationToken cancellationToken = default);
}
