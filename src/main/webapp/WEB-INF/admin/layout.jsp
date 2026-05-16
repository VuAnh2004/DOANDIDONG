<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hệ thống quản trị thông minh</title>
    
    <%-- LẤY CONTEXT PATH ĐỘNG CHO TẤT CẢ ĐƯỜNG DẪN TĨNH --%>
    <% String contextPath = request.getContextPath(); %>

    <link href="<%= contextPath %>/admin/assets/img/favicon.png" rel="icon">
    <link href="<%= contextPath %>/admin/assets/img/apple-touch-icon.png" rel="apple-touch-icon">

    <link href="https://fonts.gstatic.com" rel="preconnect">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300,400,600,700|Nunito:300,400,600,700|Poppins:300,400,500,600,700" rel="stylesheet">

    <link href="<%= contextPath %>/admin/assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="<%= contextPath %>/admin/assets/vendor/bootstrap-icons/bootstrap-icons.css" rel="stylesheet"> <%-- LỖI ICON Ở ĐÂY --%>
    <link href="<%= contextPath %>/admin/assets/vendor/boxicons/css/boxicons.min.css" rel="stylesheet">
    <link href="<%= contextPath %>/admin/assets/vendor/quill/quill.snow.css" rel="stylesheet">
    <link href="<%= contextPath %>/admin/assets/vendor/quill/quill.bubble.css" rel="stylesheet">
    <link href="<%= contextPath %>/admin/assets/vendor/remixicon/remixicon.css" rel="stylesheet">
    <link href="<%= contextPath %>/admin/assets/vendor/simple-datatables/style.css" rel="stylesheet">

    <link href="<%= contextPath %>/admin/assets/css/style.css" rel="stylesheet">
</head>

<body>

<jsp:include page="/WEB-INF/admin/include/header.jsp" />

<jsp:include page="/WEB-INF/admin/include/sidebar.jsp" />

<%-- 
    KHU VỰC CHÈN NỘI DUNG CHÍNH (<main>)
    - Dòng "<jsp:include page="/WEB-INF/admin/include/index.jsp" />" đã bị xóa
    - Thay vào đó là logic chèn nội dung động (dựa trên request attribute từ Controller)
--%>
    
    <%
        // Lấy tên tệp nội dung cần chèn (ví dụ: QLKhoi/Index.jsp)
        String contentPage = (String) request.getAttribute("contentPage");
        
        if (contentPage != null) {
            // Chèn nội dung động
            %>
            <jsp:include page="<%= contentPage %>" />
            <%
        } else {
            // Mặc định, hiển thị Dashboard (index.jsp)
            %>
            <jsp:include page="/WEB-INF/admin/include/index.jsp" />
            <%
        }
    %>


<script src="<%= contextPath %>/admin/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/apexcharts/apexcharts.min.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/chart.js/chart.min.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/echarts/echarts.min.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/quill/quill.min.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/simple-datatables/simple-datatables.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/tinymce/tinymce.min.js"></script>
<script src="<%= contextPath %>/admin/assets/vendor/php-email-form/validate.js"></script>

<script src="<%= contextPath %>/admin/assets/js/main.js"></script>

</body>
</html>