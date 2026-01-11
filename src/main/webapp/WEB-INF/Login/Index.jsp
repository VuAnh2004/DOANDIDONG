
 <%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<% 
    String message = (String) request.getAttribute("_message");
    String token = (String) request.getAttribute("token");
    Boolean showCaptcha = (Boolean) request.getAttribute("showCaptcha");
    if (showCaptcha == null) showCaptcha = false;

    Object accountObj = request.getAttribute("account");
    String userName = "";
    if (accountObj != null) {
        try {
            // Lấy userName từ đối tượng Account nếu có
            java.lang.reflect.Method getUserName = accountObj.getClass().getMethod("getUsername");
            userName = (String) getUserName.invoke(accountObj);
        } catch(Exception e) {
            userName = "";
        }
    }
    
    // Lấy đường dẫn Captcha cho JSP
    String captchaImagePath = (String) request.getAttribute("captchaImage");
    if (captchaImagePath == null) {
        captchaImagePath = request.getContextPath() + "/Login/CaptchaImage";
    }
%>

<!DOCTYPE html>
<html lang="vi">

<head>
<meta charset="utf-8" />
<meta content="width=device-width, initial-scale=1.0" name="viewport" />

<title>Học sinh đăng nhập</title>

<link href="assets/img/logo.jpg" rel="icon" />
<link href="assets/img/logo.jpg" rel="apple-touch-icon" />

<link
	href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap"
	rel="stylesheet" />

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.0/font/bootstrap-icons.css" />

<link
	href="${pageContext.request.contextPath}/admin/assets/vendor/bootstrap/css/bootstrap.min.css"
	rel="stylesheet" />

<style>
:root {
	--primary-color: #4361ee;
	--secondary-color: #3f37c9;
	--accent-color: #4895ef;
	--light-color: #f8f9fa;
	--dark-color: #212529;
	--success-color: #4cc9f0;
	--danger-color: #f72585;
}

body {
	font-family: 'Inter', sans-serif;
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	min-height: 100vh;
	display: flex;
	align-items: center;
	padding: 20px 0;
}

.login-container {
	width: 100%;
	max-width: 480px;
	margin: 0 auto;
}

.login-card {
	border: none;
	border-radius: 10px;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
	overflow: hidden;
	transition: all 0.3s ease;
	background: white;
}

