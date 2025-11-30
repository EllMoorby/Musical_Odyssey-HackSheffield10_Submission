const map = L.map('map').setView([45, 10], 4);

console.log(tracksData);
data = JSON.parse(tracksData);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);

function createTrackPopup(track) {
    return `
        <div class="card border-dark text-bg-dark">
            <div class="card-body spotifyCard">
                <div>
                    <img src="${track.album.coverImage}" class="card-img pb-2" alt="${track.album.name} Album Cover" width="100%" height="100%">
                </div>
                <span class="text-white trackName">${track.name}</span><br>
                <span class="text-secondary artistName">${Object.values(track.artists).join(', ')}</span>
            </div>
        </div>
    `;
}

async function fetchTrack(artist, trackName) {
    try {
        const response = await fetch(`/api/spotify/track?artist=${encodeURIComponent(artist)}&trackName=${encodeURIComponent(trackName)}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const trackData = await response.json();
        console.log('Raw track data from API:', trackData);
        return trackData;
    } catch (error) {
        console.error('Error fetching track:', error);
        return null;
    }
}

const startMarker = L.marker([lat1, lng1], { riseOnHover: true })
    .addTo(map)
    .bindPopup("<b>Start</b>")
    .openPopup();

const endMarker = L.marker([lat2, lng2], { riseOnHover: true })
    .addTo(map)
    .bindPopup("<b>End</b>")
    .openPopup();

// Optionally, draw a line between start and end
const line = L.polyline([[lat1, lng1], [lat2, lng2]], {
    color:'#ee100f',
    dashArray: '5, 5',
    opacity: 0.8
})
    .addTo(map);

data.forEach(city => {

    city.tracks.forEach(track => {
        const content = createTrackPopup(track);


        var popup = L.popup({
            offset: [2, -50],
            closeButton: false,
            autoClose: false,
            closeOnClick: false,
            className: "spotifyPopup",
        })
            .setLatLng([city.lat, city.lng])
            .setContent(content)
            .openOn(map);

        var marker = L.marker({
            riseOnHover: true,
            riseOffset: 250,
        })
            .setLatLng([city.lat, city.lng])
            .addTo(map);
    })


});
