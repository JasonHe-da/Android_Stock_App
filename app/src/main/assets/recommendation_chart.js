function init_recommend(val){
        const obj = JSON.parse(val);
        var buy = [];
        var sell = [];
        var hold = [];
        var strongBuy  = [];
        var strongSell = [];
        var period  = [];
        var symbol = "";
        for(var i = 0 ; i < obj.length; i ++){

              buy.push(obj[i]["buy"]);
              sell.push(obj[i]["sell"]);
              period.push(obj[i]["period"]);
              hold.push(obj[i]["hold"]);
              strongBuy.push(obj[i]["strongBuy"]);
              strongSell.push(obj[i]["strongSell"]);
              symbol = obj[i]["symbol"];
        }
        for(var i = 0; i < period.length;i++){
            period[i] = period[i].slice(0,-3);
        }
        Highcharts.chart('container_recom',{
            chart: {
                  type: 'column'
              },
              title: {
                  text: 'Recommendation Trends'
              },
              xAxis: {
                  categories: period
              },
              yAxis: {
                  min: 0,
                  title: {
                      text: '#Analysis'
                  },
                  stackLabels: {
                      enabled: true,
                      style: {
                          fontWeight: 'bold',
                          color: ( // theme
                              Highcharts.defaultOptions.title.style &&
                              Highcharts.defaultOptions.title.style.color
                          ) || 'gray'
                      }
                  }
              },
              legend: {

                  verticalAlign: 'bottom'

              },
              tooltip: {
                  headerFormat: '<b>{point.x}</b><br/>',
                  pointFormat: '{series.name}: {point.y}<br/>Total: {point.stackTotal}'
              },
              plotOptions: {
                  column: {
                      stacking: 'normal',
                      dataLabels: {
                          enabled: true
                      }
                  }
              },
              series: [{
                  name: 'Strong Sell',
                  type: 'column',
                  color: '#8b0000',
                  data: strongSell
              }, {
                  name: 'Sell',
                  type: 'column',
                  color: '#FF0000',
                  data: sell
              }, {
                  name: 'Hold',
                  type: 'column',
                  color: '#964B00',
                  data: hold
              },{
                  name: 'Buy',
                  type: 'column',
                  color: '#90EE90',
                  data: buy
              } ,{
                  name: 'Strong Buy',
                  type: 'column',
                  color: '#00FF00',
                  data: strongBuy
              }]
        });
}