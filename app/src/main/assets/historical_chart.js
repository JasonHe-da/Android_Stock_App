function init(val){
        const obj = JSON.parse(val);
//        document.getElementById('container').innerHTML += ticker;
        var ticker = obj["ticker"];
        var ohlc = [],
        volume = [],
        dataLength = obj["c"].length,
        groupingUnits = [[
            'week',                         // unit name
            [1]                             // allowed multiples
        ], [
            'month',
            [1, 2, 3, 4, 6]
        ]],
        i = 0;
        for (i; i < dataLength; i += 1) {
            let tempTime = new Date(obj["t"][i]*1000);
            let correct_time = tempTime.getTime();
            ohlc.push([
                correct_time,
                obj["o"][i],
                obj["h"][i],
                obj["l"][i],
                obj["c"][i]
            ]);


            volume.push([
                correct_time, // the date
                obj["v"][i] // the volume
            ]);
        }
            Highcharts.stockChart('container', {

                rangeSelector: {
                    selected: 2
                },

                title: {
                    text: ticker + ' Historical'
                },

                subtitle: {
                    text: 'With SMA and Volume by Price technical indicators'
                },

                yAxis: [{
                    startOnTick: false,
                    endOnTick: false,
                    labels: {
                        align: 'right',
                        x: -3
                    },
                    title: {
                        text: 'OHLC'
                    },
                    height: '60%',
                    lineWidth: 2,
                    resize: {
                        enabled: true
                    }
                }, {
                    labels: {
                        align: 'right',
                        x: -3
                    },
                    title: {
                        text: 'Volume'
                    },
                    top: '65%',
                    height: '35%',
                    offset: 0,
                    lineWidth: 2
                }],

                tooltip: {
                    split: true
                },

                plotOptions: {
                    series: {
                        dataGrouping: {
                            units: groupingUnits
                        }
                    }
                },

                series: [{
                    type: 'candlestick',
                    name: ticker,
                    id: ticker,
                    zIndex: 2,
                    data: ohlc
                }, {
                    type: 'column',
                    name: 'Volume',
                    id: 'volume',
                    data: volume,
                    yAxis: 1
                }, {
                    type: 'vbp',
                    linkedTo: ticker,
                    params: {
                        volumeSeriesID: 'volume'
                    },
                    dataLabels: {
                        enabled: false
                    },
                    zoneLines: {
                        enabled: false
                    }
                }, {
                    type: 'sma',
                    linkedTo: ticker,
                    zIndex: 1,
                    marker: {
                        enabled: false
                    }
                }]
            });


    }