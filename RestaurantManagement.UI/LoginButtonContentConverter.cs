using System.Globalization;
using System.Windows.Data;

namespace RestaurantManagement.UI;

public class LoginButtonContentConverter : IValueConverter
{
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        => value is true ? "Signing in..." : "Sign in";

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        => Binding.DoNothing;
}
