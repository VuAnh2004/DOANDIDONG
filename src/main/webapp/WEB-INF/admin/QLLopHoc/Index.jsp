<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="model.bean.QLLopHoc"%>
<%@ page import="model.bean.QLKhoi"%>
<%@ page import="model.bean.QLKhoaHoc"%>

<%
List<QLLopHoc> list = (List<QLLopHoc>) request.getAttribute("list");
int stt = 1;
%>

<main id="main" class="main">
	<div class="pagetitle">
		<div
			class="pagetitle d-flex justify-content-between align-items-center">
			<h2>Danh sách lớp học</h2>
			<a href="<%=request.getContextPath()%>/admin/QLLopHoc/Create"
				class="btn btn-success">Thêm lớp học</a>
		</div>

		<section class="section dashboard">
			<div class="row">
				<div class="col-12">
					<div class="card recent-sales overflow-auto">
						<div class="card-body mt-4">
							<table class="table table-borderless datatable">
								<thead>
									<tr>
										<th class="text-center">Stt</th>
										<th class="text-center">Tên lớp</th>
										<th class="text-center">Mã khối</th>
										<th class="text-center">Số lượng tối đa</th>
										<th class="text-center">Số lượng hiện tại</th>
										<th class="text-center">Năm học</th>
										<th class="text-center">Năm học - Niên khóa</th>
										<th class="text-center">Hiển thị</th>
										<th class="text-center">Chức năng</th>
									</tr>
								</thead>
								<tbody>
									<%
									if (list != null) {
										for (QLLopHoc item : list) {
									%>
									<tr>
										<td class="text-center"><%=stt++%></td>
										<td class="text-center text-primary"><a
											href="<%=request.getContextPath()%>/admin/QLLopHoc/Details?id=<%=item.getClassID()%>">
												<%=item.getClassName()%>
										</a></td>
										<td class="text-center"><%=item.getKhois() != null ? item.getKhois().getGradeName() : "Chưa có khối"%>
										</td>
										<td class="text-center"><%=item.getMaxStudents()%></td>
										<td class="text-center"><%=item.getCurrentStudents() != null ? item.getCurrentStudents() : 0%></td>
										<td class="text-center"><%=item.getSchoolYear()%></td>
										<td class="text-center">
											<%
											QLKhoaHoc kh = item.getKhoaHoc();
											if (kh != null) {
												out.print(kh.getStartYear() + " - " + kh.getEndYear() + " - Khóa " + kh.getCohort());
											} else {
												out.print("Chưa có khóa học");
											}
											%>
										</td>

										<td class="text-center">
											<form
												action="<%=request.getContextPath()%>/admin/QLLopHoc/ToggleStatus"
												method="post">
												<input type="hidden" name="id"
													value="<%=item.getClassID()%>" />
												<div
													class="form-check form-switch d-flex justify-content-center">
													<input class="form-check-input toggle-status-switch"
														type="checkbox" name="IsActive"
														<%=item.isActive() ? "checked" : ""%>
														onchange="this.form.submit()" />
												</div>
											</form>
										</td>
										<td class="text-center"><a
											href="<%=request.getContextPath()%>/admin/QLLopHoc/Edit?id=<%=item.getClassID()%>"
											class="btn btn-primary btn-sm"><i class="bi bi-pencil"></i></a>
											<a
											href="<%=request.getContextPath()%>/admin/QLLopHoc/Delete?id=<%=item.getClassID()%>"
											class="btn btn-danger btn-sm"><i class="bi bi-trash"></i></a>
										</td>
									</tr>
									<%
									}
									}
									%>
								</tbody>

							</table>
						</div>
					</div>
				</div>
			</div>
		</section>
	</div>
</main>
