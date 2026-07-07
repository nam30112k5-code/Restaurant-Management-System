using System.Windows;
using ViewModels;

namespace UI;

public partial class LoginWindow : Window
{
    public LoginWindow(MainViewModel viewModel)
    {
        InitializeComponent();
        DataContext = viewModel;
    }
}
