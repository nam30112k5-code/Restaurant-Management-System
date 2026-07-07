namespace Services;

public class UserSession : IUserSession
{
    public UserAccount? CurrentUser { get; private set; }
    public bool IsAuthenticated => CurrentUser is not null;

    public void SignIn(UserAccount accountMember)
    {
        ArgumentNullException.ThrowIfNull(accountMember);
        CurrentUser = accountMember;
    }

    public void SignOut()
    {
        CurrentUser = null;
    }
}
