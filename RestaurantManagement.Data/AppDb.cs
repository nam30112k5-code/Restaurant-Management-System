using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Models;
using System.IO;

namespace Data;

public partial class AppDb : DbContext
{
    public AppDb()
    {
    }

    public AppDb(DbContextOptions<AppDb> options)
        : base(options)
    {
    }

    public DbSet<Employee> Employees => Set<Employee>();
    public DbSet<Guest> Guests => Set<Guest>();
    public DbSet<Role> Roles => Set<Role>();
    public DbSet<Table> Tables => Set<Table>();
    public DbSet<Appointment> Appointments => Set<Appointment>();
    public DbSet<Feedback> Feedbacks => Set<Feedback>();

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            var builder = new ConfigurationBuilder()
                .SetBasePath(Directory.GetCurrentDirectory())
                .AddJsonFile("appsettings.json", optional: true, reloadOnChange: true);

            IConfigurationRoot configuration = builder.Build();
            var connectionString = configuration.GetConnectionString("RestaurantDatabase");

            optionsBuilder.UseSqlServer(connectionString);
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.HasAnnotation("Relational:Collation", "SQL_Latin1_General_CP1_CI_AS");

        modelBuilder.Entity<Role>(entity =>
        {
            entity.ToTable("Roles");
            entity.HasKey(role => role.RoleId);
            entity.Property(role => role.RoleId).HasColumnName("roleId");
            entity.Property(role => role.RoleName).HasColumnName("roleName").HasMaxLength(100).IsRequired();
        });

        modelBuilder.Entity<Employee>(entity =>
        {
            entity.ToTable("Employees");
            entity.HasKey(employee => employee.EmployeeId);
            entity.HasIndex(employee => employee.Username).IsUnique();

            entity.Property(employee => employee.EmployeeId).HasColumnName("employeeId");
            entity.Property(employee => employee.Username).HasColumnName("username").HasMaxLength(10).IsRequired();
            entity.Property(employee => employee.Password).HasColumnName("password").IsRequired();
            entity.Property(employee => employee.IdentityNumber).HasColumnName("identityNumber").HasMaxLength(12);
            entity.Property(employee => employee.PhoneNumber).HasColumnName("phoneNumber").HasMaxLength(10);
            entity.Property(employee => employee.DateOfBirth).HasColumnName("dateOfBirth").HasColumnType("date");
            entity.Property(employee => employee.Gender).HasColumnName("gender").HasMaxLength(6);
            entity.Property(employee => employee.IsActive).HasColumnName("isActive");
            entity.Property(employee => employee.RoleId).HasColumnName("roleId");

            entity.HasOne(employee => employee.Role)
                .WithMany(role => role.Employees)
                .HasForeignKey(employee => employee.RoleId);
        });

        modelBuilder.Entity<Guest>(entity =>
        {
            entity.ToTable("Guests");
            entity.HasKey(guest => guest.GuestId);
            entity.HasIndex(guest => guest.Username).IsUnique();

            entity.Property(guest => guest.GuestId).HasColumnName("guestId");
            entity.Property(guest => guest.Username).HasColumnName("username").HasMaxLength(50);
            entity.Property(guest => guest.Password).HasColumnName("password").IsRequired();
            entity.Property(guest => guest.Name).HasColumnName("name").HasMaxLength(50);
            entity.Property(guest => guest.IdentityNumber).HasColumnName("identityNumber").HasMaxLength(12);
            entity.Property(guest => guest.PhoneNumber).HasColumnName("phoneNumber").HasMaxLength(10);
            entity.Property(guest => guest.DateOfBirth).HasColumnName("dateOfBirth").HasColumnType("date");
            entity.Property(guest => guest.Gender).HasColumnName("gender").HasMaxLength(6);
            entity.Property(guest => guest.IsActive).HasColumnName("isActive");
        });

        modelBuilder.Entity<Table>(entity =>
        {
            entity.ToTable("Tables");
            entity.HasKey(table => table.TableId);

            entity.Property(table => table.TableId).HasColumnName("tableId");
            entity.Property(table => table.TableName).HasColumnName("tableName").HasMaxLength(100).IsRequired();
        });

        modelBuilder.Entity<Appointment>(entity =>
        {
            entity.ToTable("Appointments");
            entity.HasKey(appointment => appointment.AppointmentId);

            entity.Property(appointment => appointment.AppointmentId).HasColumnName("appointmentId");
            entity.Property(appointment => appointment.GuestId).HasColumnName("guestId");
            entity.Property(appointment => appointment.TableId).HasColumnName("tableId");
            entity.Property(appointment => appointment.CreateBy).HasColumnName("createBy").HasMaxLength(10);
            entity.Property(appointment => appointment.StartTime).HasColumnName("startTime").HasColumnType("time");
            entity.Property(appointment => appointment.EndTime).HasColumnName("endTime").HasColumnType("time");
            entity.Property(appointment => appointment.Date).HasColumnName("date").HasColumnType("date");
            entity.Property(appointment => appointment.Status).HasColumnName("status").HasMaxLength(20);

            entity.HasOne(appointment => appointment.Guest)
                .WithMany(guest => guest.Appointments)
                .HasForeignKey(appointment => appointment.GuestId);

            entity.HasOne(appointment => appointment.Table)
                .WithMany(table => table.Appointments)
                .HasForeignKey(appointment => appointment.TableId);
        });

        modelBuilder.Entity<Feedback>(entity =>
        {
            entity.ToTable("Feedbacks");
            entity.HasKey(feedback => feedback.FeedbackId);

            entity.Property(feedback => feedback.FeedbackId).HasColumnName("feedbackId");
            entity.Property(feedback => feedback.Content).HasColumnName("content").IsRequired();
            entity.Property(feedback => feedback.Rating).HasColumnName("rating");
            entity.Property(feedback => feedback.GuestId).HasColumnName("guestId");
            entity.Property(feedback => feedback.AppointmentId).HasColumnName("appointmentId");

            entity.HasOne(feedback => feedback.Guest)
                .WithMany(guest => guest.Feedbacks)
                .HasForeignKey(feedback => feedback.GuestId);

            entity.HasOne(feedback => feedback.Appointment)
                .WithMany(appointment => appointment.Feedbacks)
                .HasForeignKey(feedback => feedback.AppointmentId);
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
