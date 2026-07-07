namespace Services;

public enum AccountType
{
    Employee,
    Guest
}

public sealed record UserAccount(
    int Id,
    string Username,
    AccountType AccountType,
    int? RoleId,
    string DisplayName);
