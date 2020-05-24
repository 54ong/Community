package com.nowcoder.community.entity;

/*
封装分类
 */
public class Page {

    //当前页码
    private int current=1;

    private int limit=10;

    private int rows;
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1)
            this.current = current;

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1 && limit<=100)
            this.limit = limit;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0)
            this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getOffset()
    {
        return current*limit-limit;//current is current page number
    }

    public int getTotal()
    {
        if(rows%limit==0) return rows/limit;
        else return  rows/limit+1;
    }

    public int getFrom()
    {
        int from=current-2;
        if(from<1) return 1;
        else return from;
    }

    public int getTo()
    {
        int to=current+2;
        if(to>100) return getTotal();
        else return to;
    }
}
