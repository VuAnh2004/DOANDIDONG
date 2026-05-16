<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.bean.QLHocSinh"%>
<%
    QLHocSinh hs = (QLHocSinh) request.getAttribute("hocSinh");

    // Tách address từ DB thành xóm, xã, tỉnh, quốc tịch
    String hamlet = "";
    String ward = "";
    String province = "";
    String country = "Việt Nam";

    if (hs != null && hs.getAddress() != null) {
        String[] parts = hs.getAddress().split(",");
        if (parts.length >= 1) hamlet = parts[0].trim();
        if (parts.length >= 2) ward = parts[1].trim();
        if (parts.length >= 3) province = parts[2].trim();
        if (parts.length >= 4) country = parts[3].trim();
    }
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Sửa thông tin học sinh</h2>
    </div>

    <%-- Thêm input ẩn để đánh dấu xóa ảnh --%>
    <input type="hidden" name="DeleteImage" id="DeleteImageHidden" value="false">

    <form action="<%= request.getContextPath() %>/admin/QLHocSinh/Edit" method="post" enctype="multipart/form-data">
        <input type="hidden" name="ID" value="<%= hs != null ? hs.getID() : "" %>">

        <div class="row">
            <div class="col-md-6 mb-3">
                <label>Mã học sinh</label>
                <input type="text" class="form-control" name="StudentID" value="<%= hs != null ? hs.getStudentID() : "" %>" required>
            </div>

            <div class="col-md-6 mb-3">
                <label>Họ và tên</label>
                <input type="text" class="form-control" name="FullName" value="<%= hs != null ? hs.getFullName() : "" %>" required>
            </div>

            <div class="col-md-6 mb-3">
                <label>Ngày sinh</label>
                <input type="date" class="form-control" name="Birth" value="<%= hs != null && hs.getBirth() != null ? hs.getBirth() : "" %>">
            </div>

            <div class="col-md-6 mb-3">
                <label>Giới tính</label>
                <select class="form-select" name="Gender">
                    <option value="Nam" <%= "Nam".equals(hs != null ? hs.getGender() : "") ? "selected" : "" %>>Nam</option>
                    <option value="Nữ" <%= "Nữ".equals(hs != null ? hs.getGender() : "") ? "selected" : "" %>>Nữ</option>
                    <option value="Khác" <%= "Khác".equals(hs != null ? hs.getGender() : "") ? "selected" : "" %>>Khác</option>
                </select>
            </div>

            <div class="col-md-6 mb-3">
                <label>Tỉnh / Thành phố</label>
                <input type="text" class="form-control" id="provinceInput" value="<%= province %>" autocomplete="off">
                <select id="provinceSelect" size="5" class="form-select mt-1" style="display:none;"></select>
            </div>
            <div class="col-md-6 mb-3">
                <label>Xã / Phường</label>
                <input type="text" class="form-control" id="wardInput" value="<%= ward %>" autocomplete="off">
                <select id="wardSelect" size="5" class="form-select mt-1" style="display:none;"></select>
            </div>
            <div class="col-md-6 mb-3">
                <label>Xóm</label>
                <input type="text" class="form-control" id="hamletInput" value="<%= hamlet %>">
            </div>
            <div class="col-md-6 mb-3">
                <label>Quốc tịch</label>
                <input type="text" class="form-control" id="quocTichInput" value="<%= country %>">
            </div>
            <div class="col-md-12 mb-3">
                <label>Địa chỉ đầy đủ</label>
                <input type="text" class="form-control" id="addressInput" name="Address" readonly value="<%= hs != null ? hs.getAddress() : "" %>">
            </div>

            <div class="col-md-6 mb-3">
                <label>Dân tộc</label>
                <input type="text" class="form-control" name="Nation" value="<%= hs != null ? hs.getNation() : "" %>">
            </div>

            <div class="col-md-6 mb-3">
                <label>Tôn giáo</label>
                <select class="form-select" name="Religion">
                    <option value="Không" <%= "Không".equals(hs != null ? hs.getReligion() : "") ? "selected" : "" %>>Không</option>
                    <option value="Có" <%= "Có".equals(hs != null ? hs.getReligion() : "") ? "selected" : "" %>>Có</option>
                    <option value="Không xác định" <%= "Không xác định".equals(hs != null ? hs.getReligion() : "") ? "selected" : "" %>>Không xác định</option>
                </select>
            </div>

            <div class="col-md-6 mb-3">
                <label>Tình trạng học</label>
                <select class="form-select" name="StatusStudent">
                    <option value="Đang học" <%= "Đang học".equals(hs != null ? hs.getStatusStudent() : "") ? "selected" : "" %>>Đang học</option>
                    <option value="Nghỉ học" <%= "Nghỉ học".equals(hs != null ? hs.getStatusStudent() : "") ? "selected" : "" %>>Nghỉ học</option>
                    <option value="Bảo lưu" <%= "Bảo lưu".equals(hs != null ? hs.getStatusStudent() : "") ? "selected" : "" %>>Bảo lưu</option>
                    <option value="Đã tốt nghiệp" <%= "Đã tốt nghiệp".equals(hs != null ? hs.getStatusStudent() : "") ? "selected" : "" %>>Đã tốt nghiệp</option>
                </select>
            </div>

            <div class="col-md-6 mb-3">
                <label>Số điện thoại</label>
                <input type="tel" class="form-control" name="NumberPhone" value="<%= hs != null ? hs.getNumberPhone() : "" %>">
            </div>

            <div class="col-md-6 mb-3">
                <label>Hình ảnh</label>
                <input type="file" class="form-control" name="Images" id="imageFile" accept="image/*" onchange="previewImage(event)">
                
                <div id="imageContainer" class="mt-2" style="position: relative; display: inline-block;">
                <%
                    String imagePath = (hs != null && hs.getImages() != null && !hs.getImages().isEmpty()) ? 
                                       request.getContextPath() + "/" + hs.getImages() : "";
                    String displayStyle = imagePath.isEmpty() ? "display:none;" : "";
                %>
                    <img id="preview" src="<%= imagePath %>" width="120" style="<%= displayStyle %>">
                    
                    <%-- Nút xóa ảnh --%>
                    <button type="button" id="deleteImageBtn" 
                        class="btn btn-danger btn-sm" 
                        style="position: absolute; top: -10px; right: -10px; height: 25px; width: 25px; border-radius: 50%; padding: 0; line-height: 1; <%= displayStyle %>"
                        onclick="deleteImage()">
                        &times;
                    </button>
                </div>
            </div>

            <div class="col-md-6 mb-3">
                <label><input type="checkbox" name="IsActive" <%= hs != null && hs.isIsActive() ? "checked" : "" %>> Hiển thị</label>
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
        <a href="<%= request.getContextPath() %>/admin/QLHocSinh/Index" class="btn btn-warning">Thoát</a>
    </form>
