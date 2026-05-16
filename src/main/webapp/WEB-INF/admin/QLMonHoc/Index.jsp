<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLMonHoc"%>

<%
    // Dữ liệu từ Servlet được đặt tên là "list"
    List<QLMonHoc> list = (List<QLMonHoc>) request.getAttribute("list"); 
    int stt = 1;
%>

<main id="main" class="main">
    <div class="pagetitle d-flex justify-content-between align-items-center">
        <h2>Danh sách Môn Học</h2>
        <a href="<%= request.getContextPath() %>/admin/QLMonHoc/Create" class="btn btn-success">Thêm Môn Học</a>
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
                                    <th class="text-center">Tên Môn Học</th>
                                    <th class="text-center">Số Tiết</th>
                                    <th class="text-center">Học Kỳ</th>
                                    <th class="text-center">Hiển thị</th>
                                    <th class="text-center">Chức năng</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (list != null && !list.isEmpty()) { 
                                    for (QLMonHoc item : list) { %>
                                    <tr>
                                        <td class="text-center"><%= stt %></td>
                                        <td class="text-center text-primary">
                                            <%-- Giả định có trang Details cho môn học --%>
                                            <a href="<%= request.getContextPath() %>/admin/QLMonHoc/Details?id=<%= item.getSubjectID() %>">
                                                <%= item.getSubjectName() %>
                                            </a>
                                        </td>
                                        <td class="text-center"><%= item.getNumberOfLesson() %></td>
                                        <td class="text-center"><%= item.getSemester() %></td>
                                        
                                        <%-- Form Toggle Status --%>
                                        <td class="text-center">
                                            <form action="<%= request.getContextPath() %>/admin/QLMonHoc/ToggleStatus" method="post">
                                                <input type="hidden" name="id" value="<%= item.getSubjectID() %>" />
                                                <input type="checkbox" name="IsActive" 
                                                       <%= item.isActive() ? "checked" : "" %> 
                                                       onchange="this.form.submit()" />
                                            </form>
                                        </td>
                                        
                                        <%-- Chức năng Sửa/Xóa --%>
                                        <td class="text-center">
                                            <a href="<%= request.getContextPath() %>/admin/QLMonHoc/Edit?id=<%= item.getSubjectID() %>" 
                                               class="btn btn-primary btn-sm" title="Sửa">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="<%= request.getContextPath() %>/admin/QLMonHoc/Delete?id=<%= item.getSubjectID() %>" 
                                               class="btn btn-danger btn-sm" title="Xóa">
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