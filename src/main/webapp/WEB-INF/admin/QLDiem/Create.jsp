<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.*"%>

<html>
<head>
<title>Nhập điểm học sinh</title>
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/assets/bootstrap.min.css">
<script
	src="<%=request.getContextPath()%>/assets/bootstrap.bundle.min.js"></script>
</head>
<body>

	<main id="main" class="main">
		<h2 class="text-primary mb-4">Nhập điểm học sinh</h2>

		<%
		// Lấy dữ liệu dropdowns
		// !!! SỬ DỤNG classListUnique (danh sách lớp đã được lọc duy nhất theo ClassName-Cohort trong Controller)
		List<QLLopHoc> classListUnique = (List<QLLopHoc>) request.getAttribute("classListUnique");
		// Danh sách các dropdown khác
		List<QLMonHoc> subjectList = (List<QLMonHoc>) request.getAttribute("subjectList");
		List<QLHocKy> semesterList = (List<QLHocKy>) request.getAttribute("semesterList");

		// Lấy giá trị đã chọn (đúng tên thuộc tính được gán trong Controller)
		String selectedClassCohort = (String) request.getAttribute("selectedClassCohort");
		Integer selectedSubjectId = (Integer) request.getAttribute("selectedSubjectID");
		Integer selectedSemesterId = (Integer) request.getAttribute("selectedSemesterID");

		// Danh sách học sinh (đã được lọc theo lớp-khóa)
		List<QLHocSinh> studentList = (List<QLHocSinh>) request.getAttribute("studentList");
		%>

		<form id="filterForm" action="<%=request.getContextPath()%>/admin/QLDiem/Create" method="get">
			<div class="row mb-3">
				
				<div class="col-md-4">
					<label>Lớp - Khóa</label> 
					<select name="classCohort" class="form-select" required onchange="this.form.submit()">
						<option value="">--- Chọn lớp ---</option>
						<%
						// SỬ DỤNG DANH SÁCH ĐÃ LỌC: classListUnique
						if (classListUnique != null && !classListUnique.isEmpty()) {

							for (QLLopHoc c : classListUnique) {
								String cohortStr = "";
								if (c.getKhoaHoc() != null && c.getKhoaHoc().getCohort() != null) {
									cohortStr = c.getKhoaHoc().getCohort().toString();
								}
								// VALUE PHẢI LÀ ClassID-Cohort để Controller lấy đúng học sinh
								String val = c.getClassID() + "-" + cohortStr;

								boolean isSelected = (selectedClassCohort != null && selectedClassCohort.equals(val));
						%>
						<option value="<%=val%>" <%=isSelected ? "selected" : ""%>>
							<%=c.getClassName()%> - K<%=cohortStr%>
						</option>
						<%
							}
						}
						%>
					</select>
				</div>


				<div class="col-md-4">
					<label>Môn học</label> 
					<select name="subjectID" class="form-select"
						required onchange="this.form.submit()">
						<option value="">--- Chọn môn ---</option>
						<%
						if (subjectList != null) {
							for (QLMonHoc m : subjectList) {
						%>
						<option value="<%=m.getSubjectID()%>"
							<%=(selectedSubjectId != null && selectedSubjectId.equals(m.getSubjectID())) ? "selected" : ""%>>
							<%=m.getSubjectName()%>
						</option>
						<%
						}
						}
						%>
					</select>
				</div>

				<div class="col-md-4">
					<label>Học kỳ</label> 
					<select name="semesterID" class="form-select"
						required onchange="this.form.submit()">
						<option value="">--- Chọn học kỳ ---</option>
						<%
						if (semesterList != null) {
							for (QLHocKy hk : semesterList) {
						%>
						<option value="<%=hk.getSemesterId()%>"
							<%=(selectedSemesterId != null && selectedSemesterId.equals(hk.getSemesterId())) ? "selected" : ""%>>
							<%=hk.getSemesterName()%> (<%=hk.getSemesterCode()%>)
						</option>
						<%
						}
						}
						%>
					</select>
				</div>
			</div>
		</form>

		<%
		if (studentList != null && !studentList.isEmpty()) {
		%>
		<form action="Create" method="post"> 
			<table class="table table-bordered">
				<thead class="table-light">
					<tr>
						<th>STT</th>
						<th>Mã HS</th>
						<th>Họ tên</th>
						<th>Miệng 1</th>
						<th>Miệng 2</th>
						<th>Miệng 3</th>
						<th>15 phút 1</th>
						<th>15 phút 2</th>
						<th>Giữa kỳ</th>
						<th>Cuối kỳ</th>
						<th>Điểm TB</th>
						<th>Đánh giá</th>
					</tr>
				</thead>
				<tbody>
					<%
					int idx = 1;
					for (QLHocSinh s : studentList) {
					%>
					<tr>
						<td><%=idx++%></td>
						<td><%=s.getStudentID()%></td>
						<td><%=s.getFullName()%></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="oralScore1_<%=s.getStudentID()%>"></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="oralScore2_<%=s.getStudentID()%>"></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="oralScore3_<%=s.getStudentID()%>"></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="quiz15Min1_<%=s.getStudentID()%>"></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="quiz15Min2_<%=s.getStudentID()%>"></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="midtermScore_<%=s.getStudentID()%>"></td>
						<td><input type="number" step="0.1" min="0" max="10"
							class="form-control score"
							name="finalScore_<%=s.getStudentID()%>"></td>
						<td><input type="text" class="form-control"
							id="avg_<%=s.getStudentID()%>" readonly></td>
						<td><input type="text" class="form-control"
							id="grade_<%=s.getStudentID()%>" readonly></td>
					</tr>
					<input type="hidden" name="studentIDs"
						value="<%=s.getStudentID()%>">
					<%
					}
					%>
				</tbody>
			</table>

			<input type="hidden" name="subjectID" value="<%=selectedSubjectId%>">
			<input type="hidden" name="semesterID"
				value="<%=selectedSemesterId%>">

			<div class="mt-3 text-end">
			<a href="${pageContext.request.contextPath}/admin/QLDiem"
						class="btn btn-warning me-2"> <i class="bi bi-arrow-left"></i>
						Quay lại
					</a>
				<button type="submit" class="btn btn-primary">Lưu tất cả</button>
			</div>
		</form>
		<%
		} else if (selectedClassCohort != null) {
		%>
		<div class="alert alert-warning">Lớp này chưa có học sinh.</div>
		<%
		}
		%>

		</div>

		<script>
