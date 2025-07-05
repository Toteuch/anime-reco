function openAnimeDetails(animeId) {
    $.ajax({
        type: "GET",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/" + animeId,
        contentType: 'application/json',
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        }
    });
}

function setNotifications(animeId, enable) {
    $.ajax({
        type: "POST",
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/" + animeId + "/notifications?enable=" +
        enable,
        contentType: 'application/json',
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        }
    });
}

function editWatchList(animeId, add) {
    let type = "DELETE";
    if (add) {
        type = "POST";
    }
    $.ajax({
        type: type,
        headers: {"X-CSRF-Token": $('#csrf-token').val()},
        url: window.location.protocol + "//" + window.location.host + "/anime/"+animeId+"/watchlist",
        dataType: 'json',
        success: function(data) {
            if (data.error != null) {
                showErrorModal(data.error);
            } else {
                let html = getAnimeDetailsContent(data.animeDetails);
                $('#animeDetailsModal').html(html);
                $('#animeDetailsModal').modal('show');
            }
        }
    })
}

function getAnimeDetailsContent(animeDetails) {
    let html = `
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-body">
                    <div class="flex d-flex">
                        <img src="${animeDetails.mainPictureMediumUrl}" class="img-wrapper-xl mx-2"/>
                        <div>
                            <h2>${animeDetails.mainTitle}</h2>
                            <p><i>`;
    for (let i = 0; i < animeDetails.alternativeTitles.length; i++) {
        html += `${animeDetails.alternativeTitles[i]}`;
        if (i + 1 < animeDetails.alternativeTitles.length) {
            html += " | ";
        }
    }
    html += `               </i></p>
                            <p>${animeDetails.mediaTypeLabel} | ${animeDetails.numEpisodes} episodes |
                            ${animeDetails.statusLabel}</p>`;
    if (animeDetails.seasonLabel != null
        || animeDetails.startDate != null) {
        html += "<p>";
        let elements = 0;
        if (animeDetails.seasonLabel != null) {
            html += animeDetails.seasonLabel;
            elements++;
        }
        if (animeDetails.startDate != null) {
            if (elements > 0) html += " | ";
            html += new Date(animeDetails.startDate).toLocaleDateString(undefined);
            if (animeDetails.endDate != null) {
                html += " - ";
                html += new Date(animeDetails.endDate).toLocaleDateString(undefined);
            }
        }
        html += "</p>";
    }
    if (animeDetails.genreLabels != null && animeDetails.genreLabels.length > 0) {
        html += `<p>`;
        for (let i = 0; i < animeDetails.genreLabels.length; i++) {
            html += `<span class="badge rounded-pill text-bg-primary mx-1">${animeDetails.genreLabels[i]}</span>`;
        }
        html += `</p>`;
    }
    if (animeDetails.pictureLinks != null && animeDetails.pictureLinks.length > 0) {
        html += `<div class="flex d-flex flex-wrap">`;
            for (let i = 0; i < animeDetails.pictureLinks.length; i++) {
                html += `<img class="img-thumbnail thumbnail" src="${animeDetails.pictureLinks[i]}"/>`;
            }
        html += `</div>`;
    }
    html += `           </div>
                    </div>
                </div>
            <div class="modal-footer d-flex justify-content-between">`;
    if (animeDetails.prequelAnimeId != null) {
        html += `<div><button class="btn btn-primary" type="button"
        onclick="openAnimeDetails(${animeDetails.prequelAnimeId})">Previous</button></div>`;
    } else {
        html += `<div><button class="btn btn-outline-secondary" type="button" disabled>Previous</button></div>`;
    }
    html += `   <div>`;
    if (animeDetails.notificationsEnabled) {
        html += `<button class="btn btn-primary" type="button" onclick="setNotifications(${animeDetails.id}, false)">
                     <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell" viewBox="0 0 16 16">
                        <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2M8 1.918l-.797.161A4 4 0 0 0 4 6c0 .628-.134 2.197-.459 3.742-.16.767-.376 1.566-.663 2.258h10.244c-.287-.692-.502-1.49-.663-2.258C12.134 8.197 12 6.628 12 6a4 4 0 0 0-3.203-3.92zM14.22 12c.223.447.481.801.78 1H1c.299-.199.557-.553.78-1C2.68 10.2 3 6.88 3 6c0-2.42 1.72-4.44 4.005-4.901a1 1 0 1 1 1.99 0A5 5 0 0 1 13 6c0 .88.32 4.2 1.22 6"/>
                      </svg>
                 </button>`;
    } else {
        html += `<button class="btn btn-outline-primary" type="button" onclick="setNotifications(${animeDetails.id}, true)">
                     <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bell" viewBox="0 0 16 16">
                       <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2M8 1.918l-.797.161A4 4 0 0 0 4 6c0 .628-.134 2.197-.459 3.742-.16.767-.376 1.566-.663 2.258h10.244c-.287-.692-.502-1.49-.663-2.258C12.134 8.197 12 6.628 12 6a4 4 0 0 0-3.203-3.92zM14.22 12c.223.447.481.801.78 1H1c.299-.199.557-.553.78-1C2.68 10.2 3 6.88 3 6c0-2.42 1.72-4.44 4.005-4.901a1 1 0 1 1 1.99 0A5 5 0 0 1 13 6c0 .88.32 4.2 1.22 6"/>
                     </svg>
                 </button>`;
    }
    if (animeDetails.inWatchlist == true) {
        html += `<button class="btn btn-primary mx-2" type="button" onclick="editWatchList(${animeDetails.id}, false);">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmark" viewBox="0 0 16 16">
                      <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v13.5a.5.5 0 0 1-.777.416L8 13.101l-5.223 2.815A.5.5 0 0 1 2 15.5zm2-1a1 1 0 0 0-1 1v12.566l4.723-2.482a.5.5 0 0 1 .554 0L13 14.566V2a1 1 0 0 0-1-1z"/>
                    </svg>
                 </button>`;
    } else if (animeDetails.addableToWatchlist == true) {
        html += `
            <button class="btn btn-outline-primary mx-2" type="button"
                onclick="editWatchList(${animeDetails.id}, true);">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmark" viewBox="0 0 16 16">
                    <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v13.5a.5.5 0 0 1-.777.416L8 13.101l-5.223 2.815A.5.5 0 0 1 2 15.5zm2-1a1 1 0 0 0-1 1v12.566l4.723-2.482a.5.5 0 0 1 .554 0L13 14.566V2a1 1 0 0 0-1-1z"/>
                </svg>
            </button>`;
    } else {
        html += `
            <button class="btn btn-outline-primary mx-2" type="button" disabled>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-bookmark" viewBox="0 0 16 16">
                    <path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v13.5a.5.5 0 0 1-.777.416L8 13.101l-5.223 2.815A.5.5 0 0 1 2 15.5zm2-1a1 1 0 0 0-1 1v12.566l4.723-2.482a.5.5 0 0 1 .554 0L13 14.566V2a1 1 0 0 0-1-1z"/>
                </svg>
            </button>`;
    }
    html += `<button class="btn btn-secondary" data-bs-dismiss="modal" type="button">Close</button>
            </div>`;
    if (animeDetails.sequelAnimeId != null) {
        html += `<div><button class="btn btn-primary" type="button"
        onclick="openAnimeDetails(${animeDetails.sequelAnimeId})">Next</button></div>`;
    } else {
        html += `<div><button class="btn btn-outline-secondary" type="button" disabled>Next</button></div>`;
    }
    html += `</div>
        </div>
    `;
    return html;
}


