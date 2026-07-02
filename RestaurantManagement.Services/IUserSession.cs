namespace RestaurantManagement.Services;

public interface IUserSession
{
    AccountMember? CurrentUser { get; }
    bool IsAuthenticated { get; }
    void SignIn(AccountMember accountMember);
    void SignOut();
}
