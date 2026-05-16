<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLKhoaHoc" %>

<%
    QLKhoaHoc kh = (QLKhoaHoc) request.getAttribute("kh");
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Sửa Khóa Học</h2>
    </div>

    <form action="<%=request.getContextPath()%>/admin/QLKhoaHoc/Edit" method="post">
        <input type="hidden" name="CourseID" value="<%=kh.getCourseID()%>">

        <div class="mb-3">
            <label class="form-label">Năm bắt đầu</label>
            <input type="number" name="StartYear" id="startYear" class="form-control" required
                   value="<%=kh.getStartYear()%>">
        </div>

        <div class="mb-3">
            <label class="form-label">Năm kết thúc</label>
            <input type="number" name="EndYear" id="endYear" class="form-control" required
                   value="<%=kh.getEndYear()%>">
        </div>

        <div class="mb-3">
            <label class="form-label">Khóa (Cohort)</label>
            <input type="text" id="cohortDisplay" class="form-control" readonly>
            <input type="hidden" name="Cohort" id="cohort">
        </div>

        <div class="form-check mb-3">
            <input type="checkbox" name="IsActive" class="form-check-input"
                   <%= kh.isActive() ? "checked" : "" %>>
            <label class="form-check-label">Hiển thị</label>
        </div>

        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="<%=request.getContextPath()%>/admin/QLKhoaHoc" class="btn btn-secondary">Hủy</a>
    </form>
</main>

<script>
    const startYearInput = document.getElementById("startYear");
    const endYearInput = document.getElementById("endYear");
    const cohortDisplay = document.getElementById("cohortDisplay");
    const cohortHidden = document.getElementById("cohort");

    function calculateCohortNumber(startYear) {
        if (!startYear || isNaN(startYear)) return null;
        return 60 + (startYear - 2024); // logic giống C# Edit: 2024 → K60
    }

    function updateEndYearAndCohort() {
        const start = parseInt(startYearInput.value);
        if (!isNaN(start)) {
            const suggestedEnd = start + 3;
            endYearInput.value = suggestedEnd;

            const cohortNum = calculateCohortNumber(start);
            cohortHidden.value = cohortNum;
            cohortDisplay.value = cohortNum ? `K${cohortNum}` : "";
        } else {
            endYearInput.value = "";
            cohortHidden.value = "";
            cohortDisplay.value = "";
        }
    }

    startYearInput.addEventListener("input", updateEndYearAndCohort);

    // Tính Cohort ngay khi load trang Edit
    window.addEventListener("DOMContentLoaded", updateEndYearAndCohort);
</script>
