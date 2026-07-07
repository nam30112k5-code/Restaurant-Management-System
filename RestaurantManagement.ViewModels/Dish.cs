namespace ViewModels;

public class Dish : ViewModelBase
{
    private bool _isSelected;

    public Dish(string name, string category, string description, decimal price)
    {
        Name = name;
        Category = category;
        Description = description;
        Price = price;
    }

    public string Name { get; }
    public string Category { get; }
    public string Description { get; }
    public decimal Price { get; }
    public string PriceText => $"{Price:N0} d";

    public bool IsSelected
    {
        get => _isSelected;
        set => SetProperty(ref _isSelected, value);
    }
}
