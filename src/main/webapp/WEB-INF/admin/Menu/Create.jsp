<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.Menu"%>

<%
    // Nhận danh sách từ Controller truyền qua
    List<Menu> parentList = (List<Menu>) request.getAttribute("listMenu");
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>Thêm Menu mới</h2>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-4">
            <form action="<%=request.getContextPath()%>/admin/Menu/Create" method="post">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên Menu</label>
                        <input type="text" name="MenuName" class="form-control" required>
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Biểu tượng</label>
                        <input type="text" name="Icon" class="form-control" placeholder="">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên bộ điều khiển</label>
                        <input type="text" name="ControllerName" class="form-control">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên hành động</label>
                        <input type="text" name="ActionName" class="form-control">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Thuộc Menu Cha</label>
                        <select name="ParentID" id="parentID" class="form-select">
                            <option value="0">-- Là Menu Gốc (Cấp 1) --</option>
                            <% 
                            if (parentList != null) { 
                                for (Menu p : parentList) { 
                                    // Chỉ cho phép chọn menu Cấp 1 làm cha
                                    if(p.getLevels() == 1) { 
                            %>
                                    <option value="<%= p.getMenuID() %>"><%= p.getMenuName() %></option>
                            <% 
                                    }
                                } 
                            } 
                            %>
                        </select>
                    </div>

                    <div class="col-md-2 mb-3">
                        <label class="form-label fw-bold">Cấp độ</label>
                        <input type="number" name="Levels" id="levels" class="form-control" value="1" readonly>
                    </div>

                    <div class="col-md-2 mb-3">
                        <label class="form-label fw-bold">Thứ tự</label>
                        <input type="number" name="MenuOrder" class="form-control" value="1" required>
                    </div>
                    
                    <div class="col-md-2 mb-3">
                        <label class="form-label fw-bold">Vị trí</label>
                        <input type="number" name="Position" class="form-control" value="1">
                    </div>
                </div>


                <div class="form-check mb-4">
                    <input type="checkbox" name="IsActive" class="form-check-input" id="isActive" checked>
                    <label class="form-check-label" for="isActive">Hiển thị</label>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-success px-4">
                        <i class="bi bi-save me-1"></i> Lưu thông tin
                    </button>
                    <a href="<%=request.getContextPath()%>/admin/Menu/Index" class="btn btn-secondary px-4">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
    // JS tự động cập nhật Cấp độ khi người dùng chọn menu cha
    const parentSelect = document.getElementById("parentID");
    const levelsInput = document.getElementById("levels");

    parentSelect.addEventListener("change", function() {
        if (this.value == "0") {
            levelsInput.value = 1; 
        } else {
            levelsInput.value = 2; 
        }
    });
</script>