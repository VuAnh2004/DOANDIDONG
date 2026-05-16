<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.*"%>

<%
// Lấy đối tượng điểm từ request
QLDiem d = (QLDiem) request.getAttribute("diem");

// Danh sách dropdown
List<QLHocSinh> studentList = (List<QLHocSinh>) request.getAttribute("studentList");
List<QLMonHoc> subjectList = (List<QLMonHoc>) request.getAttribute("subjectList");
List<QLHocKy> semesterList = (List<QLHocKy>) request.getAttribute("semesterList");
%>

<main id="main" class="main">
	<div class="card shadow-lg">
		<div class="card-body p-4">
			<h2 class="card-title text-primary mb-4">
				<i class="bi bi-pencil-square me-2"></i> Sửa điểm học sinh
			</h2>

			<form action="${pageContext.request.contextPath}/admin/QLDiem/Edit"
				method="post">
				<input type="hidden" name="gradeID" value="<%=d.getGradeID()%>">

				<!-- HỌC SINH -->
				<div class="mb-3">
					<label class="form-label fw-bold">Học sinh</label> <select
						name="studentID" class="form-select" required>
						<option value="">--- Chọn học sinh ---</option>
						<%
						if (studentList != null) {
							for (QLHocSinh s : studentList) {
						%>
						<option value="<%=s.getStudentID()%>"
							<%=s.getStudentID().equals(d.getStudentID()) ? "selected" : ""%>>
							<%=s.getStudentID()%> -
							<%=s.getFullName()%>
						</option>
						<%
						}
						}
						%>
					</select>
				</div>

				<!-- MÔN HỌC -->
				<div class="mb-3">
					<label class="form-label fw-bold">Môn học</label> <select
						name="subjectID" class="form-select" required>
						<option value="">--- Chọn môn ---</option>
						<%
						if (subjectList != null) {
							for (QLMonHoc m : subjectList) {
						%>
						<option value="<%=m.getSubjectID()%>"
							<%=m.getSubjectID() == d.getSubjectID() ? "selected" : ""%>>
							<%=m.getSubjectName()%>
						</option>
						<%
						}
						}
						%>
					</select>
				</div>

				<!-- HỌC KỲ -->
				<div class="mb-3">
					<label class="form-label fw-bold">Học kỳ</label> <select
						name="semesterID" class="form-select" required>
						<option value="">--- Chọn học kỳ ---</option>
						<%
						if (semesterList != null) {
							for (QLHocKy hk : semesterList) {
						%>
						<option value="<%=hk.getSemesterId()%>"
							<%=hk.getSemesterId() == d.getSemesterID() ? "selected" : ""%>>
							<%=hk.getSemesterName()%> (<%=hk.getSemesterCode()%>)
						</option>
						<%
						}
						}
						%>
					</select>
				</div>

				<!-- ĐIỂM MIỆNG -->
				<div class="row">
					<div class="col-md-4 mb-3">
						<label class="form-label">Miệng 1</label> <input type="number"
							step="0.1" name="oralScore1" class="form-control score"
							value="<%=d.getOralScore1() != null ? d.getOralScore1() : ""%>">
					</div>
					<div class="col-md-4 mb-3">
						<label class="form-label">Miệng 2</label> <input type="number"
							step="0.1" name="oralScore2" class="form-control score"
							value="<%=d.getOralScore2() != null ? d.getOralScore2() : ""%>">
					</div>
					<div class="col-md-4 mb-3">
						<label class="form-label">Miệng 3</label> <input type="number"
							step="0.1" name="oralScore3" class="form-control score"
							value="<%=d.getOralScore3() != null ? d.getOralScore3() : ""%>">
					</div>
				</div>

				<!-- ĐIỂM 15 PHÚT -->
				<div class="row">
					<div class="col-md-6 mb-3">
						<label class="form-label">15 phút 1</label> <input type="number"
							step="0.1" name="quiz15Min1" class="form-control score"
							value="<%=d.getQuiz15Min1() != null ? d.getQuiz15Min1() : ""%>">
					</div>
					<div class="col-md-6 mb-3">
						<label class="form-label">15 phút 2</label> <input type="number"
							step="0.1" name="quiz15Min2" class="form-control score"
							value="<%=d.getQuiz15Min2() != null ? d.getQuiz15Min2() : ""%>">
					</div>
				</div>

				<!-- GIỮA KỲ & CUỐI KỲ -->
				<div class="row">
					<div class="col-md-6 mb-3">
						<label class="form-label">Giữa kỳ</label> <input type="number"
							step="0.1" name="midtermScore" class="form-control score"
							value="<%=d.getMidtermScore() != null ? d.getMidtermScore() : ""%>">
					</div>
					<div class="col-md-6 mb-3">
						<label class="form-label">Cuối kỳ</label> <input type="number"
							step="0.1" name="finalScore" class="form-control score"
							value="<%=d.getFinalScore() != null ? d.getFinalScore() : ""%>">
					</div>
				</div>

				<!-- ĐIỂM TRUNG BÌNH & ĐÁNH GIÁ -->
				<div class="mb-3">
					<label class="form-label fw-bold text-success">Điểm trung
						bình môn</label> <input type="text" id="avgScore"
						class="form-control fw-bold text-success" readonly
						value="<%=d.getAverageScore() != null ? d.getAverageScore() : ""%>">
					<input type="hidden" name="averageScore" id="averageScore"
						value="<%=d.getAverageScore() != null ? d.getAverageScore() : ""%>">
					<input type="hidden" name="gradeCategory" id="gradeCategory"
						value="<%=d.getGradeCategory() != null ? d.getGradeCategory() : ""%>">
				</div>

				<!-- GHI CHÚ -->
				<div class="mb-3">
					<label class="form-label fw-bold text-danger">Lý do sửa
						điểm</label>
					<textarea name="notes" class="form-control" required
						oninvalid="this.setCustomValidity('Vui lòng ghi lại lý do sửa điểm')"
						oninput="this.setCustomValidity('')"><%=d.getNotes() != null ? d.getNotes() : ""%></textarea>
				</div>

				<!-- TRẠNG THÁI -->
				<div class="form-check mb-3">
					<input type="checkbox" name="isActive" class="form-check-input"
						<%=d.isActive() ? "checked" : ""%>> <label
						class="form-check-label">Kích hoạt</label>
				</div>

				<!-- NÚT LƯU -->
				<div class="mt-4 text-end">
					<a href="${pageContext.request.contextPath}/admin/QLDiem"
						class="btn btn-warning me-2"> <i class="bi bi-arrow-left"></i>
						Quay lại
					</a>
					<button type="submit" class="btn btn-primary">
						<i class="bi bi-save"></i> Lưu
					</button>
				</div>
			</form>
		</div>
	</div>
