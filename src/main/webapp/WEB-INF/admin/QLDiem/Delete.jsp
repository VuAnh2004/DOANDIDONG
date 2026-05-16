<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="model.bean.*"%>

<main id="main" class="main">
	<div class="card shadow-lg">
		<div class="card-body p-4">
			<h2 class="card-title text-danger mb-4">
				<i class="bi bi-trash3-fill me-2"></i> Xóa điểm học sinh
			</h2>

			<p>Bạn có chắc chắn muốn xóa bản ghi điểm này không?</p>

			<%
			QLDiem d = (QLDiem) request.getAttribute("diem");
			if (d != null) {
			%>
			<form action="${pageContext.request.contextPath}/admin/QLDiem/Delete"
				method="post">
				<input type="hidden" name="gradeID" value="<%=d.getGradeID()%>">
				<table class="table table-bordered">
					<tr>
						<th>Mã học sinh</th>
						<td><%=d.getStudentID()%></td>
					</tr>
					<tr>
						<th>Điểm trung bình</th>
						<td><%=d.getAverageScore() != null ? d.getAverageScore() : ""%></td>
					</tr>
				</table>
				<div class="mt-3 text-end">
					<a href="${pageContext.request.contextPath}/admin/QLDiem"
						class="btn btn-secondary me-2">Hủy</a>
					<button type="submit" class="btn btn-danger">Xóa</button>
				</div>
			</form>
			<%
			} else {
			%>
			<p class="text-warning">Bản ghi không tồn tại!</p>
			<a href="${pageContext.request.contextPath}/admin/QLDiem"
				class="btn btn-primary">Quay lại</a>
			<%
			}
			%>

		</div>
	</div>
</main>
