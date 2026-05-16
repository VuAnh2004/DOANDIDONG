<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="model.bean.QLHocSinh"%>
<%
    QLHocSinh hs = (QLHocSinh) request.getAttribute("hocSinh");
    if (hs == null) {
        response.sendRedirect(request.getContextPath() + "/admin/QLHocSinh/Index");
        return;
    }
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Xóa Học Sinh</h2>
    </div>

    <div class="card p-3 mb-3">
        <h5>Thông tin học sinh</h5>
        <div class="row">
            <div class="col-md-6 mb-2">
                <label>ID:</label>
                <input type="text" class="form-control" value="<%= hs.getID() %>" disabled>
            </div>
            <div class="col-md-6 mb-2">
                <label>Mã học sinh:</label>
                <input type="text" class="form-control" value="<%= hs.getStudentID() %>" disabled>
            </div>
            <div class="col-md-6 mb-2">
                <label>Họ và tên:</label>
                <input type="text" class="form-control" value="<%= hs.getFullName() %>" disabled>
            </div>
            <div class="col-md-6 mb-2">
                <label>Ảnh hiện tại:</label><br>
                <% if (hs.getImages() != null && !hs.getImages().isEmpty()) { %>
                    <img src="<%= request.getContextPath() + "/" + hs.getImages() %>" width="120" alt="Học sinh">
                <% } else { %>
                    <span>Chưa có ảnh</span>
                <% } %>
            </div>
        </div>
    </div>

    <form action="<%= request.getContextPath() %>/admin/QLHocSinh/Delete" method="POST">
        <input type="hidden" name="ID" value="<%= hs.getID() %>">

        <a class="btn btn-warning" href="<%= request.getContextPath() %>/admin/QLHocSinh/Index">
            <i class="bi bi-arrow-left-circle"></i> Quay lại
        </a>

        <button type="submit" class="btn btn-danger">
            <i class="bi bi-trash"></i> Xóa
        </button>
    </form>
</main>
