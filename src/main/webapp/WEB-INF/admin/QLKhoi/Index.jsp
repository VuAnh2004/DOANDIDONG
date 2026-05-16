<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLKhoi"%>

<%
    List<QLKhoi> list = (List<QLKhoi>) request.getAttribute("list"); // dữ liệu từ Servlet
    int stt = 1;
%>

<main id="main" class="main">
    <div class="pagetitle d-flex justify-content-between align-items-center">
        <h2>Danh sách Khối</h2>
        <a href="<%= request.getContextPath() %>/admin/QLKhoi/Create" class="btn btn-success">Thêm Khối</a>
    </div>

    <section class="section dashboard mt-3">
        <div class="row">
            <div class="col-12">
                <div class="card recent-sales overflow-auto">
                    <div class="card-body mt-3">
                        <table class="table table-striped table-bordered">
                            <thead class="table-dark">
                                <tr>
                                    <th class="text-center">STT</th>
                                    <th class="text-center">Tên Khối</th>
                                    <th class="text-center">Mô tả</th>
                                    <th class="text-center">Hiển thị</th>
                                    <th class="text-center">Chức năng</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (list != null && !list.isEmpty()) { 
                                    for (QLKhoi item : list) { %>
                                    <tr>
                                        <td class="text-center"><%= stt %></td>
                                        <td class="text-center text-primary">
                                            <a href="<%= request.getContextPath() %>/admin/QLKhoi/Details?id=<%= item.getGradeLevelId() %>">
                                                <%= item.getGradeName() %>
                                            </a>
                                        </td>
                                        <td class="text-center"><%= item.getDescription() %></td>
                                        <td class="text-center">
                                            <form action="<%= request.getContextPath() %>/admin/QLKhoi/ToggleStatus" method="post">
                                                <input type="hidden" name="id" value="<%= item.getGradeLevelId() %>" />
                                                <input type="checkbox" name="IsActive" 
                                                       <%= item.isActive() ? "checked" : "" %> 
                                                       onchange="this.form.submit()" />
                                            </form>
                                        </td>
                                        <td class="text-center">
                                            <a href="<%= request.getContextPath() %>/admin/QLKhoi/Edit?id=<%= item.getGradeLevelId() %>" 
                                               class="btn btn-primary btn-sm" title="Sửa">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="<%= request.getContextPath() %>/admin/QLKhoi/Delete?id=<%= item.getGradeLevelId() %>" 
                                               class="btn btn-danger btn-sm" title="Xóa"
                                            ">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>
                                <% stt++; } 
                                } else { %>
                                    <tr>
                                        <td colspan="5" class="text-center">Chưa có dữ liệu</td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </section>
</main>

