<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLKhoi"%>

<%
    QLKhoi q = (QLKhoi) request.getAttribute("q");
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Sửa Khối</h2>
    </div>

    <%-- SỬA LỖI: Thay đổi action từ /Sua thành /Edit --%>
    <form action="<%= request.getContextPath() %>/admin/QLKhoi/Edit" method="post">
        <input type="hidden" name="GradeLevelId" value="<%= q.getGradeLevelId() %>" />
        <div class="mb-3">
            <label>Tên Khối</label>
            <input type="text" name="GradeName" class="form-control" value="<%= q.getGradeName() %>" required>
        </div>
        <div class="mb-3">
            <label>Mô tả</label>
            <textarea name="Description" class="form-control"><%= q.getDescription() %></textarea>
        </div>
        <div class="mb-3 form-check">
            <input type="checkbox" name="IsActive" class="form-check-input" 
                   <%= q.isActive() ? "checked" : "" %>>
            <label class="form-check-label">Hiển thị</label>
        </div>
        <button type="submit" class="btn btn-primary">Cập nhật</button>
        <a href="<%= request.getContextPath() %>/admin/QLKhoi/Index" class="btn btn-secondary">Hủy</a>
    </form>
</main>