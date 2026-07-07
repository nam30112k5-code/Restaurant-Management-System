namespace RestaurantManagement.Services;

public sealed record AuthResult(
    bool IsSuccess,
    UserAccount? UserAccount,
    string? ErrorMessage)
{
    public static AuthResult Success(UserAccount accountMember)
        => new(true, accountMember, null);

    public static AuthResult Failure(string message)
        => new(false, null, message);
}
