package com.example.admin.biojima;

/**
 * Created by adslbna2 on 15. 10. 25..
 */
class ResultData {

    private String Name;
    private String DetailInfo;
    private String Pop;

    public ResultData(String _Name,String pop ,String detailInfo){
        this.Name = _Name;
        this.DetailInfo = detailInfo;
        this.Pop = pop;
    }

    public String getName() {
        return Name;
    }

    public String getDetailInfo() {
        return DetailInfo;
    }
    public String getPopInfo() {
        return Pop;
    }

}