using System.Windows;
using RestaurantManagement.ViewModels;

namespace RestaurantManagement.UI;

public partial class LoginWindow : Window
{
    public LoginWindow(MainViewModel viewModel)
    {
        InitializeComponent();
        DataContext = viewModel;
    }
}
