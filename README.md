## Project Overview

**Restaurant Reservation Management Database** is a web application designed to manage restaurant reservations efficiently.
The system helps optimize and improve the reservation management process for restaurants.

## Technologies Used

* **Java JDK 17**
* **NetBeans 17**
* **Apache Tomcat 10.1.x**
* **Microsoft SQL Server 2019 or higher**
* **SQL Server Management Studio (SSMS) 2019 or higher**

## Database Connection Setup (Testing)

To configure the database connection for testing the application, follow the steps below:

1. Run the **`RestaurantManagement.sql`** file to create the database and required tables.
2. Open the project using **NetBeans**.
3. Navigate to:
   **Web Pages / WEB-INF / `ConnectDB.properties`**
4. Update the database credentials:

   * `userID` (default: `sa`)
   * `password` (default: `123`)
5. Save the file and run the project on **Apache Tomcat**.

No additional configuration is required.

## Thiết lập kết nối CSDL (Test chương trình)

Để thiết lập kết nối cơ sở dữ liệu và chạy thử chương trình, vui lòng thực hiện:

1. Chạy file **`RestaurantManagement.sql`** để tạo database và các bảng cần thiết.
2. Mở project bằng **NetBeans**.
3. Truy cập:
   **Web Pages / WEB-INF / `ConnectDB.properties`**
4. Chỉnh sửa thông tin đăng nhập:

   * `userID`: `sa` (mặc định)
   * `password`: `123` (mặc định)
5. Lưu file và chạy project trên **Tomcat**.

Không cần chỉnh sửa thêm file nào khác.