<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLGiaoVien"%>
<%@ page import="model.bean.QLMonHoc"%>
<%@ page import="java.util.Collections"%>
<%@ page import="java.util.Set, java.util.List, java.util.Collections" %>


<%
QLGiaoVien gv = (QLGiaoVien) request.getAttribute("giaoVien");
List<QLMonHoc> availableSubjects = (List<QLMonHoc>) request.getAttribute("subjects");
Set<Integer> assignedSubjectIDs = (Set<Integer>) request.getAttribute("assignedSubjectIDs");

//Nếu không có môn nào được assign → tạo Set rỗng
if (assignedSubjectIDs == null) {
 assignedSubjectIDs = Collections.emptySet();
}

//Nếu không có môn nào trong database → tạo List rỗng
if (availableSubjects == null) {
 availableSubjects = Collections.emptyList();
}
%>
<main id="main" class="main">
	<div class="pagetitle">
		<h2>
			Chỉnh Sửa Giáo Viên:
			<%=gv != null ? gv.getFullName() : ""%></h2>
	</div>

	<form action="<%=request.getContextPath()%>/admin/QLGiaoVien/Edit"
		method="post" enctype="multipart/form-data">
		<input type="hidden" name="ID"
			value="<%=gv != null ? gv.getID() : ""%>">

		<div class="row">
			<!-- Cột 1 -->
			<div class="col-md-6">
				<div class="mb-3">
					<label class="form-label">Mã Giáo Viên</label> <input type="text"
						name="TeacherID" class="form-control" required
						value="<%=gv != null ? gv.getTeacherID() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Họ Tên</label> <input type="text"
						name="FullName" class="form-control" required
						value="<%=gv != null ? gv.getFullName() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Ngày Sinh</label> <input type="date"
						name="Birth" class="form-control"
						value="<%=gv != null && gv.getBirth() != null ? gv.getBirth() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Giới Tính</label> <select name="Gender"
						class="form-control">
						<option value="Nam"
							<%=gv != null && "Nam".equals(gv.getGender()) ? "selected" : ""%>>Nam</option>
						<option value="Nữ"
							<%=gv != null && "Nữ".equals(gv.getGender()) ? "selected" : ""%>>Nữ</option>
						<option value="Khác"
							<%=gv != null && "Khác".equals(gv.getGender()) ? "selected" : ""%>>Khác</option>
					</select>
				</div>
				<div class="mb-3">
					<label class="form-label">Địa Chỉ</label> <input type="text"
						name="Address" class="form-control"
						value="<%=gv != null ? gv.getAddress() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Số CCCD</label> <input type="text"
						name="CCCD" class="form-control"
						value="<%=gv != null ? gv.getCCCD() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Điện thoại</label> <input type="text"
						name="NumberPhone" class="form-control"
						value="<%=gv != null ? gv.getNumberPhone() : ""%>">
				</div>
			</div>

			<!-- Cột 2 -->
			<div class="col-md-6">
				<div class="mb-3">
					<label class="form-label">Tình trạng dạy</label> <select
						name="StatusTeacher" class="form-select">
						<option value="Đang dạy"
							<%=gv != null && "Đang dạy".equals(gv.getStatusTeacher()) ? "selected" : ""%>>Đang
							dạy</option>
						<option value="Chuyển công tác"
							<%=gv != null && "Chuyển công tác".equals(gv.getStatusTeacher()) ? "selected" : ""%>>Chuyển
							công tác</option>
						<option value="Nghỉ hưu"
							<%=gv != null && "Nghỉ hưu".equals(gv.getStatusTeacher()) ? "selected" : ""%>>Nghỉ
							hưu</option>
					</select>
				</div>

				<div class="mb-3">
					<label class="form-label">Dân tộc</label> <input type="text"
						name="Nation" class="form-control"
						value="<%=gv != null ? gv.getNation() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Tôn giáo</label> <input type="text"
						name="Religion" class="form-control"
						value="<%=gv != null ? gv.getReligion() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Vào đảng</label> <select name="GroupDV"
						class="form-select">
						<option value="Đã vào"
							<%=gv != null && "Đã vào".equals(gv.getGroupDV()) ? "selected" : ""%>>Đã
							vào</option>
						<option value="Chưa vào"
							<%=gv != null && "Chưa vào".equals(gv.getGroupDV()) ? "selected" : ""%>>Chưa
							vào</option>
						<option value="Không xác định"
							<%=gv != null && "Không xác định".equals(gv.getGroupDV()) ? "selected" : ""%>>Không
							xác định</option>
					</select>
				</div>
				<div class="mb-3">
					<label class="form-label">Số BHXH</label> <input type="text"
						name="NumberBHXH" class="form-control"
						value="<%=gv != null ? gv.getNumberBHXH() : ""%>">
				</div>
				<div class="mb-3">
					<label class="form-label">Ảnh Đại Diện (Bỏ trống để giữ ảnh
						cũ)</label> <input type="file" name="Images" class="form-control">
					<%
					if (gv != null && gv.getImages() != null && !gv.getImages().isEmpty()) {
						String imgPath = request.getContextPath() + "/" + gv.getImages();
					%>
					<img src="<%=imgPath%>" width="100" height="100"
						style="object-fit: cover; border-radius: 6px; margin-top: 10px;" />
					<%
					}
					%>
				</div>
			</div>
		</div>

		<hr>

		<div class="mb-3">
			<label class="form-label"><i class="bi bi-book"></i> Chọn Môn
				Học Phụ Trách</label>
			<div class="row border p-3 bg-light">
				<%
				for (QLMonHoc subject : availableSubjects) {
					boolean isAssigned = assignedSubjectIDs.contains(subject.getSubjectID());
				%>
				<div class="col-md-4">
					<div class="form-check">
						<input class="form-check-input" type="checkbox" name="SubjectIDs"
							value="<%=subject.getSubjectID()%>"
							id="subject-<%=subject.getSubjectID()%>"
							<%=isAssigned ? "checked" : ""%>> <label
							class="form-check-label"
							for="subject-<%=subject.getSubjectID()%>"> <%=subject.getSubjectName()%>
						</label>
					</div>
				</div>
				<%
				}
				%>
				<%
				if (availableSubjects.isEmpty()) {
				%>
				<p class="text-danger">Chưa có môn học nào được tạo.</p>
				<%
				}
				%>
			</div>
		</div>



		<div class="form-check mb-3">
			<input type="checkbox" name="IsActive" class="form-check-input"
				<%=gv != null && gv.isIsActive() ? "checked" : ""%>> <label
				class="form-check-label">Hoạt động</label>
		</div>

		<div class="d-flex">
			<button type="submit" class="btn btn-primary me-2">
				<i class="bi bi-save"></i> Cập nhật
			</button>
			<a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Index"
				class="btn btn-secondary">Hủy</a>
		</div>
	</form>
</main>
