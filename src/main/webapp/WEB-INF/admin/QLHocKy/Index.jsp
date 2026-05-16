<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLHocKy"%>

<%
    List<QLHocKy> list = (List<QLHocKy>) request.getAttribute("list"); // dữ liệu từ Servlet
    // Không cần khai báo int stt = 1; ở đây nếu dùng Datatables, nhưng tôi sẽ giữ lại cho logic JSP
    int stt = 1; 
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>📋 Danh Sách Học Kỳ</h2>
    </div>

    <section class="section dashboard">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm border-0">
                    
                    <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Quản lý Học Kỳ</h5>
                        <div>
                            <a href="<%= request.getContextPath() %>/admin/QLHocKy/Create" class="btn btn-light btn-sm me-2"> 
                                <i class="bi bi-plus-circle me-1"></i> Thêm mới
                            </a>
                        </div>
                    </div>

                    <div class="card-body">
                        
                        <table class="table table-hover table-bordered align-middle datatable bg-white">
                            <thead class="table-light">
                                <tr class="text-center">
                                    <th class="text-center">STT</th>
                                    <th class="text-center">Tên Học Kỳ</th>
                                    <th class="text-center">Mã Học Kỳ</th>
                                    <th class="text-center">Hiển thị</th>
                                    <th class="text-center">Chức năng</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (list != null && !list.isEmpty()) { 
                                    for (QLHocKy item : list) { %>
                                    <tr class="text-center">
                                        <td><%= stt %></td>
                                        <td class="text-primary">
                                            <a href="<%= request.getContextPath() %>/admin/QLHocKy/Details?id=<%= item.getSemesterId() %>">
                                                <%= item.getSemesterName() %>
                                            </a>
                                        </td>
                                        <td><%= item.getSemesterCode() %></td>
                                        <td>
                                            <form action="<%= request.getContextPath() %>/admin/QLHocKy/ToggleStatus" method="post">
                                                <input type="hidden" name="id" value="<%= item.getSemesterId() %>" />
                                                <input type="checkbox" name="IsActive" 
                                                       <%= item.isActive() ? "checked" : "" %> 
                                                       onchange="this.form.submit()" />
                                            </form>
                                        </td>
                                        <td>
                                            <a href="<%= request.getContextPath() %>/admin/QLHocKy/Edit?id=<%= item.getSemesterId() %>" 
                                               class="btn btn-primary btn-sm me-1" title="Chỉnh sửa">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="<%= request.getContextPath() %>/admin/QLHocKy/Delete?id=<%= item.getSemesterId() %>" 
                                               class="btn btn-danger btn-sm"
                                               onclick="return confirm('Bạn có chắc chắn muốn xóa Học Kỳ này không?');"
                                               title="Xóa Học Kỳ">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>
                                <% stt++; } 
                                } else { %>
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-3">
                                            <i class="bi bi-exclamation-circle me-1"></i> Chưa có dữ liệu Học Kỳ
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div> </div>
            </div>
        </div>
    </section>
</main>