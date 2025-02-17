const statisticsUrl = 'http://localhost:8080/api/statistics';


async function fetchChartData(endpoint) {
    try {
        const response = await fetch(statisticsUrl + endpoint);
        if (!response.ok) throw new Error('Network response was not ok');
        return await response.json();
    } catch (error) {
        console.error('Error fetching data:', error);
        return null;
    }
}


async function initCharts() {
    const byMonthData = await fetchChartData('/all-spent-by-month');
    if (byMonthData) createChart('byMonthChart', byMonthData, 'bar');

    const byCategoryData = await fetchChartData('/all-spent-by-category');
    if (byCategoryData) createChart('byCategoryChart', byCategoryData, 'line');
}


function createChart(elementId, data, type) {
    const chartElement = document.getElementById(elementId);
    if (!chartElement) {
        console.error(`Element with ID ${elementId} not found`);
        return;
    }

    if (chartElement.chartInstance) {
        chartElement.chartInstance.destroy();
    }

    const chartInstance = new Chart(chartElement, {
        type: type,
        data: {
            labels: data.map(row => row[Object.keys(row)[0]]),
            datasets: [{
                label: 'Total expenses',
                data: data.map(row => row.moneySpent),
                borderWidth: 1,
            }],
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true,
                },
            },
            maintainAspectRatio: false,
        },
    });

    chartElement.chartInstance = chartInstance;
}

initCharts();
