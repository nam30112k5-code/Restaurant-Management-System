namespace RestaurantManagement.Services;

public sealed record AuthenticationResult(
    bool IsSuccess,
    AccountMember? AccountMember,
    string? ErrorMessage)
{
    public static AuthenticationResult Success(AccountMember accountMember)
        => new(true, accountMember, null);

    public static AuthenticationResult Failure(string message)
        => new(false, null, message);
}
