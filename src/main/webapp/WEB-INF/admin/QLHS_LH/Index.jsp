<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLHS_LH"%>
<%@ page import="model.bean.QLHocSinh"%>
<%@ page import="model.bean.QLLopHoc"%>
<%@ page import="model.bean.QLHocKy"%>
<%@ page import="model.bean.QLKhoaHoc"%>


<%
// Ép kiểu danh sách từ request scope
List<QLHS_LH> hsLhList = (List<QLHS_LH>) request.getAttribute("hs_lhList");
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>📋 Danh sách Học sinh - Lớp học</h2>
    </div>

    <section class="section dashboard">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm border-0">
                    
                    <div
						class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
						<h5 class="mb-0">Quản lý Học sinh - Lớp học</h5>
						<div>
							<a href="<%=request.getContextPath()%>/admin/QLHS_LH/Create"
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
                            class="table table-hover table-bordered align-middle datatable bg-white">
                            <thead class="table-light">
                                <tr class="text-center">
                                    <th style="width: 5%;" class="text-center">Stt</th>
                                    <th style="width: 15%;" class="text-center">Mã học sinh</th>
                                    <th style="width: 15%;" class="text-center">Học sinh</th>
                                    <th style="width: 15%;" class="text-center">Lớp học</th>
                                    <th style="width: 15%;" class="text-center">Học kỳ</th>
                                    <th style="width: 15%;" class="text-center">Khóa học</th>
                                    <th style="width: 10%;" class="text-center">Trạng thái</th>
                                    <th style="width: 25%;" class="text-center">Hành động</th>
                                </tr>
                            </thead>

                            <tbody>
                                <%
                                if (hsLhList != null && !hsLhList.isEmpty()) {
                                    for (QLHS_LH item : hsLhList) {
                                        // Giả định các đối tượng liên quan đã được load sẵn trong item từ Controller
                                        QLHocSinh hocSinh = item.getHocsinh();
                                        QLLopHoc lopHoc = item.getLopHoc();
                                        QLHocKy hocKy = item.getHocKy();
                                        QLKhoaHoc khoaHoc = item.getKhoaHoc();
                                %>

                                <tr class="text-center">
                                    <td><%=stt++%></td>
                                    <td><%=hocSinh != null ? hocSinh.getStudentID() : item.getStudentID()%></td>
                                    <td class="text-center">
                                        <%
                                        if (hocSinh != null) {
                                        %>
                                        <a href="<%=request.getContextPath()%>/admin/QLHocSinh/Details?id=<%=hocSinh.getID()%>">
                                            <%=hocSinh.getFullName()%>
                                        </a>
                                        <%
                                        } else {
                                        %>
                                            <%=item.getStudentID()%>
                                        <%
                                        }
                                        %>
                                    </td>
                                    
                                    <td><%=lopHoc != null ? lopHoc.getClassName() : item.getClassID()%></td>
                                    
                                    <td><%=hocKy != null ? hocKy.getSemesterName() : (item.getSemesterID() != null ? item.getSemesterID() : "N/A")%></td>
                                    
                                    <td><%=khoaHoc != null ? khoaHoc.getCohort() : (item.getCourseID() != null ? item.getCourseID() : "N/A")%></td>


                                    <td>
                                        <form
                                            action="<%=request.getContextPath()%>/admin/QLHS_LH/ToggleStatus"
                                            method="post" style="margin: 0;">
                                            <input type="hidden" name="id" value="<%=item.getHocSinhLopHocID()%>" /> 
                                            <input
                                                type="checkbox" name="IsActive" class="toggle-status-switch"
                                                <%=item.getIsActive() ? "checked" : ""%>
                                                onchange="this.form.submit()" />
                                        </form>
                                    </td>

                                    <td>
                                        <a
                                        href="<%=request.getContextPath()%>/admin/QLHS_LH/Edit?id=<%=item.getHocSinhLopHocID()%>"
                                        class="btn btn-primary btn-sm me-1" title="Chỉnh sửa">
                                            <i class="bi bi-pencil"></i>
                                        </a> 
                                        <a
                                        href="<%=request.getContextPath()%>/admin/QLHS_LH/Delete?id=<%=item.getHocSinhLopHocID()%>"
                                        class="btn btn-danger btn-sm btn-delete"
                                        onclick="return confirm('Bạn có chắc chắn muốn xóa mối liên kết này không?');"
                                        title="Xóa">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>

                                <%
                                    }
                                } else {
                                %>

                                <tr>
                                    <td colspan="7" class="text-center text-muted py-3">
                                        <i class="bi bi-exclamation-circle me-1"></i> Không có dữ liệu Học sinh - Lớp học.
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