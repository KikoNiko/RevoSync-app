let chartElementId;
let myChart;
let jsonData;

const statisticsUrl = 'http://localhost:8080/api/statistics';


fetch(statisticsUrl + '/all-spent-by-month')
.then(function(respone) {
    if(respone.ok == true) {
        return respone.json();
    }
})
.then(function(data) {
    chartElementId = 'byMonthChart';
    jsonData = data;
    createChart(chartElementId, data, 'bar');
});

fetch(statisticsUrl + '/all-spent-by-category')
.then(function(respone) {
    if(respone.ok == true) {
        return respone.json();
    }
})
.then(function(data) {
    chartElementId = 'byCategoryChart';
    jsonData = data;
    createChart(chartElementId, data, 'line');
});


function setCharType(chartType) {
    myChart.destroy();
    createChart(chartElementId, jsonData, chartType);
}


function createChart(chartElementId, data, chartType) {
    const chartElement = document.getElementById(chartElementId);
    
    myChart = new Chart(chartElement, {
        type: chartType,
        data: {
          labels: data.map(row => row[Object.keys(row)[0]]),
          datasets: [{
            label: 'Total expenses',
            data: data.map(row => row.moneySpent),
            borderWidth: 1
          }]
        },
        options: {
          scales: {
            y: {
              beginAtZero: true
            }
          },
          maintainAspectRatio: false
        }
      });
}
  