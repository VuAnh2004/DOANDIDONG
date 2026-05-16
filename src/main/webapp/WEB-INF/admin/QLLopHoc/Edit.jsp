<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="model.bean.QLLopHoc"%>
<%@ page import="model.bean.QLKhoi"%>
<%@ page import="model.bean.QLKhoaHoc"%>
<%@ page import="java.util.List"%>

<%
QLLopHoc lop = (QLLopHoc) request.getAttribute("lophoc");
List<QLKhoi> khoiList = (List<QLKhoi>) request.getAttribute("khoiList");
List<QLKhoaHoc> khoaHocList = (List<QLKhoaHoc>) request.getAttribute("KhoaHocList");
%>

<main id="main" class="main">
	<div class="pagetitle">
		<h2>Sửa thông tin lớp học</h2>
	</div>

	<div class="container shadow p-5">
		<%
		if (request.getAttribute("errorMessage") != null) {
		%>
		<div class="alert alert-danger">
			<%=request.getAttribute("errorMessage")%>
		</div>
		<%
		}
		%>

		<form action="<%=request.getContextPath()%>/admin/QLLopHoc/Edit"
			method="post">
			<input type="hidden" name="ClassID" value="<%=lop.getClassID()%>" />

			<div class="row">
				<div class="col-md-6 mb-3">
					<label>Tên lớp <span class="text-danger">*</span></label> <input
						type="text" class="form-control" name="ClassName"
						value="<%=lop.getClassName() != null ? lop.getClassName() : ""%>"
						placeholder="Tên lớp..." required />
				</div>

				<div class="col-md-6 mb-3">
					<label>Mã khối <span class="text-danger">*</span></label> <select
						name="GradeLevelId" class="form-control" required>
						<option value="">--- Chọn Khối ---</option>
						<%
						if (khoiList != null && !khoiList.isEmpty()) {
							Integer selectedGradeId = (lop != null) ? lop.getGradeLevelId() : null;
							for (QLKhoi k : khoiList) {
								boolean selected = selectedGradeId != null && selectedGradeId.equals(k.getGradeLevelId());
						%>
						<option value="<%=k.getGradeLevelId()%>"
							<%=selected ? "selected" : ""%>>
							<%=k.getGradeName()%>
						</option>
						<%
						}
						}
						%>
					</select>
				</div>



				<div class="col-md-6 mb-3">
					<label>Số lượng hiện tại</label> <input type="number"
						class="form-control" name="MaxStudents"
						value="<%=lop.getMaxStudents() %>" />
				</div>

				<div class="col-md-6 mb-3">
					<label>Khóa học <span class="text-danger">*</span></label> <select
						name="CourseID" class="form-control" required>
						<option value="">--- Chọn Khóa ---</option>
						<%
						if (khoaHocList != null) {
							for (QLKhoaHoc kh : khoaHocList) {
								boolean selected = lop.getCourseID() != null && lop.getCourseID().equals(kh.getCourseID());
						%>
						<option value="<%=kh.getCourseID()%>"
							<%=selected ? "selected" : ""%>>
							<%=kh.getStartYear() + "-" + kh.getEndYear() + " - Khóa " + kh.getCohort()%>
						</option>
						<%
						}
						}
						%>
					</select>
				</div>

				<div class="col-md-6 mb-3">
					<label>Năm học</label> <input type="text" class="form-control"
						name="SchoolYear" readonly
						value="<%=lop.getSchoolYear() != null ? lop.getSchoolYear() : ""%>" />
				</div>

				<div class="col-md-6 mb-3">
					<label> <input type="checkbox" name="IsActive"
						<%=lop.isActive() ? "checked" : ""%> /> Hiển thị
					</label>
				</div>
			</div>

			<a href="<%=request.getContextPath()%>/admin/QLLopHoc/Index"
				class="btn btn-warning"> <i class="bi bi-arrow-left-circle"></i>
				Quay lại
			</a>
			<button type="submit" class="btn btn-primary">
				<i class="bi bi-file-plus-fill"></i> Lưu thông tin
			</button>
		</form>
	</div>
</main>