.login-card:hover {
	transform: translateY(-3px);
	box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

/* TẠO HEADER MÀU XANH GIỐNG ẢNH MẪU */
.card-header-custom {
	background: var(--primary-color);
	color: white;
	text-align: center;
	padding: 3rem 1.5rem;
	border-bottom: none;
	border-top-left-radius: 10px;
	border-top-right-radius: 10px;
}

.card-header-custom .logo {
	height: 40px;
	width: auto;
	margin-bottom: 0.5rem;
}

.card-header-custom h3 {
	font-weight: 700;
	margin-bottom: 0.25rem;
	font-size: 1.5rem;
}

.card-header-custom p {
	font-size: 0.95rem;
	opacity: 0.9;
	margin-bottom: 0;
	font-weight: 500;
}

.card-body {
	padding: 2rem;
}

/* CẢI TIẾN INPUT FIELD: ICON NẰM NGOÀI TRƯỜNG NHẬP */
.form-label {
	font-weight: 500;
	color: var(--dark-color);
	margin-bottom: 0.25rem;
}

.input-icon {
	color: var(--primary-color);
	margin-right: 8px;
	font-size: 1.1rem;
	width: 20px;
	text-align: center;
}

.input-control-group {
	display: flex;
	align-items: center;
	border: 1px solid #e0e0e0;
	border-radius: 8px;
	padding: 0 15px;
	transition: all 0.3s;
	background-color: #fff;
}

.input-control-group:focus-within {
	border-color: var(--primary-color);
	box-shadow: 0 0 0 0.25rem rgba(67, 97, 238, 0.25);
}

.form-control-minimal {
	border: none;
	padding: 12px 0;
	box-shadow: none !important;
	flex-grow: 1;
}

.form-control-minimal:focus {
	outline: none;
}
/* END CẢI TIẾN INPUT FIELD */

.btn-login {
	background: var(--primary-color);
	border: none;
	border-radius: 8px;
	padding: 12px;
	font-weight: 600;
	letter-spacing: 0.5px;
	transition: all 0.3s;
	color: white;
}

.btn-login:hover {
	background: var(--secondary-color);
	transform: translateY(-2px);
}

.error-message {
	background-color: rgba(247, 37, 133, 0.1);
	border-left: 4px solid var(--danger-color);
	padding: 0.75rem 1rem;
	border-radius: 4px;
	margin-bottom: 1rem;
	color: var(--danger-color);
	font-weight: 500;
}

.captcha-img {
	height: 40px;
	width: auto;
	display: block;
}

.btn-captcha-refresh {
	background: none;
	border: none;
	color: #6c757d;
	padding: 0.35rem;
	cursor: pointer;
	transition: all 0.2s;
	font-size: 1rem;
	width: 32px;
	height: 32px;
	display: flex;
	align-items: center;
	justify-content: center;
}

.btn-captcha-refresh:hover {
	color: #0d6efd;
}

.btn-captcha-refresh:active i {
	transform: rotate(360deg);
	transition: transform 0.3s ease;
}

@media ( max-width : 576px) {
	.card-body {
		padding: 1.5rem;
	}
	.card-header-custom {
		padding: 2rem 1rem;
	}
	.card-header-custom h3 {
		font-size: 1.25rem;
	}
}
</style>
</head>

<body>
	<div class="login-container">
		<div class="login-card">
			<div class="card-header-custom">
				<div class="mb-3">
					<img src="assets/img/logo1.png" alt="Logo" class="logo"
						style="height: 40px; width: auto; filter: brightness(0) invert(1);" /> 
				</div>
				
				<h3 class="mb-0">TRƯỜNG THPT TÂY HIẾU</h3>
				<p class="text-uppercase">HỆ THỐNG QUẢN TRỊ TRƯỜNG HỌC THÔNG MINH</p>
			</div>

			<div class="card-body">
				<% if (message != null && !message.isEmpty()) { %>
				<div class="error-message">
					<i class="fas fa-exclamation-circle me-2"></i><%= message %>
				</div>
				<% } %>

				<form class="needs-validation" novalidate
					action="<%=request.getContextPath()%>/Login" method="post">

					<input type="hidden" name="token"
						value="<%= token != null ? token : "" %>" />

					<div class="mb-4">
						<label for="yourUsername" class="form-label">Tên đăng nhập</label>
						<div class="input-control-group">
							<i class="bi bi-person-fill input-icon"></i> 
							<input type="text"
								class="form-control-minimal" id="yourUsername"
								placeholder="Nhập tên đăng nhập" required name="UserName"
								value="<%= userName %>" />
						</div>
						<div class="invalid-feedback">Vui lòng nhập tên đăng nhập</div>
					</div>

					<div class="mb-4">
						<label for="yourPassword" class="form-label">Mật khẩu</label>
						<div class="input-control-group">
							<i class="bi bi-lock-fill input-icon"></i>
							<input type="password" class="form-control-minimal" id="yourPassword"
								placeholder="Nhập mật khẩu" required name="Password" />
						</div>
						<div class="invalid-feedback">Vui lòng nhập mật khẩu</div>
					</div>

					<% if (showCaptcha) { %>
					<div class="mb-3 text-center">
						<label class="form-label">Mã xác thực</label>
						<div class="d-flex justify-content-center align-items-center">
							<div class="border rounded d-flex align-items-center"
								style="padding: 2px; background: white;">
								<img src="<%= captchaImagePath %>"
									alt="Captcha" class="captcha-img" id="captchaImage" />

								<button type="button" class="btn-captcha-refresh ms-1"
									id="refreshCaptcha" title="Làm mới mã">
									<i class="fas fa-sync-alt"></i>
								</button>
							</div>
						</div>
						<div class="mt-2 mx-auto" style="max-width: 200px;">
							<input type="text" class="form-control text-center"
								name="captchaInput" placeholder="Nhập mã xác thực" required />
						</div>
						<div class="invalid-feedback">Vui lòng nhập mã xác thực</div>
					</div>
					<% } %>

					<div class="d-grid gap-2 mt-4">
						<button class="btn btn-primary btn-login" type="submit">
							<i class="fas fa-arrow-right me-2"></i>Đăng nhập
						</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<script>
        // Bootstrap 5 form validation
        (function () {
            'use strict';
            var forms = document.querySelectorAll('.needs-validation');
            Array.prototype.forEach.call(forms, function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        })();

        // Refresh captcha
        document.addEventListener('DOMContentLoaded', function () {
            var captchaRefreshBtn = document.getElementById('refreshCaptcha');
            var captchaImage = document.getElementById('captchaImage');
            if (captchaRefreshBtn && captchaImage) {
                captchaRefreshBtn.addEventListener('click', function () {
                    // Cập nhật đường dẫn Captcha khi refresh
                    captchaImage.src = '<%=request.getContextPath()%>/Login/CaptchaImage?' + new Date().getTime();
                    const icon = this.querySelector('i');
                    icon.style.transform = 'rotate(360deg)';
                    setTimeout(() => { icon.style.transform = 'rotate(0)'; }, 300);
                });
            }
        });
    </script>
</body>
</html>