<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<main id="main" class="main">
	<div class="pagetitle">
		<h2>Thêm Môn Học</h2>
	</div>

	<form action="<%=request.getContextPath()%>/admin/QLMonHoc/Create"
		method="post">

		<div class="mb-3">
			<label class="form-label">Tên Môn Học</label> <input type="text"
				name="SubjectName" class="form-control" required>
		</div>

		<div class="mb-3">
			<label class="form-label">Số Tiết Học</label> <input type="number"
				name="NumberOfLesson" class="form-control" required min="1">
		</div>

		<div class="mb-3">
			<label class="form-label">Học Kỳ Áp Dụng</label> <select
				name="Semester" class="form-control">
				<option value="Cả 2 Học kỳ">Cả 2 Học kỳ</option>
				<option value="Học kỳ 1">Học kỳ 1</option>
				<option value="Học kỳ 2">Học kỳ 2</option>
				
			</select>
		</div>

		<div class="form-check mb-3">
			<input type="checkbox" name="IsActive" class="form-check-input"
				checked> <label class="form-check-label">Hiển thị</label>
		</div>

		<button type="submit" class="btn btn-success">Lưu</button>
		<a href="<%=request.getContextPath()%>/admin/QLMonHoc/Index"
			class="btn btn-secondary">Hủy</a>
	</form>
</main>