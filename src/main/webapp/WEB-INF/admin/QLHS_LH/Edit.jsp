 <%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.*"%>

<%
    QLHS_LH hs_lh = (QLHS_LH) request.getAttribute("hs_lh");
    if (hs_lh == null) {
        response.sendRedirect(request.getContextPath() + "/admin/QLHS_LH/Index");
        return;
    }
%>

<main id="main" class="main">
    <h2>Chỉnh sửa học sinh</h2>

    <form action="<%=request.getContextPath()%>/admin/QLHS_LH/Edit" method="post">
        <input type="hidden" name="HocSinhLopHocID" value="<%=hs_lh.getHocSinhLopHocID()%>" />

        <!-- Học sinh -->
        <div class="mb-3">
            <label>Học sinh:</label>
            <select name="StudentID" class="form-select" required>
                <%
                    List<QLHocSinh> studentList = (List<QLHocSinh>) request.getAttribute("studentList");
                    if (studentList != null) {
                        for (QLHocSinh hs : studentList) {
                            String selected = hs.getStudentID().equals(hs_lh.getStudentID()) ? "selected" : "";
                %>
                <option value="<%=hs.getStudentID()%>" <%=selected%>>
                    <%=hs.getStudentID()%> - <%=hs.getFullName()%>
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <!-- Lớp học -->
        <div class="mb-3">
            <label>Lớp học:</label>
            <select name="ClassID" class="form-select" required>
                <%
                    List<QLLopHoc> classList = (List<QLLopHoc>) request.getAttribute("classList");
                    if (classList != null) {
                        for (QLLopHoc lop : classList) {
                            String selected = (lop.getClassID() == hs_lh.getClassID()) ? "selected" : "";
                            QLKhoi khoi = lop.getKhois();
                            QLKhoaHoc khoaHoc = lop.getKhoaHoc();
                            String gradeName = (khoi != null && khoi.getGradeName() != null) ? khoi.getGradeName() : "N/A";
                            String schoolYear = (khoaHoc != null) ? (khoaHoc.getStartYear() + "-" + khoaHoc.getEndYear()) : "N/A";
                %>
                <option value="<%=lop.getClassID()%>" <%=selected%>>
                    <%=lop.getClassName()%> | Khối: <%=gradeName%> | Niên khóa: <%=schoolYear%>
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <!-- Học kỳ -->
       <%--  <div class="mb-3">
            <label>Học kỳ:</label>
            <select name="SemesterID" class="form-select">
                <%
                    List<QLHocKy> semesterList = (List<QLHocKy>) request.getAttribute("semesterList");
                    if (semesterList != null) {
                        for (QLHocKy hk : semesterList) {
                            String selected = (hs_lh.getSemesterID() != null && hk.getSemesterID().equals(hs_lh.getSemesterID())) ? "selected" : "";
                %>
                <option value="<%=hocky.getSemesterID()%>" <%=selected%>>
                    <%=hk.getSemesterName() != null ? hk.getSemesterName() : "" %>
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div> --%>

        <!-- Khóa học -->
        <%-- <div class="mb-3">
            <label>Khóa học:</label>
            <select name="CourseID" class="form-select">
                <%
                    List<QLKhoaHoc> courseList = (List<QLKhoaHoc>) request.getAttribute("courseList");
                    if (courseList != null) {
                        for (QLKhoaHoc kh : courseList) {
                            String selected = (hs_lh.getCourseID() != null && khoaHoc.getCourseID().equals(hs_lh.getCourseID())) ? "selected" : "";
                %>
                <option value="<%=kh.getCourseID()%>" <%=selected%>>
                    <%=kh.getStartYear()%>-<%=kh.getEndYear()%> | Khóa: <%=kh.getCohort() != null ? kh.getCohort() : "" %>
                </option>
                <%
                        }
                    }
                %>
            </select>
        </div> --%>

        <!-- IsActive -->
        <div class="form-check mb-3">
            <input type="hidden" name="IsActive" value="false" />
            <input type="checkbox" class="form-check-input" id="isActive" name="IsActive" value="true"
                <%= (hs_lh.getIsActive() != null && hs_lh.getIsActive()) ? "checked" : "" %> />
            <label class="form-check-label" for="isActive">Hoạt động</label>
        </div>

        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="<%=request.getContextPath()%>/admin/QLHS_LH" class="btn btn-secondary">Hủy</a>
    </form>
</main>
