<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.Menu" %>

<%
    // Lấy đối tượng menu cần xóa từ Controller truyền qua
    Menu item = (Menu) request.getAttribute("menuItem");
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>Xác nhận xóa Menu</h2>
    </div>

    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body p-4">
            <div class="alert alert-warning">
                <i class="bi bi-exclamation-triangle me-2"></i>
                Bạn có chắc chắn muốn xóa Menu này?
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="fw-bold">Mã Menu</label>
                    <input type="text" class="form-control mt-1" value="<%= item.getMenuID() %>" disabled />
                </div>
                <div class="col-md-6 mb-3">
                    <label class="fw-bold">Tên Menu</label>
                    <input type="text" class="form-control mt-1" value="<%= item.getMenuName() %>" disabled />
                </div>
                <div class="col-md-6 mb-3">
                    <label class="fw-bold">Tên bộ điều khiển</label>
                    <input type="text" class="form-control mt-1" value="<%= item.getControllerName() != null ? item.getControllerName() : "-" %>" disabled />
                </div>
                <div class="col-md-6 mb-3">
                    <label class="fw-bold">Tên hành động</label>
                    <input type="text" class="form-control mt-1" value="<%= item.getActionName() != null ? item.getActionName() : "-" %>" disabled />
                </div>
                <div class="col-md-4 mb-3">
                    <label class="fw-bold">Cấp độ</label>
                    <input type="text" class="form-control mt-1" value="<%= item.getLevels() %>" disabled />
                </div>
                <div class="col-md-4 mb-3">
                    <label class="fw-bold">Thứ tự</label>
                    <input type="text" class="form-control mt-1" value="<%= item.getMenuOrder() %>" disabled />
                </div>
                <div class="col-md-4 mb-3">
                    <label class="fw-bold">Trạng thái hiển thị</label>
                    <div class="mt-2">
                        <span class="badge <%= item.isActive() ? "bg-success" : "bg-secondary" %>">
                            <%= item.isActive() ? "Đang hiển thị" : "Đang ẩn" %>
                        </span>
                    </div>
                </div>
            </div>

            <form action="<%= request.getContextPath() %>/admin/Menu/Delete" method="post" class="mt-4">
                <input type="hidden" name="MenuID" value="<%= item.getMenuID() %>" />
                
                <a class="btn btn-secondary px-4 me-2" href="<%= request.getContextPath() %>/admin/Menu/Index">
                    <i class="bi bi-arrow-left-circle me-1"></i> Quay lại
                </a>
                
                <button type="submit" class="btn btn-danger px-4">
                    <i class="bi bi-trash me-1"></i> Xác nhận xóa
                </button>
            </form>
            </div>
    </div>
</main>