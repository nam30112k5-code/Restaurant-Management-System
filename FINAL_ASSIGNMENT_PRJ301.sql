USE master
GO

-- 1. Kiểm tra và xóa DB cũ nếu tồn tại để tạo mới sạch sẽ
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'ManagerRestaurant')
BEGIN
    ALTER DATABASE ManagerRestaurant SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE ManagerRestaurant;
END
GO

CREATE DATABASE ManagerRestaurant
GO

USE ManagerRestaurant
GO

-- =============================================
-- 2. TẠO BẢNG (TABLES)
-- =============================================

-- Bảng Quyền (Roles)
CREATE TABLE Roles (
	roleId INT IDENTITY(1,1) PRIMARY KEY,
	roleName NVARCHAR(100) NOT NULL
)
GO

-- Bảng Bàn ăn (Tables)
CREATE TABLE Tables (
	tableId INT IDENTITY(1,1) PRIMARY KEY,
	tableName NVARCHAR(100) NOT NULL
)
GO

-- Bảng Nhân viên (Employees)
CREATE TABLE Employees (
	employeeId INT IDENTITY(1,1) PRIMARY KEY,
	username NVARCHAR(10) NOT NULL UNIQUE, -- Thêm UNIQUE để làm khóa ngoại cho Appointments
	password NVARCHAR(MAX) NOT NULL,
	identityNumber NVARCHAR(12),
	phoneNumber NVARCHAR(10),
	dateOfBirth DATE,
	gender NVARCHAR(6) CHECK (gender IN ('male', 'female')),
    isActive BIT NOT NULL DEFAULT 1,
	roleId INT REFERENCES Roles(roleId)
)
GO

-- Bảng Khách hàng (Guests)
CREATE TABLE Guests (
	guestId INT IDENTITY(1,1) PRIMARY KEY,
	username NVARCHAR(50) UNIQUE, -- Tăng độ dài và thêm UNIQUE
	password NVARCHAR(MAX) NOT NULL,
	name NVARCHAR(50),
	identityNumber NVARCHAR(12),
	phoneNumber NVARCHAR(10),
	dateOfBirth DATE,
	gender NVARCHAR(6) CHECK (gender IN ('male', 'female')),
    isActive BIT NOT NULL DEFAULT 1,
)
GO

-- Bảng Đặt bàn (Appointments)
CREATE TABLE Appointments(
	appointmentId UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWSEQUENTIALID(),
	guestId INT REFERENCES Guests(guestId), -- Khách đặt (có thể NULL nếu khách vãng lai không có tk)
	tableId INT REFERENCES Tables(tableId),
	createBy NVARCHAR(10),
	startTime TIME,
	endTime TIME,
	date DATE,
	status NVARCHAR(20) 
        CHECK (status IN ('pending', 'confirmed', 'cancelled', 'completed')),
)
GO

-- Bảng Đánh giá (Feedbacks)
CREATE TABLE Feedbacks (
    feedbackId INT IDENTITY(1,1) PRIMARY KEY,
    content NVARCHAR(MAX) NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    guestId INT NOT NULL REFERENCES Guests(guestId),
    appointmentId UNIQUEIDENTIFIER NOT NULL REFERENCES Appointments(appointmentId)
);
GO

-- =============================================
-- 3. CHÈN DỮ LIỆU MẪU (SEED DATA)
-- =============================================

-- A. Roles
INSERT INTO Roles (roleName) VALUES (N'admin'), (N'receptionist');

-- B. Tables (10 bàn)
INSERT INTO Tables (tableName) VALUES
    (N'Table 1 (VIP)'), (N'Table 2 (Window)'), (N'Table 3'), (N'Table 4'), (N'Table 5'),
    (N'Table 6'), (N'Table 7'), (N'Table 8'), (N'Table 9'), (N'Table 10 (Large)');

-- C. Employees (Mật khẩu mặc định là '123' cho tất cả)
-- Hash: $2a$12$M8Lm19Q5YsADHL96QBiEZOQgyhk40HQ4aOAXMR44men1ApcCINMaa
INSERT INTO Employees (username, password, identityNumber, phoneNumber, dateOfBirth, gender, isActive, roleId)
VALUES
    -- Admin
    ('admin', '$2a$12$M8Lm19Q5YsADHL96QBiEZOQgyhk40HQ4aOAXMR44men1ApcCINMaa', '001090000001', '0901234567', '1990-01-01', 'male', 1, 1),
    -- Receptionist 1
    ('staff1', '$2a$12$M8Lm19Q5YsADHL96QBiEZOQgyhk40HQ4aOAXMR44men1ApcCINMaa', '001090000002', '0901234568', '1995-05-15', 'female', 1, 2),
    -- Receptionist 2 (Đã nghỉ việc - isActive = 0)
    ('staff2', '$2a$12$M8Lm19Q5YsADHL96QBiEZOQgyhk40HQ4aOAXMR44men1ApcCINMaa', '001090000003', '0901234569', '1998-08-20', 'male', 0, 2);

