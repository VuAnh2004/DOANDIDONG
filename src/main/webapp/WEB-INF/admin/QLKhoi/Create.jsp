<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Thêm Khối mới</h2>
    </div>

    <form action="<%= request.getContextPath() %>/admin/QLKhoi/Create" method="post">
        <div class="mb-3">
            <label>Tên Khối</label>
            <input type="text" name="GradeName" class="form-control" required>
        </div>
        <div class="mb-3">
            <label>Mô tả</label>
            <textarea name="Description" class="form-control"></textarea>
        </div>
        <div class="mb-3 form-check">
            <input type="checkbox" name="IsActive" class="form-check-input" checked>
            <label class="form-check-label">Hiển thị</label>
        </div>
        <button type="submit" class="btn btn-success">Thêm</button>
        <a href="<%= request.getContextPath() %>/admin/QLKhoi/Index" class="btn btn-secondary">Hủy</a>
    </form>
</main>