// Tính điểm TB & đánh giá
function calcAvg(studentID) {
    let names = ['oralScore1','oralScore2','oralScore3','quiz15Min1','quiz15Min2','midtermScore','finalScore'];
    let total = 0, weight = 0;
    for (let name of names) {
        let el = document.getElementsByName(name + '_' + studentID)[0];
        let val = parseFloat(el.value);
        if (!isNaN(val)) {
            let w = 1;
            if (name === 'midtermScore') w = 2;
            else if (name === 'finalScore') w = 3;
            total += val * w;
            weight += w;
        }
    }
    // Định dạng lại điểm trung bình
    let avg = weight > 0 ? (total / weight).toFixed(2) : '';
    document.getElementById('avg_' + studentID).value = avg;

    let category = '';
    if (avg !== '') {
        let a = parseFloat(avg);
        if (a >= 8) category = 'Giỏi';
        else if (a >= 6.5) category = 'Khá';
        else if (a >= 5) category = 'Trung bình';
        else category = 'Yếu';
    }
    document.getElementById('grade_' + studentID).value = category;
}

// Gắn sự kiện onchange cho tất cả input điểm
<%if (studentList != null) {
	for (QLHocSinh s : studentList) {%>
document.querySelectorAll('[name^="oralScore1_<%=s.getStudentID()%>"],[name^="oralScore2_<%=s.getStudentID()%>"],[name^="oralScore3_<%=s.getStudentID()%>"],[name^="quiz15Min1_<%=s.getStudentID()%>"],[name^="quiz15Min2_<%=s.getStudentID()%>"],[name^="midtermScore_<%=s.getStudentID()%>"],[name^="finalScore_<%=s.getStudentID()%>"]').forEach(el=>{
    el.addEventListener('input',()=>calcAvg('<%=s.getStudentID()%>'));
});
<%}
}%>
</script>
</body>
</html>