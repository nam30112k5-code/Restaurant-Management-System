document.addEventListener("DOMContentLoaded", function () {
    // 1. NAVBAR SCROLL EFFECT
    const navbar = document.querySelector('.navbar-custom');
    if (navbar) {
        window.addEventListener('scroll', function () {
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });
    }

    // 2. PASSWORD TOGGLE (LOGIN)
    const togglePassword = document.querySelector('#togglePassword');
    const passwordInput = document.querySelector('#id_password');
    const eyeIcon = document.querySelector('#eyeIcon');
    if (togglePassword && passwordInput && eyeIcon) {
        togglePassword.addEventListener('click', function (e) {
            e.preventDefault();
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            if (type === 'text') {
                eyeIcon.classList.remove('bi-eye-slash');
                eyeIcon.classList.add('bi-eye');
            } else {
                eyeIcon.classList.remove('bi-eye');
                eyeIcon.classList.add('bi-eye-slash');
            }
        });
    }

    // 3. STAR RATING LOGIC (FEEDBACK)
    const ratingInput = document.getElementById('ratingValue');
    const starIcons = document.querySelectorAll('.star-icon');
    const textLabel = document.getElementById('ratingText');
    if (ratingInput && starIcons.length > 0) {
        function updateStarUI(rating) {
            ratingInput.value = rating;
            starIcons.forEach(star => {
                let starVal = parseInt(star.getAttribute('data-value'));
                if (starVal <= rating) {
                    star.classList.add('active');
                    star.classList.remove('bi-star');
                    star.classList.add('bi-star-fill');
                } else {
                    star.classList.remove('active');
                    star.classList.remove('bi-star-fill');
                    star.classList.add('bi-star');
                }
            });
            if (textLabel) {
                switch (rating) {
                    case 1:
                        textLabel.innerHTML = "Terrible";
                        textLabel.className = "mt-2 text-danger fw-bold small";
                        break;
                    case 2:
                        textLabel.innerHTML = "Bad";
                        textLabel.className = "mt-2 text-danger fw-bold small";
                        break;
                    case 3:
                        textLabel.innerHTML = "Normal";
                        textLabel.className = "mt-2 text-white fw-bold small";
                        break;
                    case 4:
                        textLabel.innerHTML = "Good";
                        textLabel.className = "mt-2 text-success fw-bold small";
                        break;
                    case 5:
                        textLabel.innerHTML = "Excellent";
                        textLabel.className = "mt-2 text-warning fw-bold small";
                        break;
                }
            }
        }
        starIcons.forEach(star => {
            star.addEventListener('click', function () {
                const val = parseInt(this.getAttribute('data-value'));
                updateStarUI(val);
            });
        });
        updateStarUI(5);
    }

});

// 4. GLOBAL FUNCTIONS (MODALS)
// Hàm mở Modal Booking cho Admin (Table Management)
window.openAdminBookingModal = function (id, name) {
    const idInput = document.getElementById('modalTableId');
    const nameInput = document.getElementById('modalTableName');
    const modalEl = document.getElementById('adminBookModal');

    if (idInput && nameInput && modalEl) {
        idInput.value = id;
        nameInput.value = name;
        new bootstrap.Modal(modalEl).show();
    }
};

// Hàm mở Modal Booking cho Staff/Guest (Map Booking)
window.openBookingModal = function (id, name) {
    const idInput = document.getElementById('modalTableId');
    const nameInput = document.getElementById('modalTableName');
    const modalEl = document.getElementById('bookingModal');

    if (idInput && nameInput && modalEl) {
        idInput.value = id;
        nameInput.value = name;
        new bootstrap.Modal(modalEl).show();
    }
};