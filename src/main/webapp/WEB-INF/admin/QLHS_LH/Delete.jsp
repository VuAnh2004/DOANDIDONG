<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Xóa học sinh</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
</head>
<body>
<main id="main" class="main">
    <h2>Xác nhận xóa học sinh</h2>
    <p>Bạn có chắc muốn xóa học sinh <strong>${hs_lh.hocsinh != null ? hs_lh.hocsinh.fullName : hs_lh.studentID}</strong> khỏi lớp?</p>
    <form action="${pageContext.request.contextPath}/admin/QLHS_LH/delete" method="post">
        <input type="hidden" name="HocSinhLopHocID" value="${hs_lh.hocSinhLopHocID}" />
        <button type="submit" class="btn btn-danger">Xóa</button>
        <a href="${pageContext.request.contextPath}/admin/QLHS_LH" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>
