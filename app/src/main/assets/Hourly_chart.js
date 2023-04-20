function init_hour(val){
    const obj = JSON.parse(val);
    let hourlyChart = [];
    var going_up = obj["going_up"];
    var ticker = obj["ticker"]
    var color = "";
    if(going_up){
        color = "green";
    }else{
        color = "red"
    }
    for(var i = 0 ; i < obj["c"].length; i++){
        let tempTime = new Date(obj["t"][i]*1000);
        let correct_time = tempTime.getTime();
        hourlyChart.push([correct_time, obj["c"][i]]);
    }

    Highcharts.stockChart('container_hour', {
        title:{
            text: ticker + " Hourly Price Variation"
        },
        xAxis: {
            type: 'datetime',
            minTickInterval: 12
        },
        plotOptions:{

        },
        series: [{
            name: ticker,
            data: hourlyChart,
            color: color,
            type: 'line'
        }]

    });
    Highcharts.setOptions({
          time: {
              useUTC: false
          }
        });

}