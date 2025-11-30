const map = L.map('map').setView([45, 10], 4);

const points = [null, null];
const markers = [null, null];
line = null;

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);



map.on('click', async function(ev) {
    const index = points[0] === null ? 0 : 1;

    points[index] = ev.latlng;

    if (markers[index]) {
        map.removeLayer(markers[index]);
    }

    markers[index] = L.marker(points[index]).addTo(map);

    if (points[0] && points[1]) {
        if (line) map.removeLayer(line);
        line = L.polyline(points, {
            color:'#ee100f',
            dashArray: '5, 5',
            opacity: 0.8
        }).addTo(map);
    }

    updateSubmitButton();

})


document.getElementById("clear-button").onclick = function () {
    event.stopPropagation();
    markers.forEach(m => {
        if (m) map.removeLayer(m);
    });

    if (line) {
        map.removeLayer(line);
        line = null;
    }

    points[0] = null;
    points[1] = null;
    markers[0] = null;
    markers[1] = null;

    updateSubmitButton();
};

function updateSubmitButton() {
    document.getElementById("submit-button").disabled = (points[0] == null || points[1] == null);
}

document.getElementById("submit-button").onclick = function () {
    event.stopPropagation();
    document.getElementById('lat1').value = points[0].lat;
    document.getElementById('lng1').value = points[0].lng;
    document.getElementById('lat2').value = points[1].lat;
    document.getElementById('lng2').value = points[1].lng;

    document.getElementById("mapForm").submit();
};



