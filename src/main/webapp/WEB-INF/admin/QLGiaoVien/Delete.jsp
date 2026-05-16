<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLGiaoVien"%>

<%
QLGiaoVien gv = (QLGiaoVien) request.getAttribute("giaoVien");
if (gv == null) {
    response.sendRedirect(request.getContextPath() + "/admin/QLGiaoVien/Index");
    return;
}
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Xóa Giáo Viên: <%= gv.getFullName() %></h2>
    </div>

    <div class="alert alert-warning">
        <p>Bạn có chắc chắn muốn xóa giáo viên này không?</p>
    </div>

    <form action="<%=request.getContextPath()%>/admin/QLGiaoVien/Delete" method="post">
        <input type="hidden" name="ID" value="<%=gv.getID()%>">

        <table class="table table-bordered">
            <tr>
                <th>Mã Giáo Viên</th>
                <td><%=gv.getTeacherID()%></td>
            </tr>
            <tr>
                <th>Họ Tên</th>
                <td><%=gv.getFullName()%></td>
            </tr>
            <tr>
                <th>Ngày Sinh</th>
                <td><%=gv.getBirth()%></td>
            </tr>
            <tr>
                <th>Giới Tính</th>
                <td><%=gv.getGender()%></td>
            </tr>
            <tr>
                <th>Địa Chỉ</th>
                <td><%=gv.getAddress()%></td>
            </tr>
            <tr>
                <th>Số CCCD</th>
                <td><%=gv.getCCCD()%></td>
            </tr>
            <tr>
                <th>Điện thoại</th>
                <td><%=gv.getNumberPhone()%></td>
            </tr>
            
        </table>

        <div class="d-flex">
            <button type="submit" class="btn btn-danger me-2">
                <i class="bi bi-trash"></i> Xóa
            </button>
            <a href="<%=request.getContextPath()%>/admin/QLGiaoVien/Index" class="btn btn-secondary">Hủy</a>
        </div>
    </form>
</main>
