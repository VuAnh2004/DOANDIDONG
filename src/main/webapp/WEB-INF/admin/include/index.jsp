<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<main id="main" class="main">

  <!-- ======= Page Title ======= -->
  <div class="pagetitle">
    <h1>Bảng điều khiển</h1>
    <nav>
      <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="index.jsp">Trang chủ</a></li>
        <li class="breadcrumb-item active">Thống kê</li>
      </ol>
    </nav>
  </div>

  <!-- ======= Dashboard ======= -->
  <section class="section dashboard">
    <div class="row">

      <!-- ======= THẺ TỔNG QUAN ======= -->
      <div class="col-xl-4 col-md-6">
        <div class="card info-card">
          <div class="card-body">
            <h5 class="card-title">Tổng số học sinh</h5>
            <div class="d-flex align-items-center">
              <div class="card-icon rounded-circle bg-primary text-white">
                <i class="bi bi-people"></i>
              </div>
              <div class="ps-3">
                <h6>${totalStudents}</h6>
                <span class="text-success small">Active</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-xl-4 col-md-6">
        <div class="card info-card">
          <div class="card-body">
            <h5 class="card-title">Tổng số giáo viên</h5>
            <div class="d-flex align-items-center">
              <div class="card-icon rounded-circle bg-success text-white">
                <i class="bi bi-person-badge"></i>
              </div>
              <div class="ps-3">
                <h6>${totalTeachers}</h6>
                <span class="text-primary small">Active</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="col-xl-4 col-md-12">
        <div class="card info-card">
          <div class="card-body">
            <h5 class="card-title">Tổng số lớp học</h5>
            <div class="d-flex align-items-center">
              <div class="card-icon rounded-circle bg-warning text-white">
                <i class="bi bi-building"></i>
              </div>
              <div class="ps-3">
                <h6>${totalClasses}</h6>
                <span class="text-danger small">Lớp mở</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ======= BIỂU ĐỒ SĨ SỐ ======= -->
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Thống kê sĩ số học sinh (Top 10)</h5>
            <div id="studentChart"></div>
          </div>
        </div>
      </div>

      <!-- ======= BIỂU ĐỒ HỌC LỰC ======= -->
      <div class="col-lg-6">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Tỷ lệ học lực / hạnh kiểm</h5>
            <div id="rankChart" style="height: 350px"></div>
          </div>
        </div>
      </div>

      <!-- ======= BIỂU ĐỒ PHỔ ĐIỂM ======= -->
      <div class="col-lg-6">
        <div class="card">
          <div class="card-body">
            <h5 class="card-title">Phổ điểm trung bình</h5>
            <div id="scoreChart"></div>
          </div>
        </div>
      </div>

    </div>
  </section>
</main>

<!-- ======= SCRIPT BIỂU ĐỒ ======= -->

<script>
document.addEventListener("DOMContentLoaded", () => {

  /* ===== BIỂU ĐỒ SĨ SỐ ===== */
  new ApexCharts(document.querySelector("#studentChart"), {
    chart: { type: 'bar', height: 350 },
    series: [{
      name: 'Sĩ số',
      data: ${studentCounts}
    }],
    xaxis: {
      categories: ${classNames}
    }
  }).render();

  /* ===== BIỂU ĐỒ PHỔ ĐIỂM ===== */
  new ApexCharts(document.querySelector("#scoreChart"), {
    chart: { type: 'bar', height: 350 },
    series: [{
      name: 'Số lượng',
      data: ${scoreData}
    }],
    xaxis: {
      categories: ['Kém (<5)', 'TB (5-6.5)', 'Khá (6.5-8)', 'Giỏi (>=8)']
    }
  }).render();

  /* ===== BIỂU ĐỒ HỌC LỰC ===== */
  echarts.init(document.querySelector("#rankChart")).setOption({
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['45%', '70%'],
      data: [
        { value: ${gioi}, name: 'Giỏi' },
        { value: ${kha}, name: 'Khá' },
        { value: ${xuatSac}, name: 'Xuất sắc' }
      ]
    }]
  });

});
</script>
