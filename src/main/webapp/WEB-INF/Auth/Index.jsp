<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>404 - Không tìm thấy trang</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Favicon -->
    <link rel="icon" href="<%=request.getContextPath()%>/assets/img/favicon.png">

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600;700&display=swap" rel="stylesheet">

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    <style>
        :root {
            --primary-color: #4154f1;
            --secondary-color: #717ff5;
            --dark-color: #012970;
            --light-color: #f6f9ff;
        }

        html, body {
            height: 100%;
            margin: 0;
        }

        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--light-color);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .error-container {
            width: 100%;
            height: 100%;
            background: #fff;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
        }

        .error-title {
            font-size: 2.5rem;
            font-weight: 600;
            color: var(--dark-color);
            margin-bottom: 1rem;
        }

        .error-message {
            font-size: 1.2rem;
            color: #666;
            margin-bottom: 2rem;
        }

        .error-image img {
            max-width: 500px;
            margin-bottom: 2rem;
        }

        .btn-primary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
            padding: 0.6rem 1.5rem;
            border-radius: 50px;
        }

        .btn-primary:hover {
            background-color: var(--secondary-color);
            border-color: var(--secondary-color);
        }

        .countdown {
            margin-top: 1rem;
            color: #888;
            font-size: 0.9rem;
        }
    </style>
</head>

<body>

<div class="error-container">

    <h1 class="error-title">Không tìm thấy trang</h1>
    <p class="error-message">
        Ồ!  Bạn không có quyền truy cập vào khu vực này.
    </p>

    <div class="error-image">
        <img src="<%=request.getContextPath()%>/assets/img/phanquyen.png" alt="Access Denied">
    </div>

    <a href="<%=request.getContextPath()%>/" class="btn btn-primary">
        <i class="fas fa-home me-2"></i>Quay về trang chủ
    </a>

    <div class="countdown">
        Bạn sẽ được quay về trang chủ sau <span id="countdown">15</span> giây.
    </div>

</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    let seconds = 15;
    const el = document.getElementById("countdown");

    const timer = setInterval(() => {
        seconds--;
        el.innerText = seconds;

        if (seconds <= 0) {
            clearInterval(timer);
            window.location.href = "<%=request.getContextPath()%>/";
        }
    }, 1000);
</script>

</body>
</html>
