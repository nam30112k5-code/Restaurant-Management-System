namespace RestaurantManagement.Services;

public class UserSession : IUserSession
{
    public AccountMember? CurrentUser { get; private set; }
    public bool IsAuthenticated => CurrentUser is not null;

    public void SignIn(AccountMember accountMember)
    {
        ArgumentNullException.ThrowIfNull(accountMember);
        CurrentUser = accountMember;
    }

    public void SignOut()
    {
        CurrentUser = null;
    }
}
