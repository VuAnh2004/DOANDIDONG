<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% 
    String message = (String) request.getAttribute("_message");
    String token = (String) request.getAttribute("token");
    Boolean showCaptcha = (Boolean) request.getAttribute("showCaptcha");
    if (showCaptcha == null) showCaptcha = false;

    Object accountObj = request.getAttribute("account");
    String userName = "";
    if (accountObj != null) {
        try {
            java.lang.reflect.Method getUserName = accountObj.getClass().getMethod("getUsername");
            userName = (String) getUserName.invoke(accountObj);
        } catch(Exception e) { userName = ""; }
    }
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
    <title>Hệ thống Đăng nhập | THPT Tây Hiếu</title>

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.0/font/bootstrap-icons.css" />
    <link href="${pageContext.request.contextPath}/admin/assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet" />

    <style>
        :root {
            --primary: #4361ee;
            --primary-hover: #3730a3;
            --text-main: #1e293b;
            --text-muted: #64748b;
            --input-bg: #f8fafc;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
        }

        .login-card {
            width: 100%;
            max-width: 400px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
            overflow: hidden;
        }

        .card-header-custom {
            background: var(--primary);
            padding: 2.5rem 1.5rem;
            text-align: center;
            color: white;
        }

        .card-header-custom h3 {
            font-weight: 700;
            font-size: 1.35rem;
            margin: 0;
            letter-spacing: 0.5px;
        }

        .card-header-custom p {
            font-size: 0.8rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            opacity: 0.9;
            margin-top: 6px;
            margin-bottom: 0;
        }

        .card-body {
            padding: 2rem 2rem;
        }

        .form-group-spacing {
            margin-bottom: 1.2rem;
        }

        .form-label {
            font-weight: 600;
            font-size: 0.85rem;
            color: var(--text-main);
            margin-bottom: 0.4rem;
            display: block;
        }

        .input-group-custom {
            display: flex;
            align-items: center;
            background: var(--input-bg);
            border: 1px solid #e2e8f0;
            border-radius: 10px;
            padding: 0 1rem;
            transition: all 0.2s;
            position: relative;
        }

        .input-group-custom:focus-within {
            border-color: var(--primary);
            background: #fff;
            box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
        }

        /* Tăng khoảng cách icon */
        .input-icon {
            color: var(--text-muted);
            font-size: 1.1rem;
            margin-right: 12px; /* Tăng khoảng cách bên phải của icon */
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .form-control-minimal {
            border: none;
            background: transparent;
            padding: 0.75rem 0;
            width: 100%;
            outline: none;
            font-size: 0.95rem;
            /* Đảm bảo nội dung chữ không bị sát icon */
            padding-left: 5px; 
        }

        .btn-login {
            background: var(--primary);
            color: white;
            border: none;
            width: 100%;
            padding: 0.85rem;
            border-radius: 10px;
            font-weight: 700;
            font-size: 0.95rem;
            margin-top: 0.5rem;
            transition: 0.3s;
        }

        .btn-login:hover {
            background: var(--primary-hover);
        }

        .footer-text {
            text-align: center;
            margin-top: 1.5rem;
            font-size: 0.75rem;
            color: var(--text-muted);
        }
    </style>
</head>

<body>
    <div class="login-card">
        <div class="card-header-custom">
            <h3>TRƯỜNG THPT TÂY HIẾU</h3>
            <p>Hệ thống quản trị thông minh</p>
        </div>

        <div class="card-body">
            <% if (message != null && !message.isEmpty()) { %>
                <div class="alert alert-danger py-2 px-3 mb-3" style="font-size: 0.85rem;">
                    <i class="bi bi-exclamation-triangle me-2"></i><%= message %>
                </div>
            <% } %>

            <form class="needs-validation" novalidate action="<%=request.getContextPath()%>/Login" method="post">
                <input type="hidden" name="token" value="<%= token != null ? token : "" %>" />

                <div class="form-group-spacing">
                    <label class="form-label">Tên đăng nhập</label>
                    <div class="input-group-custom">
                        <div class="input-icon">
                            <i class="bi bi-person"></i>
                        </div>
                        <input type="text" class="form-control-minimal" placeholder="Tài khoản học sinh" 
                               required name="UserName" value="<%= userName %>" />
                    </div>
                </div>

                <div class="form-group-spacing">
                    <label class="form-label">Mật khẩu</label>
                    <div class="input-group-custom">
                        <div class="input-icon">
                            <i class="bi bi-shield-lock"></i>
                        </div>
                        <input type="password" class="form-control-minimal" placeholder="Mật khẩu" 
                               required name="Password" />
                    </div>
                </div>

                <% if (showCaptcha) { %>
                <div class="mb-3 p-2 border rounded bg-light">
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <img src="<%= captchaImagePath %>" id="captchaImage" height="35" class="rounded" />
                        <button type="button" class="btn btn-link btn-sm p-0 text-decoration-none" id="refreshCaptcha">Làm mới</button>
                    </div>
                    <input type="text" class="form-control form-control-sm text-center" name="captchaInput" placeholder="Nhập mã xác thực" required />
                </div>
                <% } %>

                <button class="btn-login" type="submit">
                    ĐĂNG NHẬP
                </button>
            </form>

            <div class="footer-text">
                © 2026 Trường THPT Tây Hiếu.
            </div>
        </div>
    </div>

    <script>
        // Refresh captcha logic
        const btn = document.getElementById('refreshCaptcha');
        if (btn) {
            btn.addEventListener('click', () => {
                document.getElementById('captchaImage').src = '<%=request.getContextPath()%>/Login/CaptchaImage?' + Date.now();
            });
        }

        // Bootstrap Validation
        (function () {
            'use strict'
            var forms = document.querySelectorAll('.needs-validation')
            Array.prototype.slice.call(forms).forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }
                    form.classList.add('was-validated')
                }, false)
            })
        })()
    </script>
</body>
</html>