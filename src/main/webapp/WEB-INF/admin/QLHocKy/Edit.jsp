<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLHocKy"%>

<%
QLHocKy q = (QLHocKy) request.getAttribute("q");
if (q == null) {
	response.sendRedirect(request.getContextPath() + "/admin/QLHocKy/Index");
	return;
}
%>

<main id="main" class="main">
	<div
		class="pagetitle d-flex justify-content-between align-items-center">
		<h2>Sửa Học Kỳ</h2>

	</div>


	<form action="<%=request.getContextPath()%>/admin/QLHocKy/Edit"
		method="post">
		<!-- Hidden field để controller nhận ID -->
		<input type="hidden" name="SemesterID"
			value="<%=q.getSemesterId()%>">

		<div class="mb-3">
			<label class="form-label">Tên Học Kỳ</label> <input type="text"
				name="SemesterName" class="form-control"
				value="<%=q.getSemesterName()%>" required>
		</div>

		<div class="mb-3">
			<label class="form-label">Năm bắt đầu - kết thúc</label> <input
				type="text" name="SemesterCode" class="form-control"
				value="<%=q.getSemesterCode()%>" required>
		</div>

		<div class="form-check mb-3">
			<input type="checkbox" name="IsActive" class="form-check-input"
				<%=q.isActive() ? "checked" : ""%>> <label
				class="form-check-label">Hiển thị</label>
		</div>

		<div class="d-flex justify-content-between">
			<button type="submit" class="btn btn-primary">
				<i class="bi bi-save"></i> Cập nhật
			</button>
			<a href="<%=request.getContextPath()%>/admin/QLHocKy/Index"
				class="btn btn-outline-secondary"> Hủy </a>
		</div>
	</form>
	<
</main>
