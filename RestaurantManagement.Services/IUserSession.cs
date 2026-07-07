namespace Services;

public interface IUserSession
{
    UserAccount? CurrentUser { get; }
    bool IsAuthenticated { get; }
    void SignIn(UserAccount accountMember);
    void SignOut();
}
