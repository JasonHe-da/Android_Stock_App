function init_eps(val){
    const obj = JSON.parse(val);
    var estimate_earning = [];
    var actual_earning = [];
    var time_line = [];
    var surprise =[];
    for(var i = 0; i < obj.length; i++){
        if(obj[i]["estimate"] == null){
            estimate_earning.push(0);
        }else{
            estimate_earning.push(obj[i]["estimate"]);
        }
        actual_earning.push(obj[i]["actual"]);
        time_line.push(obj[i]["period"] + "<br>"+ "surprise: "+obj[i]["surprise"]);
    }
    Highcharts.chart('container_eps',{
        chart:{
                type:'line',
              },
              title: {
                text: 'Historical EPS Surprises'
              },
              yAxis: {
                title: {
                    text: 'Quarterly EPS'
                }
              },
              tooltip:{
                shared:true
              },
              xAxis: {
                categories: time_line
              },
              plotOptions:{

              },
              legend: {
                verticalAlign: 'bottom'
              },

            series: [{
                name: 'Estimate',
                type: 'line',
                color: '#000000',
                data: estimate_earning
            }, {
                name: 'Actual',
                type: 'line',
                color: '#0000FF',
                data: actual_earning
            }],

            responsive: {
                rules: [{
                    condition: {
                        maxWidth: 500
                    },
                    chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'
                        }
                    }
                }]
            }
    });


}