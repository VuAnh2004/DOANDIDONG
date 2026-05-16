<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLHocSinh"%>


<%
List<QLHocSinh> hocSinhList = (List<QLHocSinh>) request.getAttribute("list");
%>

<main id="main" class="main">
    <div class="pagetitle mb-4">
        <h2>📋 Danh sách Học Sinh</h2>
    </div>

    <section class="section dashboard">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm border-0">
                    
                    <div
						class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
						<h5 class="mb-0">Quản lý Học sinh</h5>
						<div>
							<a href="<%=request.getContextPath()%>/admin/QLHocSinh/Create"
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
                                    <th class="text-center">Mã HS</th>
                                    <th class="text-center">Họ và tên</th>
                                    <th class="text-center">Ngày sinh</th>
                                    <th class="text-center">Giới tính</th>
                                    <th class="text-center">Địa chỉ</th>
                                    <th class="text-center">Dân tộc</th>
                                    <th class="text-center">Tôn giáo</th>
                                    <th class="text-center">Tình trạng</th>
                                    <th class="text-center">SĐT</th>
                                    <th class="text-center">Hình ảnh</th>
                                    <th class="text-center">Hiển thị</th>
                                    <th class="text-center">Chức năng</th>
                                </tr>
                            </thead>

                            <tbody>
                                <%
                                if (hocSinhList != null && !hocSinhList.isEmpty()) {
                                    for (QLHocSinh hs : hocSinhList) {
                                %>

                                <tr class="text-center">
                                    <td><%=stt++%></td>
                                    <td><%=hs.getStudentID()%></td>

                                    <td class="text-start">
                                        <a href="<%=request.getContextPath()%>/admin/QLHocSinh/Details?id=<%=hs.getID()%>">
                                            <%=hs.getFullName()%>
                                        </a>
                                    </td>

                                    <td><%=hs.getBirth() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(hs.getBirth()) : ""%></td>
                                    <td><%=hs.getGender()%></td>
                                    <td class="text-start"><%=hs.getAddress()%></td>
                                    <td><%=hs.getNation()%></td>
                                    <td><%=hs.getReligion()%></td>
                                    <td><%=hs.getStatusStudent()%></td>
                                    <td><%=hs.getNumberPhone()%></td>

                                    <td>
                                        <%
                                        if (hs.getImages() != null && !hs.getImages().isEmpty()) {
                                            // Giả sử đường dẫn trong DB là "uploads/..."
                                            String imgPath = request.getContextPath() + "/" + hs.getImages();
                                        %> 
                                        <img src="<%=imgPath%>" width="50" height="50"
                                        style="object-fit: cover; border-radius: 6px;" alt="Hình ảnh học sinh"/> 
                                        <%
                                         } else {
                                         %> 
                                         <span class="text-secondary">Không ảnh</span> 
                                         <%
                                         }
                                         %>
                                    </td>


                                    <td>
                                        <form
                                            action="<%=request.getContextPath()%>/admin/QLHocSinh/ToggleStatus"
                                            method="post" style="margin: 0;">
                                            <input type="hidden" name="id" value="<%=hs.getID()%>" /> 
                                            <input
                                                type="checkbox" name="IsActive" class="toggle-status-switch"
                                                <%=hs.isIsActive() ? "checked" : ""%>
                                                onchange="this.form.submit()" />
                                        </form>
                                    </td>

                                    <td>
                                        <a
                                            href="<%=request.getContextPath()%>/admin/QLHocSinh/Edit?id=<%=hs.getID()%>"
                                            class="btn btn-primary btn-sm me-1" title="Chỉnh sửa">
                                            <i class="bi bi-pencil"></i>
                                        </a> 
                                        <a
                                            href="<%=request.getContextPath()%>/admin/QLHocSinh/Delete?id=<%=hs.getID()%>"
                                            class="btn btn-danger btn-sm btn-delete"
                                            onclick="return confirm('Bạn có chắc chắn muốn xóa học sinh này không?');"
                                            title="Xóa học sinh">
                                            <i class="bi bi-trash"></i>
                                        </a>
                                    </td>
                                </tr>

                                <%
                                }
                                } else {
                                %>

                                <tr>
                                    <td colspan="13" class="text-center text-muted py-3">
                                        <i class="bi bi-exclamation-circle me-1"></i> Không có dữ liệu học sinh.
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