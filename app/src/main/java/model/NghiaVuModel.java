package model;

import java.util.List;

public class NghiaVuModel {
    private double TotalDebt;
    private List<NghiaVuItem> Items;

    public double getTotalDebt() { return TotalDebt; }
    public List<NghiaVuItem> getItems() { return Items; }

    public static class NghiaVuItem {
        private int TransactionId;
        private String NoiDung;
        private String HocKy;
        private double SoTienPhaiNop;
        private double DaThanhToan;
        private double ConNo;
        private String NgayPhatSinh;
        private String Loai;

        public int getTransactionId() { return TransactionId; }
        public String getNoiDung() { return NoiDung; }
        public String getHocKy() { return HocKy; }
        public double getSoTienPhaiNop() { return SoTienPhaiNop; }
        public double getDaThanhToan() { return DaThanhToan; }
        public double getConNo() { return ConNo; }
        public String getNgayPhatSinh() { return NgayPhatSinh; }
        public String getLoai() { return Loai; }
    }
}