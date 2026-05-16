<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLGiaoVien"%>

<%
List<QLGiaoVien> giaoVienList = (List<QLGiaoVien>) request.getAttribute("list");
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>📋 Danh sách Giáo Viên</h2>
    </div>

    <section class="section dashboard">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm border-0">
                    
                    <div
                        class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Quản lý Giáo viên</h5>
                        <div>
                            <a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Create"
                               class="btn btn-light btn-sm me-2"> 
                                <i class="bi bi-plus-circle me-1"></i> Thêm mới
                            </a>
                        </div>
                    </div>

                    <div class="card-body">
                        <%
                        int stt = 1;
                        %>
                        <table
                            class="table table-hover table-bordered align-middle datatable bg-white" style="font-size: 0.9em;">
                            <thead class="table-light">
                                <tr class="text-center">
                                    <th class="text-center">Stt</th>
                                    <th class="text-center">Mã GV</th>
                                    <th class="text-center">Họ và tên</th>
                                    <th class="text-center">Giới tính</th>
                                    <th class="text-center">Ngày sinh</th>
                                    <th class="text-center">Số điện thoại</th>
                                    <th class="text-center">Môn dạy</th>
                                    <th class="text-center">Ảnh</th>
                                    <th class="text-center">Hiển thị</th>
                                    <th class="text-center">Chức năng</th>
                                </tr>
                            </thead>

                            <tbody>
                            <%
                            if (giaoVienList != null && !giaoVienList.isEmpty()) {
                                for (QLGiaoVien gv : giaoVienList) {
                            %>
                                <tr class="text-center">
                                    <td><%=stt++%></td>
                                    <td><%=gv.getTeacherID()%></td>
                                    <td class="text-start">
                                        <a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Details?id=<%=gv.getID()%>">
                                            <%=gv.getFullName()%>
                                        </a>
                                    </td>
                                    <td><%=gv.getGender()%></td>
                                    <td><%=gv.getBirth() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(gv.getBirth()) : ""%></td>
                                    <td><%=gv.getNumberPhone()%></td>
                                    
                                    <td>
                                        <%
                                        // Lặp qua List<String> subjectNames đã được DAO chuẩn bị
                                        List<String> subjectNames = gv.getSubjectNames();
                                        if (subjectNames != null && !subjectNames.isEmpty()) {
                                            for (String name : subjectNames) { %>
                                                <%=name%><br/>
                                        <%
                                            }
                                        } else {
                                        %>
                                            <span class="text-secondary">Chưa có môn</span>
                                        <%
                                        }
                                        %>
                                    </td>
                                    <td>
                                        <%
                                        if (gv.getImages() != null && !gv.getImages().isEmpty()) {
                                            String imgPath = request.getContextPath() + "/" + gv.getImages();
                                        %>
                                            <img src="<%=imgPath%>" width="50" height="50" style="object-fit: cover; border-radius: 6px;" alt="Ảnh giáo viên"/>
                                        <%
                                        } else {
                                        %>
                                            <span class="text-secondary">Không ảnh</span>
                                        <%
                                        }
                                        %>
                                    </td>

                                    <td>
                                        <form action="<%=request.getContextPath()%>/admin/QLGiaoVien/ToggleStatus" method="post" style="margin: 0;">
                                            <input type="hidden" name="id" value="<%=gv.getID()%>" />
                                            <input type="checkbox" name="IsActive" class="toggle-status-switch"
                                                   <%=gv.isIsActive() ? "checked" : ""%>
                                                   onchange="this.form.submit()" />
                                        </form>
                                    </td>

                                    <td>
                                        <a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Edit?id=<%=gv.getID()%>" 
                                            class="btn btn-primary btn-sm me-1" title="Chỉnh sửa">
                                            <i class="bi bi-pencil"></i>
                                        </a>
                                        <a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Delete?id=<%=gv.getID()%>" 
                                            class="btn btn-danger btn-sm btn-delete" 
                                            onclick="return confirm('Bạn có chắc chắn muốn xóa giáo viên này không?');"
                                            title="Xóa giáo viên">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>
                            <%
                                }
                            } else {
                            %>
                                <tr>
                                    <td colspan="10" class="text-center text-muted py-3">
                                        <i class="bi bi-exclamation-circle me-1"></i> Không có dữ liệu giáo viên.
                                    </td>
                                </tr>
                            <%
                            }
                            %>
                            </tbody>
                        </table>
                    </div> </div>
            </div>
        </div>
    </section>
</main>