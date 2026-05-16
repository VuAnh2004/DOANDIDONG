<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.*"%>

<main id="main" class="main">
    <div class="card shadow-lg">
        <div class="card-body p-4">
            <h2 class="card-title text-primary mb-4">
                <i class="bi bi-person-plus-fill me-2"></i> Thêm nhiều học sinh vào lớp học
            </h2>

            <form action="${pageContext.request.contextPath}/admin/QLHS_LH/Create" method="post" id="multiStudentForm">
                <div class="row mb-4 gx-3">
                    <!-- Lớp học -->
                    <div class="col-md-6">
                        <label for="ClassID" class="form-label fw-bold">Lớp học</label>
                        <select name="ClassID" id="ClassID" class="form-select" required>
                            <option value="">--- Chọn lớp học ---</option>
                            <%
                                List<QLLopHoc> classList = (List<QLLopHoc>) request.getAttribute("classList");
                                if (classList != null) {
                                    for (QLLopHoc c : classList) {
                                        QLKhoi khoi = c.getKhois();
                                        QLKhoaHoc khoaHoc = c.getKhoaHoc();
                                        String gradeName = (khoi != null) ? khoi.getGradeName() : "N/A";
                                        String schoolYear = (khoaHoc != null) ? (khoaHoc.getStartYear() + "-" + khoaHoc.getEndYear()) : "N/A";
                                        int choCon = c.getMaxStudents() - (c.getCurrentStudents() != null ? c.getCurrentStudents() : 0);
                            %>
                            <option value="<%=c.getClassID()%>">
                                <%=c.getClassName()%> | Khối: <%=gradeName%> | Niên khóa: <%=schoolYear%> | Chỗ còn: <%=choCon%>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>

                    <!-- Khóa học -->
                    <div class="col-md-6">
                        <label for="CourseID" class="form-label fw-bold">Khóa học</label>
                        <select name="CourseID" id="CourseID" class="form-select" required>
                            <option value="">--- Chọn khóa học ---</option>
                            <%
                                List<QLKhoaHoc> courseList = (List<QLKhoaHoc>) request.getAttribute("courseList");
                                if (courseList != null) {
                                    for (QLKhoaHoc course : courseList) {
                            %>
                            <option value="<%=course.getCourseID()%>">
                                <%=course.getStartYear()%>-<%=course.getEndYear()%> | K<%=course.getCohort() != null ? course.getCohort() : ""%>
                            </option>
                            <%
                                    }
                                }
                            %>
                        </select>
                    </div>
                </div>

                <!-- Danh sách học sinh -->
                <h5 class="mt-4 mb-3 text-info"><i class="bi bi-list-check me-1"></i> Danh sách học sinh</h5>
                <div class="table-responsive table-responsive-custom">
                    <table class="table table-bordered table-hover align-middle" id="studentTable">
                        <thead class="table-primary">
                            <tr>
                                <th class="text-center" style="width: 60%">Học sinh</th>
                                <th class="text-center" style="width: 15%">Kích hoạt</th>
                                <th class="text-center" style="width: 15%">Chức năng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>
                                    <select name="StudentID[]" class="form-select" required>
                                        <option value="">--- Chọn học sinh ---</option>
                                        <%
                                            List<QLHocSinh> studentList = (List<QLHocSinh>) request.getAttribute("studentList");
                                            List<String> existingStudentIDs = (List<String>) request.getAttribute("existingStudentIDs");
                                            if (studentList != null) {
                                                for (QLHocSinh s : studentList) {
                                                    if (existingStudentIDs == null || !existingStudentIDs.contains(s.getStudentID())) {
                                        %>
                                        <option value="<%=s.getStudentID()%>"><%=s.getStudentID()%> - <%=s.getFullName()%></option>
                                        <%
                                                    }
                                                }
                                            }
                                        %>
                                    </select>
                                </td>
                                <td class="text-center">
                                    <input type="hidden" name="IsActive" value="true" />
                                    <input type="checkbox" class="form-check-input isActiveCheckbox" checked />
                                </td>
                                <td class="text-center">
                                    <button type="button" class="btn btn-danger btn-sm removeRow" disabled>
                                        <i class="bi bi-trash"></i> Xóa
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <button type="button" id="addRow" class="btn btn-outline-success mb-3">
                    <i class="bi bi-plus-circle"></i> Thêm học sinh
                </button>

                <div class="mt-4 pt-3 border-top d-flex justify-content-end">
                    <a href="${pageContext.request.contextPath}/admin/QLHS_LH" class="btn btn-warning me-2">
                        <i class="bi bi-arrow-left"></i> Quay lại
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save"></i> Lưu tất cả
                    </button>
                </div>
            </form>
        </div>
    </div>
</main>

<script>
let tbody = document.querySelector("#studentTable tbody");

// Cập nhật trạng thái nút Xóa
function updateRemoveButtons() {
    const rows = tbody.querySelectorAll("tr");
    rows.forEach(row => {
        row.querySelector(".removeRow").disabled = rows.length <= 1;
    });
}
updateRemoveButtons();

// Thêm dòng mới
document.getElementById("addRow").addEventListener("click", () => {
    const firstRow = tbody.querySelector("tr");
    const newRow = firstRow.cloneNode(true);

    // Reset giá trị select và checkbox
    newRow.querySelector("select").selectedIndex = 0;
    const checkbox = newRow.querySelector(".isActiveCheckbox");
    checkbox.checked = true;
    newRow.querySelector("input[type='hidden']").value = 'true';

    tbody.appendChild(newRow);
    updateRemoveButtons();
});

// Xóa dòng
tbody.addEventListener("click", e => {
    if (e.target.closest(".removeRow")) {
        const rows = tbody.querySelectorAll("tr");
        if (rows.length > 1) {
            e.target.closest("tr").remove();
            updateRemoveButtons();
        }
    }
});

// Đồng bộ checkbox -> hidden trước submit
document.getElementById("multiStudentForm").addEventListener("submit", () => {
    tbody.querySelectorAll("tr").forEach(row => {
        const checkbox = row.querySelector(".isActiveCheckbox");
        const hidden = row.querySelector("input[type='hidden'][name='IsActive']");
        hidden.value = checkbox.checked ? 'true' : 'false';
    });
});
</script>
