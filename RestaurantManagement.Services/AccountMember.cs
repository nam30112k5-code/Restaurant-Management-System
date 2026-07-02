namespace RestaurantManagement.Services;

public enum AccountType
{
    Employee,
    Guest
}

public sealed record AccountMember(
    int Id,
    string Username,
    AccountType AccountType,
    int? RoleId,
    string DisplayName);
