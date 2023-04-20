package com.example.csci_571_hw9_nihe;

public class Quote {
    public Double current_price;
    public Double change;
    public Double percentage_change;
    public Double high_price;
    public Double low_price;
    public Double open_price;
    public Double previous_close;
    public int last_stock_time;

    public Quote() {
    }

//    @Override
//    public String toString() {
//        return "Quote{" +
//                "current_price=" + current_price +
//                ", change=" + change +
//                ", percentage_change=" + percentage_change +
//                ", high_price=" + high_price +
//                ", low_price=" + low_price +
//                ", open_price=" + open_price +
//                ", previous_close=" + previous_close +
//                ", last_stock_time=" + last_stock_time +
//                '}';
//    }

    public Quote(Double current_price,
                 Double change,
                 Double percentage_change,
                 Double high_price,
                 Double low_price,
                 Double open_price,
                 Double previous_close,
                 int last_stock_time) {
        this.current_price = current_price;
        this.change = change;
        this.percentage_change = percentage_change;
        this.high_price = high_price;
        this.low_price = low_price;
        this.open_price = open_price;
        this.previous_close = previous_close;
        this.last_stock_time = last_stock_time;
    }

}
