using System.Windows;
using Services;

namespace WPFApp
{
    public partial class LoginWindow : Window
    {
        private readonly IAccountService accountService;

        public LoginWindow()
        {
            InitializeComponent();
            accountService = new AccountService();
        }

        private void btnLogin_Click(object sender, RoutedEventArgs e)
        {
            string email = txtUser.Text.Trim();
            string password = txtPass.Password.Trim();

            if (string.IsNullOrEmpty(email) || string.IsNullOrEmpty(password))
            {
                MessageBox.Show("Please enter email and password.");
                return;
            }

            var account = accountService.GetAccountByEmailAndPassword(email, password);

            if (account == null)
            {
                MessageBox.Show("Invalid email or password.");
                return;
            }

            MainWindow mainWindow = new MainWindow();
            mainWindow.Show();

            this.Close();
        }

        private void btnCancel_Click(object sender, RoutedEventArgs e)
        {
            Application.Current.Shutdown();
        }
    }
}