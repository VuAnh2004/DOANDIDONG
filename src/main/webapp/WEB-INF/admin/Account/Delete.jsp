<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.bean.Account"%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Xóa thông tin tài khoản</h2>
    </div>

    <%
        Account acc = (Account) request.getAttribute("account");
    %>

    <form action="${pageContext.request.contextPath}/admin/Account/Delete" method="post">
        <input type="hidden" name="UserID" value="<%= acc.getUserID() %>" />
        <div class="mb-3">
            <label>Mã tài khoản</label>
            <input type="text" class="form-control" value="<%= acc.getUserID() %>" disabled />
        </div>
        <div class="mb-3">
            <label>Tên đăng nhập</label>
            <input type="text" class="form-control" value="<%= acc.getUserName() %>" disabled />
        </div>

        <a href="${pageContext.request.contextPath}/admin/Account/Index" class="btn btn-warning">
            <i class="bi bi-arrow-left-circle"></i> Quay lại
        </a>
        <button type="submit" class="btn btn-danger">
            <i class="bi bi-trash"></i> Xóa
        </button>
    </form>
</main>
