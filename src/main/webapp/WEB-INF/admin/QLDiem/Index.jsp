<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLDiem"%>



<%
List<QLDiem> diemList = (List<QLDiem>) request.getAttribute("diemList");

%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>📋 Danh Sách Điểm Học Sinh</h2>
    </div>

    <section class="section dashboard">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm border-0">
                    
                    <div
						class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
						<h5 class="mb-0">Quản lý Điểm Học Sinh</h5>
						<div>
							<a href="<%=request.getContextPath()%>/admin/QLDiem/Create"
								class="btn btn-light btn-sm me-2"> 
                                <i class="bi bi-plus-circle me-1"></i> Thêm mới
							</a>
						</div>
					</div>

                    <div class="card-body">
                        
                        <table
                            class="table table-hover table-bordered align-middle datatable bg-white">
                            <thead class="table-light">
                                <tr class="text-center">
                                    <th class="text-center">STT</th>
                                    <th class="text-center">Mã học sinh</th>
                                    <th class="text-center">Họ và tên</th>
                                    <th class="text-center">Môn học</th>
                                    <th class="text-center">Học kỳ</th>
                                    <th class="text-center">Lớp học-Khóa học</th>
                                    <th class="text-center">Điểm TB</th>
                                    <th class="text-center">Lý do sửa điểm(nếu có)</th>
                                    <th class="text-center">Trạng thái</th>
                                    <th class="text-center">Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                int stt = 1;
                                if (diemList != null && !diemList.isEmpty()) {
                                    for (QLDiem d : diemList) {
                                %>
                                <tr class="text-center">
                                    <td><%= stt++ %></td>
                                    <td><%= d.getStudentID() != null ? d.getStudentID() : "-" %></td>
                                    <td><%= d.getHocsinh() != null ? d.getHocsinh().getFullName() : "-" %></td>
                                    <td><%= d.getMonHoc() != null ? d.getMonHoc().getSubjectName() : "-" %></td>
                                    <td>
<%= d.getHocKy() != null
    ? d.getHocKy().getSemesterName() + " (" + d.getHocKy().getSemesterCode() + ")"
    : "-" %>
</td>

                                    <td class="text-center">
                                        <%= d.getLopHoc() != null ? d.getLopHoc().getClassName() : "-" %>
                                        <%= d.getKhoaHoc() != null 
                                            ? " - K" + d.getKhoaHoc().getCohort() 
                                            + " (" + d.getKhoaHoc().getStartYear() 
                                            + "-" + d.getKhoaHoc().getEndYear() + ")" 
                                            : "" %>
                                    </td>
                                    <td><%= d.getAverageScore() != null ? d.getAverageScore() : "-" %></td>
                                    <td> <%= d.getNotes() != null ? d.getNotes() : "" %> </td>
                                    
                                    <td class="text-center">
                                        <form action="<%=request.getContextPath()%>/admin/QLDiem/ToggleStatus" method="post" style="margin:0;">
                                            <input type="hidden" name="gradeID" value="<%=d.getGradeID()%>" />
                                            <input type="checkbox" class="toggle-status-switch"
                                                <%= d.isActive() ? "checked" : "" %> onchange="this.form.submit()" />
                                        </form>
                                    </td>
                                    
                                    <td>
                                        <a href="<%=request.getContextPath()%>/admin/QLDiem/Edit?gradeID=<%=d.getGradeID()%>" class="btn btn-primary btn-sm me-1" title="Sửa điểm">
                                            <i class="bi bi-pencil"></i>
                                        </a>
                                        <a href="<%=request.getContextPath()%>/admin/QLDiem/Delete?gradeID=<%=d.getGradeID()%>" class="btn btn-danger btn-sm btn-delete" 
                                            onclick="return confirm('Bạn có chắc chắn muốn xóa điểm này không?');" title="Xóa điểm">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>
                                <%
                                    }
                                } else {
                                %>
                                <tr>
                                    <td colspan="9" class="text-center text-muted py-3">
                                        <i class="bi bi-exclamation-circle me-1"></i> Không có dữ liệu Điểm.
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