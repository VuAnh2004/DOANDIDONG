<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="model.bean.QLHocSinh"%>

<%
    QLHocSinh hs = (QLHocSinh) request.getAttribute("hocSinh");
%>

<main id="main" class="main">
    <div class="pagetitle">
        <h2>Thêm mới học sinh</h2>

        <% if (request.getAttribute("Error") != null) { %>
            <div class="alert alert-danger"><%= request.getAttribute("Error") %></div>
        <% } %>

        <% if (request.getAttribute("Success") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("Success") %></div>
        <% } %>
    </div>

    <!-- FORM -->
    <form action="<%= request.getContextPath() %>/admin/QLHocSinh/Create"
          method="post"
          enctype="multipart/form-data">

        <div class="row">

            <!-- Mã học sinh -->
            <div class="col-md-6 mb-3">
                <label>Mã học sinh</label>
                <input type="text" class="form-control"
                       name="StudentID"
                       value="<%= hs != null ? hs.getStudentID() : "" %>"
                       placeholder="Nhập mã học sinh..." required>
            </div>

            <!-- Họ tên -->
            <div class="col-md-6 mb-3">
                <label>Họ và tên</label>
                <input type="text" class="form-control"
                       name="FullName"
                       value="<%= hs != null ? hs.getFullName() : "" %>"
                       placeholder="Nhập họ tên..." required>
            </div>

            <!-- Ngày sinh -->
            <div class="col-md-6 mb-3">
                <label>Ngày sinh</label>
                <input type="date" class="form-control"
                       name="Birth"
                       value="<%= hs != null && hs.getBirth() != null ? hs.getBirth() : "" %>">
            </div>

            <!-- Giới tính -->
            <div class="col-md-6 mb-3">
                <label>Giới tính</label>
                <select class="form-select" name="Gender">
                    <option value="Nam" <%= "Nam".equals(hs != null ? hs.getGender() : "") ? "selected" : "" %>>Nam</option>
                    <option value="Nữ" <%= "Nữ".equals(hs != null ? hs.getGender() : "") ? "selected" : "" %>>Nữ</option>
                    <option value="Khác" <%= "Khác".equals(hs != null ? hs.getGender() : "") ? "selected" : "" %>>Khác</option>
                </select>
            </div>

            <!-- Địa chỉ -->
            <div class="col-md-6 mb-3">
                <label>Tỉnh / Thành phố</label>
                <input type="text" class="form-control" id="provinceInput" placeholder="Nhập tỉnh..." autocomplete="off">
                <select id="provinceSelect" size="5" class="form-select mt-1" style="display:none;"></select>
            </div>

            <div class="col-md-6 mb-3">
                <label>Xã / Phường</label>
                <input type="text" class="form-control" id="wardInput" placeholder="Nhập xã/phường..." autocomplete="off">
                <select id="wardSelect" size="5" class="form-select mt-1" style="display:none;"></select>
            </div>

            <div class="col-md-6 mb-3">
                <label>Xóm</label>
                <input type="text" class="form-control" id="hamletInput" placeholder="Nhập xóm...">
            </div>

            <div class="col-md-6 mb-3">
                <label>Quốc tịch</label>
                <input type="text" class="form-control" id="quocTichInput" value="Việt Nam">
            </div>

            <div class="col-md-12 mb-3">
                <label>Địa chỉ đầy đủ</label>
                <input type="text" class="form-control" id="addressInput" name="Address" readonly>
            </div>

            <!-- Dân tộc -->
            <div class="col-md-6 mb-3">
                <label>Dân tộc</label>
                <input type="text" class="form-control"
                       name="Nation"
                       value="<%= hs != null ? hs.getNation() : "" %>"
                       placeholder="Nhập dân tộc...">
            </div>

            <!-- Tôn giáo -->
            <div class="col-md-6 mb-3">
                <label>Tôn giáo</label>
                <select class="form-select" name="Religion">
                    <option value="Có" <%= "Có".equals(hs != null ? hs.getReligion() : "") ? "selected" : "" %>>Có</option>
                    <option value="Không" <%= "Không".equals(hs != null ? hs.getReligion() : "") ? "selected" : "" %>>Không</option>
                    <option value="Không xác định" <%= "Không xác định".equals(hs != null ? hs.getReligion() : "") ? "selected" : "" %>>Không xác định</option>
                </select>
            </div>

            <!-- Tình trạng -->
            <div class="col-md-6 mb-3">
                <label>Tình trạng học</label>
                <select class="form-select" name="StatusStudent">
                    <option value="Đang học">Đang học</option>
                    <option value="Nghỉ học">Nghỉ học</option>
                    <option value="Bảo lưu">Bảo lưu</option>
                    <option value="Đã tốt nghiệp">Đã tốt nghiệp</option>
                </select>
            </div>

            <!-- Số điện thoại -->
            <div class="col-md-6 mb-3">
                <label>Số điện thoại</label>
                <input type="tel" class="form-control"
                       name="NumberPhone"
                       placeholder="Nhập số điện thoại...">
            </div>

            <!-- Upload Ảnh -->
            <div class="col-md-6 mb-3">
                <label>Hình ảnh</label>
                <input type="file" class="form-control" name="ImageFile" accept="image/*" onchange="previewImage(event)">

                <% if (hs != null && hs.getImages() != null && !hs.getImages().isEmpty()) { %>
                    <img id="preview" src="<%= hs.getImages() %>" width="120" class="mt-2">
                <% } else { %>
                    <img id="preview" src="" width="120" style="display:none;" class="mt-2">
                <% } %>
            </div>

            <!-- IsActive -->
            <div class="col-md-6 mb-3">
                <label><input type="checkbox" name="IsActive" checked> Hiển thị</label>
            </div>

        </div>

        <button type="submit" class="btn btn-primary">Lưu thông tin</button>
        <a href="<%= request.getContextPath() %>/admin/QLHocSinh/Index" class="btn btn-warning">Thoát</a>

    </form>
</main>

<!-- API + Tự động ghép địa chỉ -->
<script>
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
            .then(data => {
                wards = data.districts.flatMap(d => d.wards);
            });

        wardInput.value = "";
        updateAddress();
    });

    // Xã / Phường
    wardInput.addEventListener('input', () => {
        const list = filter(wards, wardInput.value);
        render(wardSelect, list);
        wardSelect.style.display = list.length ? "block" : "none";
    });

    wardSelect.addEventListener('click', () => {
        const selected = wardSelect.selectedOptions[0];
        wardInput.value = selected.textContent;
        wardSelect.style.display = "none";
        updateAddress(); // ✅ cập nhật ngay khi chọn ward
    });

    // Các input khác
    [hamletInput, quocTichInput].forEach(i => i.addEventListener('input', updateAddress));
});
</script>


<!-- Preview ảnh -->
<script>
function previewImage(event) {
    const img = document.getElementById('preview');
    img.src = URL.createObjectURL(event.target.files[0]);
    img.style.display = "block";
}
</script>