</main>

<!-- SCRIPT TÍNH ĐIỂM TRUNG BÌNH & ĐÁNH GIÁ -->
<script>
function calcAvg() {
    let total = 0;
    let weight = 0;

    function v(name, w) {
        let e = document.getElementsByName(name)[0].value;
        if (e !== "") {
            total += parseFloat(e) * w;
            weight += w;
        }
    }

    v("oralScore1", 1);
    v("oralScore2", 1);
    v("oralScore3", 1);
    v("quiz15Min1", 1);
    v("quiz15Min2", 1);
    v("midtermScore", 2);
    v("finalScore", 3);

    let avg = weight > 0 ? (total / weight).toFixed(2) : "";
    document.getElementById("avgScore").value = avg;
    document.getElementById("averageScore").value = avg;

    // Tự động tính đánh giá
    let category = "";
    if (avg !== "") {
        let a = parseFloat(avg);
        if (a >= 8) category = "Giỏi";
        else if (a >= 6.5) category = "Khá";
        else if (a >= 5) category = "Trung bình";
        else category = "Yếu";
    }
    document.getElementById("gradeCategory").value = category;
}

// Gắn sự kiện onchange cho tất cả input điểm
document.querySelectorAll(".score").forEach(i => {
    i.addEventListener("input", calcAvg);
});

// Tính ngay khi load trang
calcAvg();
</script>
