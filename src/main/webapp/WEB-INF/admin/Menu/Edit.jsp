<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.Menu"%>

<%
    // Lấy đối tượng menu cần sửa và danh sách menu cha từ Controller
    Menu item = (Menu) request.getAttribute("menuItem");
    List<Menu> parentList = (List<Menu>) request.getAttribute("listMenu");
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>Chỉnh sửa Menu</h2>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-4">
            <form action="<%=request.getContextPath()%>/admin/Menu/Edit" method="post">
                <input type="hidden" name="MenuID" value="<%=item.getMenuID()%>">

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên Menu</label>
                        <input type="text" name="MenuName" class="form-control" 
                               value="<%=item.getMenuName()%>" required>
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Biểu tượng</label>
                        <input type="text" name="Icon" class="form-control" 
                               value="<%=item.getIcon() != null ? item.getIcon() : ""%>">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên bộ điều khiển</label>
                        <input type="text" name="ControllerName" class="form-control" 
                               value="<%=item.getControllerName() != null ? item.getControllerName() : ""%>">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên hành động</label>
                        <input type="text" name="ActionName" class="form-control" 
                               value="<%=item.getActionName() != null ? item.getActionName() : ""%>">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Thuộc Menu Cha</label>
                        <select name="ParentID" id="parentID" class="form-select">
                            <option value="0" <%= item.getParentID() == 0 ? "selected" : "" %>>
                                -- Là Menu Gốc (Cấp 1) --
                            </option>
                            <% 
                            if (parentList != null) { 
                                for (Menu p : parentList) { 
                                    // Không hiển thị chính nó trong danh sách cha để tránh vòng lặp vô tận
                                    if(p.getLevels() == 1 && p.getMenuID() != item.getMenuID()) { 
                            %>
                                    <option value="<%= p.getMenuID() %>" 
                                        <%= p.getMenuID() == item.getParentID() ? "selected" : "" %>>
                                        <%= p.getMenuName() %>
                                    </option>
                            <% 
                                    }
                                } 
                            } 
                            %>
                        </select>
                    </div>

                    <div class="col-md-2 mb-3">
                        <label class="form-label fw-bold">Cấp độ</label>
                        <input type="number" name="Levels" id="levels" class="form-control" 
                               value="<%=item.getLevels()%>" readonly>
                    </div>

                    <div class="col-md-2 mb-3">
                        <label class="form-label fw-bold">Thứ tự</label>
                        <input type="number" name="MenuOrder" class="form-control" 
                               value="<%=item.getMenuOrder()%>" required>
                    </div>
                    
                    <div class="col-md-2 mb-3">
                        <label class="form-label fw-bold">Vị trí</label>
                        <input type="number" name="Position" class="form-control" 
                               value="<%=item.getPosition()%>">
                    </div>

                <div class="form-check mb-4">
                    <input type="checkbox" name="IsActive" class="form-check-input" id="isActive"
                           <%= item.isActive() ? "checked" : "" %>>
                    <label class="form-check-label" for="isActive">Hiển thị</label>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary px-4">
                        <i class="bi bi-pencil-square me-1"></i> Cập nhật
                    </button>
                    <a href="<%=request.getContextPath()%>/admin/Menu/Index" class="btn btn-secondary px-4">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
    const parentSelect = document.getElementById("parentID");
    const levelsInput = document.getElementById("levels");

    // Logic tự động tính toán Levels giống như calculateCohortNumber trong QLKhoaHoc
    function updateLevels() {
        if (parentSelect.value == "0") {
            levelsInput.value = 1; 
        } else {
            levelsInput.value = 2; 
        }
    }

    parentSelect.addEventListener("change", updateLevels);
    
    // Đảm bảo cập nhật đúng levels khi trang vừa load xong
    window.addEventListener("DOMContentLoaded", updateLevels);
</script>