-- D. Guests (Mật khẩu '123')
INSERT INTO Guests (username, password, name, identityNumber, phoneNumber, dateOfBirth, gender, isActive)
VALUES
    -- Khách hàng 1 (Nam)
    ('guest', '$2a$12$5IhcjRdqPkj0kan2CyU8h.X5JsA5blPG6Pzrejp84buGbRhQjbEa6', N'Nguyen Van An', '034090000001', '0912345678', '1992-03-10', 'male', 1),
    -- Khách hàng 2 (Nữ)
    ('guest2', '$2a$12$5IhcjRdqPkj0kan2CyU8h.X5JsA5blPG6Pzrejp84buGbRhQjbEa6', N'Tran Thi Bich', '034090000002', '0987654321', '1996-07-22', 'female', 1),
    -- Khách hàng 3 (Bị khóa)
    ('guest_ban', '$2a$12$5IhcjRdqPkj0kan2CyU8h.X5JsA5blPG6Pzrejp84buGbRhQjbEa6', N'Le Van Cuong', '034090000003', '0999888777', '2000-01-01', 'male', 0);

-- E. Appointments (Dữ liệu quan trọng để test logic)

-- Trường hợp 1: Đơn đã hoàn thành trong quá khứ (Guest 1 đặt, đã ăn xong)
INSERT INTO Appointments (guestId, tableId, createBy, startTime, endTime, date, status)
VALUES (1, 1, NULL, '18:00:00', '20:00:00', DATEADD(DAY, -5, CAST(GETDATE() AS DATE)), 'completed');

-- Trường hợp 2: Đơn đã hoàn thành (Guest 2 đặt)
INSERT INTO Appointments (guestId, tableId, createBy, startTime, endTime, date, status)
VALUES (2, 2, NULL, '19:00:00', '21:00:00', DATEADD(DAY, -3, CAST(GETDATE() AS DATE)), 'completed');

-- Trường hợp 3: Đơn đang chờ duyệt (Pending) - Ngày tương lai (Ngày mai)
INSERT INTO Appointments (guestId, tableId, createBy, startTime, endTime, date, status)
VALUES (1, 3, NULL, '18:30:00', '20:30:00', DATEADD(DAY, 1, CAST(GETDATE() AS DATE)), 'pending');

-- Trường hợp 4: Đơn đã xác nhận (Confirmed) - Ngày tương lai (Ngày kia)
INSERT INTO Appointments (guestId, tableId, createBy, startTime, endTime, date, status)
VALUES (2, 1, NULL, '12:00:00', '14:00:00', DATEADD(DAY, 2, CAST(GETDATE() AS DATE)), 'confirmed');

-- Trường hợp 5: Đơn đã bị hủy (Cancelled)
INSERT INTO Appointments (guestId, tableId, createBy, startTime, endTime, date, status)
VALUES (1, 4, NULL, '20:00:00', '22:00:00', DATEADD(DAY, -1, CAST(GETDATE() AS DATE)), 'cancelled');

-- Trường hợp 6: Đặt bàn trực tiếp tại quán (Walk-in) do nhân viên 'staff1' tạo
INSERT INTO Appointments (guestId, tableId, createBy, startTime, endTime, date, status)
VALUES (NULL, 5, 'staff1', '11:00:00', '13:00:00', CAST(GETDATE() AS DATE), 'confirmed');


-- F. Feedbacks
-- Lấy ID của đơn hoàn thành (Trường hợp 1) để feedback
DECLARE @AppointID_1 UNIQUEIDENTIFIER;
SELECT TOP 1 @AppointID_1 = appointmentId FROM Appointments WHERE status = 'completed' AND guestId = 1;

IF @AppointID_1 IS NOT NULL
BEGIN
    INSERT INTO Feedbacks (content, rating, guestId, appointmentId)
    VALUES (N'Đồ ăn rất ngon, phục vụ nhiệt tình!', 5, 1, @AppointID_1);
END

-- Lấy ID của đơn hoàn thành khác (Trường hợp 2) để feedback
DECLARE @AppointID_2 UNIQUEIDENTIFIER;
SELECT TOP 1 @AppointID_2 = appointmentId FROM Appointments WHERE status = 'completed' AND guestId = 2;

IF @AppointID_2 IS NOT NULL
BEGIN
    INSERT INTO Feedbacks (content, rating, guestId, appointmentId)
    VALUES (N'Bàn hơi ồn, nhưng món ăn ổn.', 3, 2, @AppointID_2);
END

GO