</main>

<script>
// ... (Phần JS xử lý địa chỉ giữ nguyên) ...

document.addEventListener('DOMContentLoaded', function () {
    const provinceInput = document.getElementById('provinceInput');
    const provinceSelect = document.getElementById('provinceSelect');
    const wardInput = document.getElementById('wardInput');
    const wardSelect = document.getElementById('wardSelect');
    const hamletInput = document.getElementById('hamletInput');
    const quocTichInput = document.getElementById('quocTichInput');
    const addressInput = document.getElementById('addressInput');

    let provinces = [];
    let wards = [];

    fetch('https://provinces.open-api.vn/api/p/')
        .then(res => res.json())
        .then(data => provinces = data);

    function updateAddress() {
        const parts = [];
        if (hamletInput.value.trim()) parts.push(hamletInput.value.trim());
        if (wardInput.value.trim()) parts.push(wardInput.value.trim());
        if (provinceInput.value.trim()) parts.push(provinceInput.value.trim());
        parts.push(quocTichInput.value.trim() || "Việt Nam");
        addressInput.value = parts.join(', ');
    }

    function filter(list, text) {
        return list.filter(i => i.name.toLowerCase().includes(text.toLowerCase()));
    }

    function render(select, items) {
        select.innerHTML = "";
        items.forEach(i => {
            const o = document.createElement('option');
            o.value = i.code;
            o.textContent = i.name;
            select.appendChild(o);
        });
    }

    // Tỉnh
    provinceInput.addEventListener('input', () => {
        const list = filter(provinces, provinceInput.value);
        render(provinceSelect, list);
        provinceSelect.style.display = list.length ? "block" : "none";
    });
    provinceSelect.addEventListener('click', () => {
        const selected = provinceSelect.selectedOptions[0];
        provinceInput.value = selected.textContent;
        provinceSelect.style.display = "none";

        fetch(`https://provinces.open-api.vn/api/p/${selected.value}?depth=2`)
            .then(res => res.json())
            .then(data => wards = data.districts.flatMap(x => x.wards));

        wardInput.value = "";
        updateAddress();
    });

    // Xã / phường
    wardInput.addEventListener('input', () => {
        const list = filter(wards, wardInput.value);
        render(wardSelect, list);
        wardSelect.style.display = list.length ? "block" : "none";
    });
    wardSelect.addEventListener('click', () => {
        const selected = wardSelect.selectedOptions[0];
        wardInput.value = selected.textContent;
        wardSelect.style.display = "none";
        updateAddress();
    });

    [hamletInput, quocTichInput].forEach(i => i.addEventListener('input', updateAddress));
    
    
    // Đặt lại giá trị ban đầu cho DeleteImageHidden khi tải trang
    document.getElementById('DeleteImageHidden').value = "false";
});


function previewImage(event) {
    const img = document.getElementById('preview');
    const btn = document.getElementById('deleteImageBtn');
    const fileInput = document.getElementById('imageFile');
    const deleteHidden = document.getElementById('DeleteImageHidden');
    
    // 1. Hiển thị ảnh mới
    if (event.target.files.length > 0) {
        img.src = URL.createObjectURL(event.target.files[0]);
        img.style.display = "block";
        btn.style.display = "block";
        // 2. Nếu chọn ảnh mới, hủy bỏ lệnh xóa ảnh cũ (nếu có)
        deleteHidden.value = "false";
    } else {
        // Nếu hủy chọn file, kiểm tra xem có ảnh cũ không để hiển thị lại
        // Hoặc có thể giữ nguyên trạng thái trước khi bấm chọn file
    }
}

function deleteImage() {
    const img = document.getElementById('preview');
    const btn = document.getElementById('deleteImageBtn');
    const fileInput = document.getElementById('imageFile');
    const deleteHidden = document.getElementById('DeleteImageHidden');

    // 1. Ẩn ảnh và nút xóa
    img.src = "";
    img.style.display = "none";
    btn.style.display = "none";

    // 2. Reset input file (để không gửi file rỗng)
    fileInput.value = "";
    
    // 3. Đặt giá trị hidden field để thông báo cho Servlet biết cần xóa ảnh cũ
    deleteHidden.value = "true";

   
}
</script>