using System.Windows;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using RestaurantManagement.Data;
using RestaurantManagement.Services;
using RestaurantManagement.ViewModels;
using Repositories;

namespace RestaurantManagement.UI;

public partial class App : Application
{
    private IHost? _host;

    protected override async void OnStartup(StartupEventArgs e)
    {
        _host = Host.CreateDefaultBuilder(e.Args)
            .ConfigureAppConfiguration(configuration =>
            {
                configuration.AddJsonFile("appsettings.json", optional: false, reloadOnChange: true);
            })
            .ConfigureServices((context, services) =>
            {
                services.AddDbContext<RestaurantDbContext>(options =>
                    options.UseSqlServer(context.Configuration.GetConnectionString("RestaurantDatabase")));

                services.AddScoped<IAccountRepository, AccountRepository>();
                services.AddSingleton<IUserSession, UserSession>();
                services.AddSingleton<IPasswordHasher, BCryptPasswordHasher>();
                services.AddScoped<IAccountService, AccountService>();
                services.AddScoped<IRestaurantService, RestaurantService>();
                services.AddTransient<LoginViewModel>();
                services.AddTransient<ShellViewModel>();
                services.AddTransient<MainViewModel>();
                services.AddTransient<LoginWindow>();
            })
            .Build();

        await _host.StartAsync();

        var loginWindow = _host.Services.GetRequiredService<LoginWindow>();
        loginWindow.Show();

        base.OnStartup(e);
    }

    protected override async void OnExit(ExitEventArgs e)
    {
        if (_host is not null)
        {
            await _host.StopAsync();
            _host.Dispose();
        }

        base.OnExit(e);
    }
}
