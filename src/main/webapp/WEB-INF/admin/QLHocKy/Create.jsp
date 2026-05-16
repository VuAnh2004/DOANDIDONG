<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<main id="main" class="main">
	<div class="pagetitle">
		<h2>Thêm Học Kỳ</h2>
	</div>


	<form action="<%=request.getContextPath()%>/admin/QLHocKy/Create"
		method="post">
		<div class="mb-3">
			<label class="form-label">Tên Học Kỳ</label> <input type="text"
				name="SemesterName" class="form-control" required>
		</div>
		<div class="mb-3">
			<label class="form-label">Năm bắt đầu - kết thúc</label> <input
				type="text" name="SemesterCode" class="form-control" required>
		</div>
		<div class="form-check mb-3">
			<input type="checkbox" name="IsActive" class="form-check-input"
				checked> <label class="form-check-label">Hiển thị</label>
		</div>
		<button type="submit" class="btn btn-success">Lưu</button>
		<a href="<%=request.getContextPath()%>/admin/QLHocKy/Index"
			class="btn btn-secondary">Hủy</a>
	</form>

</main>
