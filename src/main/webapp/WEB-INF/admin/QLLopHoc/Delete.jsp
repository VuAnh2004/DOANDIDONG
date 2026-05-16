<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.bean.QLLopHoc"%>

<%
    QLLopHoc lop = (QLLopHoc) request.getAttribute("lophoc");
    if (lop == null) {
        response.sendRedirect(request.getContextPath() + "/admin/QLLopHoc/Index");
        return;
    }
%>

<main id="main" class="main">
    <div class="container p-5">
        <h3>Xóa lớp học: <%= lop.getClassName() %> - <%= lop.getSchoolYear() %></h3>
        <p>Bạn có chắc chắn muốn xóa lớp học này không?</p>

        <form action="<%=request.getContextPath()%>/admin/QLLopHoc/Delete" method="post">
            <input type="hidden" name="ClassID" value="<%= lop.getClassID() %>" />
            
            <button type="submit" class="btn btn-danger">
                <i class="bi bi-trash"></i> Xóa
            </button>
            <a href="<%=request.getContextPath()%>/admin/QLLopHoc/Index" class="btn btn-secondary">
                <i class="bi bi-arrow-left-circle"></i> Quay lại
            </a>
        </form>
    </div>
</main>
