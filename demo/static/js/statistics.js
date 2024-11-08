const ctx = document.getElementById('myChart');

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
    jsonData = data;
    createChart(data, 'bar');
});


function setCharType(chartType) {
    myChart.destroy();
    createChart(jsonData, chartType);
}


function createChart(data, chartType) {
    
    myChart = new Chart(ctx, {
        type: chartType,
        data: {
          labels: data.map(row => row.month),
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
  