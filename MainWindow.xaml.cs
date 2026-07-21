using System;
using System.Windows;
using System.Windows.Controls;
using BusinessObjects;
using Services;

namespace WPFApp
{
    public partial class MainWindow : Window
    {
        private readonly IProductService productService;
        private readonly ICategoryService categoryService;

        public MainWindow()
        {
            InitializeComponent();

            productService = new ProductService();
            categoryService = new CategoryService();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            LoadCategoryList();
            LoadProductList();
        }

        public void LoadCategoryList()
        {
            try
            {
                var categories = categoryService.GetCategories();

                cboCategory.ItemsSource = categories;
                cboCategory.DisplayMemberPath = "CategoryName";
                cboCategory.SelectedValuePath = "CategoryId";
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error on load list of categories");
            }
        }

        public void LoadProductList()
        {
            try
            {
                var productList = productService.GetProducts();

                dgData.ItemsSource = null;
                dgData.ItemsSource = productList;
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error on load list of products");
            }
            finally
            {
                resetInput();
            }
        }

        private void btnCreate_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                Product product = new Product();
                product.ProductName = txtProductName.Text.Trim();

                if (cboCategory.SelectedValue == null)
                {
                    MessageBox.Show("Please select category.");
                    return;
                }

                product.CategoryId = int.Parse(cboCategory.SelectedValue.ToString());

                if (string.IsNullOrWhiteSpace(txtUnitsInStock.Text))
                {
                    product.UnitsInStock = null;
                }
                else
                {
                    product.UnitsInStock = short.Parse(txtUnitsInStock.Text.Trim());
                }

                if (string.IsNullOrWhiteSpace(txtPrice.Text))
                {
                    product.UnitPrice = null;
                }
                else
                {
                    product.UnitPrice = decimal.Parse(txtPrice.Text.Trim());
                }

                productService.SaveProduct(product);

                MessageBox.Show("Create product successfully.");
            }
            catch (Exception ex)
            {
                MessageBox.Show(
                    ex.InnerException != null ? ex.InnerException.Message : ex.Message,
                    "Create product error"
                );
            }
            finally
            {
                LoadProductList();
            }
        }

        private void btnUpdate_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(txtProductID.Text))
                {
                    MessageBox.Show("You must select a Product !");
                    return;
                }

                if (cboCategory.SelectedValue == null)
                {
                    MessageBox.Show("Please select category.");
                    return;
                }

                Product product = new Product();
                product.ProductId = int.Parse(txtProductID.Text);
                product.ProductName = txtProductName.Text.Trim();
                product.CategoryId = int.Parse(cboCategory.SelectedValue.ToString());

                if (string.IsNullOrWhiteSpace(txtUnitsInStock.Text))
                {
                    product.UnitsInStock = null;
                }
                else
                {
                    product.UnitsInStock = short.Parse(txtUnitsInStock.Text.Trim());
                }

                if (string.IsNullOrWhiteSpace(txtPrice.Text))
                {
                    product.UnitPrice = null;
                }
                else
                {
                    product.UnitPrice = decimal.Parse(txtPrice.Text.Trim());
                }

                productService.UpdateProduct(product);

                MessageBox.Show("Update product successfully.");
            }
            catch (Exception ex)
            {
                MessageBox.Show(
                    ex.InnerException != null ? ex.InnerException.Message : ex.Message,
                    "Update product error"
                );
            }
            finally
            {
                LoadProductList();
            }
        }

        private void btnDelete_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(txtProductID.Text))
                {
                    MessageBox.Show("You must select a Product !");
                    return;
                }

                int productId = int.Parse(txtProductID.Text);

                Product product = productService.GetProductById(productId);

                if (product == null)
                {
                    MessageBox.Show("Product not found.");
                    return;
                }

                MessageBoxResult result = MessageBox.Show(
                    "Are you sure you want to delete this product?",
                    "Confirm delete",
                    MessageBoxButton.YesNo,
                    MessageBoxImage.Question
                );

                if (result == MessageBoxResult.Yes)
                {
                    productService.DeleteProduct(product);

                    MessageBox.Show("Delete product successfully.");
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(
                    ex.InnerException != null ? ex.InnerException.Message : ex.Message,
                    "Delete product error"
                );
            }
            finally
            {
                LoadProductList();
            }
        }


        private void dgData_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (dgData.SelectedItem is Product selectedProduct)
            {
                txtProductID.Text = selectedProduct.ProductId.ToString();
                txtProductName.Text = selectedProduct.ProductName;

                txtPrice.Text = selectedProduct.UnitPrice.HasValue
                    ? selectedProduct.UnitPrice.Value.ToString()
                    : "";

                txtUnitsInStock.Text = selectedProduct.UnitsInStock.HasValue
                    ? selectedProduct.UnitsInStock.Value.ToString()
                    : "";

                cboCategory.SelectedValue = selectedProduct.CategoryId;
            }
        }



        private void btnClose_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void resetInput()
        {
            txtProductID.Text = "";
            txtProductName.Text = "";
            txtPrice.Text = "";
            txtUnitsInStock.Text = "";
            cboCategory.SelectedIndex = -1;
        }

      
    }
}