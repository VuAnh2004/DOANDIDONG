<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Thêm Khóa Học</h2>
    </div>

    <form action="<%=request.getContextPath()%>/admin/QLKhoaHoc/Create" method="post">
        <div class="mb-3">
            <label class="form-label">Năm bắt đầu</label>
            <input type="number" name="StartYear" id="startYear" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Năm kết thúc</label>
            <input type="number" name="EndYear" id="endYear" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Khóa (Cohort)</label>
            <input type="text" id="cohortDisplay" class="form-control" readonly>
            <input type="hidden" name="Cohort" id="cohort">
        </div>

        <div class="form-check mb-3">
            <input type="checkbox" name="IsActive" class="form-check-input" checked>
            <label class="form-check-label">Hiển thị</label>
        </div>

        <button type="submit" class="btn btn-success">Lưu</button>
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
        return startYear - 1964; // Tự tính theo mốc 1964 → giống Create C#
    }

    function updateEndYearAndCohort() {
        const start = parseInt(startYearInput.value);
        if (!isNaN(start)) {
            endYearInput.value = start + 3; // gợi ý kết thúc +3 năm
            const cohortNum = calculateCohortNumber(start);
            cohortHidden.value = cohortNum;
            cohortDisplay.value = cohortNum ? "K" + cohortNum : "";
        } else {
            endYearInput.value = "";
            cohortHidden.value = "";
            cohortDisplay.value = "";
        }
    }

    startYearInput.addEventListener("input", updateEndYearAndCohort);

    // Nếu có giá trị sẵn khi load (edit lại form), tính luôn
    window.addEventListener("DOMContentLoaded", () => updateEndYearAndCohort());
</script>
