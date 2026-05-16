<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="model.bean.QLKhoaHoc"%>
<%@ page import="java.util.List"%>

<main id="main" class="main">
	<div class="pagetitle">
		<h2>Thêm lớp học</h2>
	</div>
	<div class="container shadow p-5">

		<%-- Hiển thị lỗi nếu có --%>
		<%
		List<String> errors = (List<String>) request.getAttribute("errors");
		if (errors != null && !errors.isEmpty()) {
		%>
		<div class="alert alert-danger">
			<ul>
				<%
				for (String err : errors) {
				%>
				<li><%=err%></li>
				<%
				}
				%>
			</ul>
		</div>
		<%
		}
		%>

		<form method="post"
			action="<%=request.getContextPath()%>/admin/QLLopHoc/Create">

			<div class="col-md-6 mb-3">
				<label>Tên lớp</label> <input type="text" name="ClassName"
					class="form-control"
					value="<%=request.getAttribute("ClassName") != null ? request.getAttribute("ClassName") : ""%>"
					placeholder="Tên lớp..." required />
			</div>

			<div class="col-md-6 mb-3">
				<label>Số lượng tối đa</label> <input type="number"
					name="MaxStudents" class="form-control"
					value="<%=request.getAttribute("MaxStudents") != null ? request.getAttribute("MaxStudents") : ""%>"
					placeholder="Số lượng tối đa..." required />
			</div>

			<div class="col-md-6 mb-3">
				<label>Khóa học</label> <select name="CourseID" class="form-control">
					<option value="">--- Chọn Khóa ---</option>
					<%
					List<QLKhoaHoc> khoaHocList = (List<QLKhoaHoc>) request.getAttribute("KhoaHocList");
					String selectedCourse = request.getAttribute("CourseID") != null ? request.getAttribute("CourseID").toString() : "";
					if (khoaHocList != null) {
						for (QLKhoaHoc k : khoaHocList) {
							Integer courseId = k.getCourseID();
							String courseIdStr = (courseId != null) ? courseId.toString() : "";
							String selected = courseIdStr.equals(selectedCourse) ? "selected" : "";
					%>
					<option value="<%=courseIdStr%>" <%=selected%>>
						<%=k.getStartYear()%>-<%=k.getEndYear()%> -
						<%=k.getCohort()%>
					</option>
					<%
					}
					}
					%>
				</select>
			</div>



			<div class="col-md-6 mb-3 form-check">
				<%
				boolean isActive = true;
				if (request.getAttribute("IsActive") != null) {
					isActive = Boolean.parseBoolean(request.getAttribute("IsActive").toString());
				}
				%>
				<input type="checkbox" name="IsActive" class="form-check-input"
					<%=isActive ? "checked" : ""%> /> <label class="form-check-label">Hiển
					thị</label>
			</div>

			<button type="submit" class="btn btn-primary">Lưu thông tin</button>
			<a href="<%=request.getContextPath()%>/admin/QLLopHoc/Index"
				class="btn btn-secondary">Hủy</a>
		</form>
	</div>
</main>
