<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLKhoaHoc"%>

<%
    List<QLKhoaHoc> list = (List<QLKhoaHoc>) request.getAttribute("list"); 
    int stt = 1;
%>

<main id="main" class="main">

    <div class="pagetitle d-flex justify-content-between align-items-center">
        <h2>Danh sách Khóa Học</h2>
        <a href="<%= request.getContextPath() %>/admin/QLKhoaHoc/Create" class="btn btn-success">Thêm Khóa Học</a>
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
                                    <th class="text-center">Năm bắt đầu</th>
                                    <th class="text-center">Năm kết thúc</th>
                                    <th class="text-center">Khóa </th>
                                    <th class="text-center">Hiển thị</th>
                                    <th class="text-center">Chức năng</th>
                                </tr>
                            </thead>

                            <tbody>
                                <% if (list != null && !list.isEmpty()) {
                                       for (QLKhoaHoc item : list) { %>

                                    <tr>
                                        <td class="text-center"><%= stt %></td>

                                        <td class="text-center text-primary">
                                            <a href="<%= request.getContextPath() %>/admin/QLKhoaHoc/Details?id=<%= item.getCourseID() %>">
                                                <%= item.getStartYear() %>
                                            </a>
                                        </td>

                                        <td class="text-center"><%= item.getEndYear() %></td>

                                        <td class="text-center"><%= item.getCohort() %></td>

                                        <td class="text-center">
                                            <form action="<%= request.getContextPath() %>/admin/QLKhoaHoc/ToggleStatus" method="post">
                                                <input type="hidden" name="id" value="<%= item.getCourseID() %>" />
                                                <input type="checkbox" name="IsActive"
                                                    <%= item.isActive() ? "checked" : "" %>
                                                    onchange="this.form.submit()" />
                                            </form>
                                        </td>

                                        <td class="text-center">
                                            <a href="<%= request.getContextPath() %>/admin/QLKhoaHoc/Edit?id=<%= item.getCourseID() %>"
                                               class="btn btn-primary btn-sm" title="Sửa">
                                                <i class="bi bi-pencil"></i>
                                            </a>

                                            <a href="<%= request.getContextPath() %>/admin/QLKhoaHoc/Delete?id=<%= item.getCourseID() %>"
                                               class="btn btn-danger btn-sm" title="Xóa"
                                               onclick="return confirm('Bạn có chắc muốn xóa?');">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>

                                <% stt++; } 
                                   } else { %>

                                    <tr>
                                        <td colspan="6" class="text-center">Chưa có dữ liệu</td>
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
