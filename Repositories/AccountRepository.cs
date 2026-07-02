using Microsoft.EntityFrameworkCore;
using RestaurantManagement.Data;
using RestaurantManagement.Models;

namespace Repositories;

public class AccountRepository : IAccountRepository
{
    private readonly RestaurantDbContext _dbContext;

    public AccountRepository(RestaurantDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public Task<Employee?> GetActiveEmployeeByUsernameAsync(
        string username,
        CancellationToken cancellationToken = default)
    {
        return _dbContext.Employees
            .AsNoTracking()
            .FirstOrDefaultAsync(
                employee => employee.Username == username && employee.IsActive,
                cancellationToken);
    }

    public Task<Guest?> GetActiveGuestByUsernameAsync(
        string username,
        CancellationToken cancellationToken = default)
    {
        return _dbContext.Guests
            .AsNoTracking()
            .FirstOrDefaultAsync(
                guest => guest.Username == username && guest.IsActive,
                cancellationToken);
    }
}
