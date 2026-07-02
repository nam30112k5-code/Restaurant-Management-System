using RestaurantManagement.Models;

namespace Repositories;

public interface IAccountRepository
{
    Task<Employee?> GetActiveEmployeeByUsernameAsync(string username, CancellationToken cancellationToken = default);
    Task<Guest?> GetActiveGuestByUsernameAsync(string username, CancellationToken cancellationToken = default);
}
