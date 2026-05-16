<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLMonHoc"%>

<%
List<QLMonHoc> availableSubjects = (List<QLMonHoc>) request.getAttribute("subjects");
%>

<main id="main" class="main">
	<div class="pagetitle">
		<h2>Thêm Giáo Viên</h2>
	</div>

	<form action="<%=request.getContextPath()%>/admin/QLGiaoVien/Create"
		method="post" enctype="multipart/form-data">

		<div class="row">
			<!-- Cột 1 -->
			<div class="col-md-6">
				<div class="mb-3">
					<label class="form-label">Mã Giáo Viên</label> <input type="text"
						name="TeacherID" class="form-control" required>
				</div>
				<div class="mb-3">
					<label class="form-label">Họ Tên</label> <input type="text"
						name="FullName" class="form-control" required>
				</div>
				<div class="mb-3">
					<label class="form-label">Ngày Sinh</label> <input type="date"
						name="Birth" class="form-control">
				</div>
				<div class="mb-3">
					<label class="form-label">Giới Tính</label> <select name="Gender"
						class="form-control">
						<option value="Nam">Nam</option>
						<option value="Nữ">Nữ</option>
						<option value="Khác">Khác</option>
					</select>
				</div>
				<div class="mb-3">
					<label class="form-label">Địa Chỉ</label> <input type="text"
						name="Address" class="form-control">
				</div>
				<div class="mb-3">
					<label class="form-label">Số CCCD</label> <input type="text"
						name="CCCD" class="form-control">
				</div>
				<div class="mb-3">
					<label class="form-label">Điện thoại</label> <input type="text"
						name="NumberPhone" class="form-control">
				</div>
			</div>

			<!-- Cột 2 -->
			<div class="col-md-6">
				<div class="mb-3">
					<label class="form-label">Tình trạng dạy</label> <select
						name="StatusTeacher" class="form-select">
						<option value="Đang dạy">Đang dạy</option>
						<option value="Chuyển công tác">Chuyển công tác</option>
						<option value="Nghỉ hưu">Nghỉ hưu</option>
					</select>
				</div>

				<div class="mb-3">
					<label class="form-label">Dân tộc</label> <input type="text"
						name="Nation" class="form-control">
				</div>
				<div class="mb-3">
					<label class="form-label">Tôn giáo</label> <input type="text"
						name="Religion" class="form-control">
				</div>
				<div class="mb-3">
					<label class="form-label">Vào đảng</label> <select name="GroupDV"
						class="form-select">
						<option value="Đã vào">Đã vào</option>
						<option value="Chưa vào">Chưa vào</option>
						<option value="Không xác định">Không xác định</option>
					</select>
				</div>
				<div class="mb-3">
					<label class="form-label">Số BHXH</label> <input type="text"
						name="NumberBHXH" class="form-control">
				</div>
				<div class="mb-3">
					<label class="form-label">Ảnh Đại Diện</label> <input type="file"
						name="Images" class="form-control">
				</div>
			</div>
		</div>

		<hr>

		<!-- Checkbox Môn Học -->
		<div class="mb-3">
			<label class="form-label"><i class="bi bi-book"></i> Chọn Môn
				Học Phụ Trách</label>
			<div class="row border p-3 bg-light">
				<%
				if (availableSubjects != null && !availableSubjects.isEmpty()) {
					for (QLMonHoc subject : availableSubjects) {
				%>
				<div class="col-md-4">
					<div class="form-check">
						<input class="form-check-input" type="checkbox" name="SubjectIDs"
							value="<%=subject.getSubjectID()%>"
							id="subject-<%=subject.getSubjectID()%>"> <label
							class="form-check-label"
							for="subject-<%=subject.getSubjectID()%>"> <%=subject.getSubjectName()%>
						</label>
					</div>
				</div>
				<%
				}
				} else {
				%>
				<p class="text-danger">Chưa có môn học nào được tạo.</p>
				<%
				}
				%>
			</div>
		</div>

		<div class="form-check mb-3">
			<input type="checkbox" name="IsActive" class="form-check-input"
				checked> <label class="form-check-label">Hoạt động</label>
		</div>

		<div class="d-flex">
			<button type="submit" class="btn btn-success me-2">
				<i class="bi bi-save"></i> Lưu
			</button>
			<a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Index"
				class="btn btn-secondary"> Hủy </a>
		</div>
	</form>
</main>
